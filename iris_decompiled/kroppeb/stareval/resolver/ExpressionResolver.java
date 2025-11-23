/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package kroppeb.stareval.resolver;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.element.token.IdToken;
import kroppeb.stareval.element.token.NumberToken;
import kroppeb.stareval.element.tree.AccessExpressionElement;
import kroppeb.stareval.element.tree.BinaryExpressionElement;
import kroppeb.stareval.element.tree.FunctionCall;
import kroppeb.stareval.element.tree.UnaryExpressionElement;
import kroppeb.stareval.expression.CallExpression;
import kroppeb.stareval.expression.ConstantExpression;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionResolver;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import kroppeb.stareval.function.TypedFunction;

public class ExpressionResolver {
    private final FunctionResolver functionResolver;
    private final Function<String, Type> variableTypeMap;
    private final boolean enableDebugging;
    private final Map<String, ConstantExpression> numbers = new Object2ObjectOpenHashMap();
    private List<String> logs;

    public ExpressionResolver(FunctionResolver functionResolver, Function<String, Type> variableTypeMap) {
        this(functionResolver, variableTypeMap, false);
    }

    public ExpressionResolver(FunctionResolver functionResolver, Function<String, Type> variableTypeMap, boolean enableDebugging) {
        this.functionResolver = functionResolver;
        this.variableTypeMap = variableTypeMap;
        this.enableDebugging = enableDebugging;
    }

    public Expression resolveExpression(Type targetType, ExpressionElement expression) {
        this.clearLogs();
        Expression result = this.resolveExpressionInternal(targetType, expression, true, true);
        if (result != null) {
            return result;
        }
        throw new RuntimeException("Couldn't resolve: \n" + String.join((CharSequence)"\n", this.extractLogs()));
    }

    Expression resolveCallExpressionInternal(Type targetType, String name, List<? extends ExpressionElement> inner, boolean implicit) {
        int innerLength = inner.size();
        CallExpression result = null;
        TypedFunction resultFunction = null;
        block0: for (TypedFunction typedFunction : this.functionResolver.resolve(name, targetType)) {
            TypedFunction.Parameter[] paramTypes = typedFunction.getParameters();
            if (paramTypes.length != innerLength) continue;
            Expression[] params = new Expression[innerLength];
            for (int i = 0; i < innerLength; ++i) {
                ExpressionElement paramExpression = inner.get(i);
                TypedFunction.Parameter param = paramTypes[i];
                if (param.constant() && !(paramExpression instanceof NumberToken)) continue block0;
                Expression expression = this.resolveExpressionInternal(param.type(), paramExpression, !implicit || innerLength > 1, implicit);
                if (expression == null) continue block0;
                params[i] = expression;
            }
            if (result != null && typedFunction.priority() == resultFunction.priority()) {
                throw new RuntimeException("Ambiguity, \n\told: " + TypedFunction.format(resultFunction, "") + "\n\tnew: " + TypedFunction.format(typedFunction, ""));
            }
            if (resultFunction != null && typedFunction.priority() < resultFunction.priority()) continue;
            result = new CallExpression(typedFunction, params);
            resultFunction = typedFunction;
        }
        return result;
    }

    private Expression resolveCallExpression(Type targetType, String name, List<? extends ExpressionElement> inner, boolean allowNonImplicit, boolean allowImplicit) {
        this.log("[DEBUG] resolving function %s with args %s to type %s", name, inner, targetType);
        Expression result = null;
        if (allowNonImplicit) {
            result = this.resolveCallExpressionInternal(targetType, name, inner, false);
        }
        if (result != null) {
            this.log("[DEBUG] resolved function %s with args %s to type %s directly", name, inner, targetType);
            return result;
        }
        if (!allowImplicit) {
            this.log("[DEBUG] Failed to resolve function %s with args %s to type %s directly", name, inner, targetType);
            return null;
        }
        List<? extends TypedFunction> casts = this.functionResolver.resolve("<cast>", targetType);
        for (TypedFunction typedFunction : casts) {
            Expression u = this.resolveCallExpression(typedFunction.getParameters()[0].type(), name, inner, true, true);
            if (u == null) continue;
            if (result != null) {
                throw new RuntimeException("Ambiguity");
            }
            result = new CallExpression(typedFunction, new Expression[]{u});
        }
        if (result != null) {
            this.log("[DEBUG] resolved function %s with args %s to type %s using only final cast", name, inner, targetType);
            return result;
        }
        result = this.resolveCallExpressionInternal(targetType, name, inner, true);
        if (result != null) {
            this.log("[DEBUG] resolved function %s with args %s to type %s using implicit inner casts", name, inner, targetType);
        } else {
            this.log("[DEBUG] failed to resolve function %s with args %s to type %s", name, inner, targetType);
        }
        return result;
    }

    public List<String> extractLogs() {
        List<String> old = this.logs;
        this.clearLogs();
        return old;
    }

    public void clearLogs() {
        this.logs = new ArrayList<String>();
    }

    private void log(String str) {
        if (this.enableDebugging) {
            this.logs.add(str);
        }
    }

    private void log(String str, Object ... args) {
        if (this.enableDebugging) {
            this.logs.add(String.format(str, args));
        }
    }

    private void log(Supplier<String> str) {
        if (this.enableDebugging) {
            this.log(str.get());
        }
    }

    private Expression resolveExpressionInternal(Type targetType, ExpressionElement expression, boolean allowNonImplicit, boolean allowImplicit) {
        Type innerType;
        Expression castable;
        this.log("[DEBUG] resolving %s to type %s (%d%d)", expression, targetType, allowNonImplicit ? 1 : 0, allowImplicit ? 1 : 0);
        if (expression instanceof UnaryExpressionElement) {
            UnaryExpressionElement token = (UnaryExpressionElement)expression;
            return this.resolveCallExpression(targetType, token.getOp().getName(), Collections.singletonList(token.getInner()), allowNonImplicit, allowImplicit);
        }
        if (expression instanceof BinaryExpressionElement) {
            BinaryExpressionElement token = (BinaryExpressionElement)expression;
            return this.resolveCallExpression(targetType, token.getOp().getName(), Arrays.asList(token.getLeft(), token.getRight()), allowNonImplicit, allowImplicit);
        }
        if (expression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall)expression;
            return this.resolveCallExpression(targetType, functionCall.getId(), functionCall.getArgs(), allowNonImplicit, allowImplicit);
        }
        if (expression instanceof AccessExpressionElement) {
            AccessExpressionElement token = (AccessExpressionElement)expression;
            return this.resolveCallExpression(targetType, "<access$" + token.getIndex() + ">", Collections.singletonList(token.getBase()), allowNonImplicit, allowImplicit);
        }
        if (expression instanceof NumberToken) {
            NumberToken token = (NumberToken)expression;
            ConstantExpression exp = this.resolveNumber(token.getNumber());
            if (exp.getType().equals(targetType)) {
                this.log("[DEBUG] resolved constant %s to type %s", token.getNumber(), targetType);
                return exp;
            }
            if (!allowImplicit) {
                this.log("[DEBUG] failed to resolve constant %s (of type %s) to type %s without implicit casts", token.getNumber(), exp.getType(), targetType);
                return null;
            }
            this.log("[DEBUG] trying implicit casts to resolve constant %s (of type %s) to type %s", token.getNumber(), exp.getType(), targetType);
            castable = exp;
            innerType = exp.getType();
        } else if (expression instanceof IdToken) {
            IdToken token = (IdToken)expression;
            final String name = token.getId();
            Type type = this.variableTypeMap.apply(name);
            if (type == null) {
                throw new RuntimeException("Unknown variable: " + name);
            }
            if (type.equals(targetType)) {
                this.log("[DEBUG] resolved variable %s to type %s", name, targetType);
                return new VariableExpression(){

                    @Override
                    public void evaluateTo(FunctionContext c, FunctionReturn r) {
                        c.getVariable(name).evaluateTo(c, r);
                    }

                    @Override
                    public Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
                        return context.hasVariable(name) ? context.getVariable(name) : this;
                    }
                };
            }
            if (!allowImplicit) {
                this.log("[DEBUG] failed to resolve variable %s (of type %s) to type %s without implicit casts", name, type, targetType);
                return null;
            }
            castable = new VariableExpression(){

                @Override
                public void evaluateTo(FunctionContext c, FunctionReturn r) {
                    c.getVariable(name).evaluateTo(c, r);
                }

                @Override
                public Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
                    return context.hasVariable(name) ? context.getVariable(name) : this;
                }
            };
            innerType = type;
        } else {
            throw new RuntimeException("unexpected token: " + expression.toString());
        }
        List<? extends TypedFunction> casts = this.functionResolver.resolve("<cast>", targetType);
        for (TypedFunction typedFunction : casts) {
            if (!typedFunction.getParameters()[0].type().equals(innerType)) continue;
            this.log("[DEBUG] resolved %s to type %s using implicit casts", expression, targetType);
            return new CallExpression(typedFunction, new Expression[]{castable});
        }
        this.log("[DEBUG] failed to resolved %s to type %s, even using implicit casts", expression, targetType);
        return null;
    }

    private ConstantExpression resolveNumber(String s) {
        return this.numbers.computeIfAbsent(s, str -> {
            try {
                int val;
                if (str.length() >= 2 && str.charAt(0) == '0') {
                    switch (str.charAt(1)) {
                        case 'b': {
                            val = Integer.parseInt(str.substring(2), 2);
                            break;
                        }
                        case 'x': {
                            val = Integer.parseInt(str.substring(2), 16);
                            break;
                        }
                        default: {
                            val = Integer.parseInt(str.substring(1), 8);
                            break;
                        }
                    }
                } else {
                    val = Integer.parseInt(str);
                }
                return new ConstantExpression(Type.Int){

                    @Override
                    public void evaluateTo(FunctionContext context, FunctionReturn functionReturn) {
                        functionReturn.intReturn = val;
                    }
                };
            }
            catch (NumberFormatException ex) {
                NumberFormatException p = ex;
                try {
                    final float res = Float.parseFloat(str);
                    return new ConstantExpression(Type.Float){

                        @Override
                        public void evaluateTo(FunctionContext context, FunctionReturn functionReturn) {
                            functionReturn.floatReturn = res;
                        }
                    };
                }
                catch (NumberFormatException ex2) {
                    RuntimeException exception = new RuntimeException("Illegal number: " + str, ex2);
                    exception.addSuppressed(p);
                    throw exception;
                }
            }
        });
    }
}

