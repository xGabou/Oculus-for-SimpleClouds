/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package net.irisshaders.iris.uniforms.custom;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import kroppeb.stareval.parser.Parser;
import kroppeb.stareval.resolver.ExpressionResolver;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.uniform.LocationalUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.parsing.IrisFunctions;
import net.irisshaders.iris.parsing.IrisOptions;
import net.irisshaders.iris.parsing.VectorType;
import net.irisshaders.iris.uniforms.custom.CustomUniformFixedInputUniformsHolder;
import net.irisshaders.iris.uniforms.custom.cached.CachedUniform;

public class CustomUniforms
implements FunctionContext {
    private final Map<String, CachedUniform> variables = new Object2ObjectLinkedOpenHashMap();
    private final Map<String, Expression> variablesExpressions = new Object2ObjectLinkedOpenHashMap();
    private final CustomUniformFixedInputUniformsHolder inputHolder;
    private final List<CachedUniform> uniforms = new ArrayList<CachedUniform>();
    private final List<CachedUniform> uniformOrder;
    private final Map<Object, Object2IntMap<CachedUniform>> locationMap = new Object2ObjectOpenHashMap();
    private final Map<CachedUniform, List<CachedUniform>> dependsOn;
    private final Map<CachedUniform, List<CachedUniform>> requiredBy;

    private CustomUniforms(CustomUniformFixedInputUniformsHolder inputHolder, Map<String, Builder.Variable> variables) {
        this.inputHolder = inputHolder;
        ExpressionResolver resolver = new ExpressionResolver(IrisFunctions.functions, name -> {
            Type type = this.inputHolder.getType((String)name);
            if (type != null) {
                return type;
            }
            Builder.Variable variable = (Builder.Variable)((Object)((Object)((Object)variables.get(name))));
            if (variable != null) {
                return variable.type;
            }
            return null;
        }, true);
        for (Builder.Variable variable : variables.values()) {
            try {
                Expression expression = resolver.resolveExpression(variable.type, variable.expression);
                CachedUniform cachedUniform = CachedUniform.forExpression(variable.name, variable.type, expression, this);
                this.addVariable(expression, cachedUniform);
                if (!variable.uniform) continue;
                this.uniforms.add(cachedUniform);
            }
            catch (Exception e) {
                Iris.logger.warn("Failed to resolve uniform " + variable.name + ", reason: " + e.getMessage() + " ( = " + variable.expression + ")", e);
            }
        }
        this.dependsOn = new Object2ObjectOpenHashMap();
        this.requiredBy = new Object2ObjectOpenHashMap();
        Object2IntOpenHashMap dependsOnCount = new Object2IntOpenHashMap();
        for (CachedUniform input : this.inputHolder.getAll()) {
            this.requiredBy.put(input, (List<CachedUniform>)new ObjectArrayList());
        }
        for (CachedUniform input : this.variables.values()) {
            this.requiredBy.put(input, (List<CachedUniform>)new ObjectArrayList());
        }
        FunctionReturn functionReturn = new FunctionReturn();
        ObjectOpenHashSet requires = new ObjectOpenHashSet();
        ObjectOpenHashSet brokenUniforms = new ObjectOpenHashSet();
        for (Map.Entry<String, Expression> entry2 : this.variablesExpressions.entrySet()) {
            requires.clear();
            entry2.getValue().listVariables((Collection<? super VariableExpression>)requires);
            if (requires.isEmpty()) continue;
            CachedUniform uniform = this.variables.get(entry2.getKey());
            ArrayList<CachedUniform> dependencies = new ArrayList<CachedUniform>();
            for (VariableExpression v : requires) {
                Expression evaluated = v.partialEval(this, functionReturn);
                if (evaluated instanceof CachedUniform) {
                    dependencies.add((CachedUniform)evaluated);
                    continue;
                }
                brokenUniforms.add(uniform);
            }
            if (dependencies.isEmpty()) continue;
            this.dependsOn.put(uniform, dependencies);
            dependsOnCount.put((Object)uniform, dependencies.size());
            for (CachedUniform dependency : dependencies) {
                this.requiredBy.get(dependency).add(uniform);
            }
        }
        ObjectArrayList ordered = new ObjectArrayList();
        ObjectArrayList free = new ObjectArrayList();
        for (CachedUniform entry3 : this.requiredBy.keySet()) {
            if (dependsOnCount.containsKey((Object)entry3)) continue;
            free.add(entry3);
        }
        while (!free.isEmpty()) {
            CachedUniform pop = (CachedUniform)free.remove(free.size() - 1);
            if (!brokenUniforms.contains(pop)) {
                ordered.add(pop);
            } else {
                brokenUniforms.addAll((Collection)this.requiredBy.get(pop));
            }
            for (CachedUniform dependent : this.requiredBy.get(pop)) {
                int count = dependsOnCount.mergeInt((Object)dependent, -1, Integer::sum);
                assert (count >= 0);
                if (count != 0) continue;
                free.add(dependent);
                dependsOnCount.removeInt((Object)dependent);
            }
        }
        if (!brokenUniforms.isEmpty()) {
            Iris.logger.warn("The following uniforms won't work, either because they are broken, or reference a broken uniform: \n" + brokenUniforms.stream().map(CachedUniform::getName).collect(Collectors.joining(", ")));
        }
        if (!dependsOnCount.isEmpty()) {
            throw new IllegalStateException("Circular reference detected between: " + dependsOnCount.object2IntEntrySet().stream().map(entry -> ((CachedUniform)entry.getKey()).getName() + " (" + entry.getIntValue() + ")").collect(Collectors.joining(", ")));
        }
        this.uniformOrder = ordered;
    }

    private void addVariable(Expression expression, CachedUniform uniform) throws Exception {
        String name = uniform.getName();
        if (this.variables.containsKey(name)) {
            throw new Exception("Duplicated variable: " + name);
        }
        if (this.inputHolder.containsKey(name)) {
            throw new Exception("Variable shadows build in uniform: " + name);
        }
        this.variables.put(name, uniform);
        this.variablesExpressions.put(name, expression);
    }

    public void assignTo(LocationalUniformHolder targetHolder) {
        Object2IntOpenHashMap locations = new Object2IntOpenHashMap();
        for (CachedUniform uniform : this.uniformOrder) {
            try {
                OptionalInt location = targetHolder.location(uniform.getName(), Type.convert(uniform.getType()));
                if (!location.isPresent()) continue;
                locations.put((Object)uniform, location.getAsInt());
            }
            catch (Exception e) {
                throw new RuntimeException(uniform.getName(), e);
            }
        }
        this.locationMap.put(targetHolder, (Object2IntMap<CachedUniform>)locations);
    }

    public void mapholderToPass(LocationalUniformHolder holder, Object pass) {
        this.locationMap.put(pass, this.locationMap.remove(holder));
    }

    public void update() {
        for (CachedUniform value : this.uniformOrder) {
            value.update();
        }
    }

    public void push(Object pass) {
        Object2IntMap<CachedUniform> uniforms = this.locationMap.get(pass);
        if (uniforms != null) {
            uniforms.forEach(CachedUniform::pushIfChanged);
        }
    }

    /*
     * WARNING - void declaration
     */
    public void optimise() {
        void var3_7;
        Object2IntOpenHashMap dependedByCount = new Object2IntOpenHashMap();
        for (List<CachedUniform> list : this.dependsOn.values()) {
            for (CachedUniform dependency : list) {
                dependedByCount.mergeInt((Object)dependency, 1, Integer::sum);
            }
        }
        for (Object2IntMap object2IntMap : this.locationMap.values()) {
            for (CachedUniform cachedUniform : object2IntMap.keySet()) {
                dependedByCount.mergeInt((Object)cachedUniform, 1, Integer::sum);
            }
        }
        ObjectOpenHashSet unused = new ObjectOpenHashSet();
        int n = this.uniformOrder.size() - 1;
        while (var3_7 >= 0) {
            CachedUniform uniform = this.uniformOrder.get((int)var3_7);
            if (!dependedByCount.containsKey((Object)uniform)) {
                unused.add(uniform);
                List<CachedUniform> dependencies = this.dependsOn.get(uniform);
                if (dependencies != null) {
                    for (CachedUniform dependency : dependencies) {
                        dependedByCount.computeIntIfPresent((Object)dependency, (key, value) -> value - 1);
                    }
                }
            }
            --var3_7;
        }
        this.uniformOrder.removeAll((Collection<?>)unused);
    }

    @Override
    public boolean hasVariable(String name) {
        return this.inputHolder.containsKey(name) || this.variables.containsKey(name);
    }

    @Override
    public Expression getVariable(String name) {
        CachedUniform inputUniform = this.inputHolder.getUniform(name);
        if (inputUniform != null) {
            return inputUniform;
        }
        CachedUniform customUniform = this.variables.get(name);
        if (customUniform != null) {
            return customUniform;
        }
        throw new RuntimeException("Unknown variable: " + name);
    }

    public static class Builder {
        private static final Map<String, Type> types = new ImmutableMap.Builder().put((Object)"bool", (Object)Type.Boolean).put((Object)"float", (Object)Type.Float).put((Object)"int", (Object)Type.Int).put((Object)"vec2", VectorType.VEC2).put((Object)"vec3", VectorType.VEC3).put((Object)"vec4", VectorType.VEC4).build();
        Map<String, Variable> variables = new Object2ObjectLinkedOpenHashMap();

        public void addVariable(String type, String name, String expression, boolean isUniform) {
            if (this.variables.containsKey(name)) {
                Iris.logger.warn("Ignoring duplicated custom uniform name: " + name);
                return;
            }
            Type parsedType = types.get(type);
            if (parsedType == null) {
                Iris.logger.warn("Ignoring invalid uniform type: " + type + " of " + name);
                return;
            }
            try {
                ExpressionElement ast = Parser.parse(expression, IrisOptions.options);
                this.variables.put(name, new Variable(parsedType, name, ast, isUniform));
            }
            catch (Exception e) {
                Iris.logger.warn("Failed to parse custom variable/uniform " + name + " with expression " + expression, e);
            }
        }

        public CustomUniforms build(CustomUniformFixedInputUniformsHolder inputHolder) {
            Iris.logger.info("Starting custom uniform resolving");
            return new CustomUniforms(inputHolder, this.variables);
        }

        @SafeVarargs
        public final CustomUniforms build(Consumer<UniformHolder> ... uniforms) {
            CustomUniformFixedInputUniformsHolder.Builder inputs = new CustomUniformFixedInputUniformsHolder.Builder();
            for (Consumer<UniformHolder> uniform : uniforms) {
                uniform.accept(inputs);
            }
            return this.build(inputs.build());
        }

        private record Variable(Type type, String name, ExpressionElement expression, boolean uniform) {
        }
    }
}
