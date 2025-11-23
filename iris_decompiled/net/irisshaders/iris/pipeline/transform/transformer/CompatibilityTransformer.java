/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.irisshaders.iris.pipeline.transform.transformer;

import io.github.douira.glsl_transformer.ast.data.ChildNodeList;
import io.github.douira.glsl_transformer.ast.data.ProxyArrayList;
import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.abstract_node.ASTNode;
import io.github.douira.glsl_transformer.ast.node.declaration.DeclarationMember;
import io.github.douira.glsl_transformer.ast.node.declaration.FunctionParameter;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.node.expression.LiteralExpression;
import io.github.douira.glsl_transformer.ast.node.expression.ReferenceExpression;
import io.github.douira.glsl_transformer.ast.node.expression.unary.FunctionCallExpression;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.EmptyDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.FunctionDefinition;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.NamedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.specifier.ArraySpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinNumericTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.FunctionPrototype;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructDeclarator;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructMember;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.match.AutoHintedMatcher;
import io.github.douira.glsl_transformer.ast.query.match.Matcher;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.ast.transform.Template;
import io.github.douira.glsl_transformer.ast.transform.TransformationException;
import io.github.douira.glsl_transformer.parser.ParseShape;
import io.github.douira.glsl_transformer.util.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompatibilityTransformer {
    private static final Logger LOGGER = LogManager.getLogger(CompatibilityTransformer.class);
    private static final AutoHintedMatcher<Expression> sildursWaterFract = new AutoHintedMatcher<Expression>("fract(worldpos.y + 0.001)", ParseShape.EXPRESSION);
    private static final ShaderType[] pipeline = new ShaderType[]{ShaderType.VERTEX, ShaderType.TESSELATION_CONTROL, ShaderType.TESSELATION_EVAL, ShaderType.GEOMETRY, ShaderType.FRAGMENT};
    private static final Matcher<ExternalDeclaration> outDeclarationMatcher = new DeclarationMatcher(StorageQualifier.StorageType.OUT);
    private static final Matcher<ExternalDeclaration> inDeclarationMatcher = new DeclarationMatcher(StorageQualifier.StorageType.IN);
    private static final String tagPrefix = "iris_template_";
    private static final Template<ExternalDeclaration> declarationTemplate = Template.withExternalDeclaration("out __type __name;");
    private static final Template<Statement> initTemplate = Template.withStatement("__decl = __value;");
    private static final Template<ExternalDeclaration> variableTemplate = Template.withExternalDeclaration("__type __internalDecl;");
    private static final Template<Statement> statementTemplate = Template.withStatement("__oldDecl = vec3(__internalDecl);");
    private static final Template<Statement> statementTemplateVector = Template.withStatement("__oldDecl = vec3(__internalDecl, vec4(0));");
    private static final Matcher<ExternalDeclaration> nonLayoutOutDeclarationMatcher = new Matcher<ExternalDeclaration>("out float name;", ParseShape.EXTERNAL_DECLARATION){
        {
            this.markClassWildcard("qualifier", (ASTNode)((Object)((ExternalDeclaration)this.pattern).getRoot().nodeIndex.getUnique(TypeQualifier.class)));
            this.markClassWildcard("type", (ASTNode)((Object)((ExternalDeclaration)this.pattern).getRoot().nodeIndex.getUnique(BuiltinNumericTypeSpecifier.class)));
            this.markClassWildcard("name*", ((Identifier)((ExternalDeclaration)this.pattern).getRoot().identifierIndex.getUnique("name")).getAncestor(DeclarationMember.class));
        }

        @Override
        public boolean matchesExtract(ExternalDeclaration tree) {
            boolean result = super.matchesExtract(tree);
            if (!result) {
                return false;
            }
            TypeQualifier qualifier = this.getNodeMatch("qualifier", TypeQualifier.class);
            boolean hasOutQualifier = false;
            for (TypeQualifierPart part : qualifier.getParts()) {
                if (part instanceof StorageQualifier) {
                    StorageQualifier storageQualifier = (StorageQualifier)part;
                    if (storageQualifier.storageType != StorageQualifier.StorageType.OUT) continue;
                    hasOutQualifier = true;
                    continue;
                }
                if (!(part instanceof LayoutQualifier)) continue;
                return false;
            }
            return hasOutQualifier;
        }
    };
    private static final Template<ExternalDeclaration> layoutedOutDeclarationTemplate = Template.withExternalDeclaration("out __type __name;");
    private static final String attachTargetPrefix = "outColor";
    private static final List<String> reservedWords = List.of((Object)"texture");

    private static StorageQualifier getConstQualifier(TypeQualifier qualifier) {
        if (qualifier == null) {
            return null;
        }
        for (TypeQualifierPart constQualifier : qualifier.getChildren()) {
            if (!(constQualifier instanceof StorageQualifier)) continue;
            StorageQualifier storageQualifier = (StorageQualifier)constQualifier;
            if (storageQualifier.storageType != StorageQualifier.StorageType.CONST) continue;
            return storageQualifier;
        }
        return null;
    }

    public static void transformEach(ASTParser t, TranslationUnit tree, Root root, Parameters parameters) {
        boolean emptyDeclarationHit;
        if (parameters.type == PatchShaderType.VERTEX && root.replaceExpressionMatches(t, sildursWaterFract, "fract(worldpos.y + 0.01)")) {
            Iris.logger.warn("Patched fract(worldpos.y + 0.001) to fract(worldpos.y + 0.01) to fix waving water disconnecting from other water blocks; See https://github.com/IrisShaders/Iris/issues/509");
        }
        HashMap constFunctions = new HashMap();
        HashSet<String> processingSet = new HashSet<String>();
        LinkedList<FunctionDefinition> unusedFunctions = new LinkedList<FunctionDefinition>();
        for (FunctionDefinition functionDefinition : root.nodeIndex.get(FunctionDefinition.class)) {
            FunctionPrototype prototype = functionDefinition.getFunctionPrototype();
            String functionName = prototype.getName().getName();
            if (!functionName.equals("main") && root.identifierIndex.getStream(functionName).count() <= 1L) {
                unusedFunctions.add(functionDefinition);
                continue;
            }
            if (((ArrayList)prototype.getChildren()).isEmpty()) continue;
            HashSet<String> hashSet = new HashSet<String>(((ArrayList)prototype.getChildren()).size());
            for (FunctionParameter parameter : prototype.getChildren()) {
                if (CompatibilityTransformer.getConstQualifier(parameter.getType().getTypeQualifier()) == null) continue;
                String name = parameter.getName().getName();
                hashSet.add(name);
                processingSet.add(name);
            }
            if (hashSet.isEmpty()) continue;
            constFunctions.put(functionDefinition, hashSet);
        }
        if (!Iris.getIrisConfig().areDebugOptionsEnabled()) {
            for (FunctionDefinition functionDefinition : unusedFunctions) {
                functionDefinition.detachAndDelete();
            }
        }
        boolean constDeclarationHit = false;
        ArrayDeque<String> arrayDeque = new ArrayDeque<String>(processingSet);
        while (!arrayDeque.isEmpty()) {
            String name = (String)arrayDeque.poll();
            processingSet.remove(name);
            for (Identifier identifier : root.identifierIndex.get(name)) {
                TypeQualifier qualifier;
                StorageQualifier constQualifier;
                Set constIdsInFunction;
                FunctionDefinition inDefinition;
                TypeAndInitDeclaration taid;
                ReferenceExpression reference = identifier.getAncestor(ReferenceExpression.class);
                if (reference == null || (taid = reference.getAncestor(TypeAndInitDeclaration.class)) == null || (inDefinition = taid.getAncestor(FunctionDefinition.class)) == null || (constIdsInFunction = (Set)constFunctions.get(inDefinition)) == null || !constIdsInFunction.contains(name) || (constQualifier = CompatibilityTransformer.getConstQualifier(qualifier = taid.getType().getTypeQualifier())) == null) continue;
                constQualifier.detachAndDelete();
                if (((ArrayList)qualifier.getChildren()).isEmpty()) {
                    qualifier.detachAndDelete();
                }
                constDeclarationHit = true;
                for (DeclarationMember member : taid.getMembers()) {
                    String memberName = member.getName().getName();
                    if (constIdsInFunction.contains(memberName)) {
                        throw new TransformationException("Illegal redefinition of const parameter " + name);
                    }
                    constIdsInFunction.add(memberName);
                    if (processingSet.contains(memberName)) continue;
                    arrayDeque.add(memberName);
                    processingSet.add(memberName);
                }
            }
        }
        if (constDeclarationHit) {
            LOGGER.warn("Removed the const keyword from declarations that use const parameters. See debugging.md for more information.");
        }
        if (emptyDeclarationHit = root.process(root.nodeIndex.getStream(EmptyDeclaration.class), ASTNode::detachAndDelete)) {
            LOGGER.warn("Removed empty external declarations (\";\").");
        }
        for (String string : reservedWords) {
            String newName = "iris_renamed_" + string;
            if (!root.process(root.identifierIndex.getStream(string).filter(id -> !(id.getParent() instanceof FunctionCallExpression) && !(id.getParent() instanceof FunctionPrototype)), id -> id.setName(newName))) continue;
            LOGGER.warn("Renamed reserved word \"" + string + "\" to \"" + newName + "\".");
        }
        for (StructMember structMember : root.nodeIndex.get(StructMember.class)) {
            TypeSpecifier typeSpecifier = structMember.getType().getTypeSpecifier();
            ArraySpecifier arraySpecifier = typeSpecifier.getArraySpecifier();
            if (arraySpecifier == null || !((ChildNodeList)arraySpecifier.getChildren()).isNullEmpty()) continue;
            arraySpecifier.detach();
            boolean reusedOriginal = false;
            for (StructDeclarator declarator : structMember.getDeclarators()) {
                if (declarator.getArraySpecifier() != null) {
                    throw new TransformationException("Member already has an array specifier");
                }
                declarator.setArraySpecifier(reusedOriginal ? arraySpecifier.cloneInto(root) : arraySpecifier);
                reusedOriginal = true;
            }
            LOGGER.warn("Moved unsized array specifier (of the form []) from the type to each of the the declaration member(s) " + structMember.getDeclarators().stream().map(StructDeclarator::getName).map(Identifier::getName).collect(Collectors.joining(", ")) + ". See debugging.md for more information.");
        }
    }

    private static Statement getInitializer(Root root, String name, Type type) {
        return initTemplate.getInstanceFor(root, new Identifier(name), type.isScalar() ? LiteralExpression.getDefaultValue(type) : root.indexNodes(() -> new FunctionCallExpression(new Identifier(type.getMostCompactName()), Stream.of(LiteralExpression.getDefaultValue(type)))));
    }

    private static TypeQualifier makeQualifierOut(TypeQualifier typeQualifier) {
        for (TypeQualifierPart qualifierPart : typeQualifier.getParts()) {
            if (!(qualifierPart instanceof StorageQualifier)) continue;
            StorageQualifier storageQualifier = (StorageQualifier)qualifierPart;
            if (((StorageQualifier)qualifierPart).storageType != StorageQualifier.StorageType.IN) continue;
            storageQualifier.storageType = StorageQualifier.StorageType.OUT;
        }
        return typeQualifier;
    }

    public static void transformGrouped(ASTParser t, Map<PatchShaderType, TranslationUnit> trees, Parameters parameters) {
        ShaderType prevType = null;
        for (ShaderType type : pipeline) {
            PatchShaderType[] patchTypes = PatchShaderType.fromGlShaderType(type);
            boolean hasAny = false;
            for (PatchShaderType currentType : patchTypes) {
                if (trees.get((Object)currentType) == null) continue;
                hasAny = true;
            }
            if (!hasAny) continue;
            if (prevType == null) {
                prevType = type;
                continue;
            }
            PatchShaderType prevPatchTypes = PatchShaderType.fromGlShaderType(prevType)[0];
            TranslationUnit prevTree = trees.get((Object)prevPatchTypes);
            Root prevRoot = prevTree.getRoot();
            if (prevRoot.getPrefixIdentifierIndex().prefixQueryFlat(tagPrefix).findAny().isPresent()) {
                LOGGER.warn("The prefix tag iris_template_ is used in the shader, bailing compatibility transformation.");
                return;
            }
            HashMap<String, BuiltinNumericTypeSpecifier> outDeclarations = new HashMap<String, BuiltinNumericTypeSpecifier>();
            for (DeclarationExternalDeclaration declarationExternalDeclaration : prevRoot.nodeIndex.get(DeclarationExternalDeclaration.class)) {
                if (!outDeclarationMatcher.matchesExtract(declarationExternalDeclaration)) continue;
                BuiltinNumericTypeSpecifier extractedType = outDeclarationMatcher.getNodeMatch("type", BuiltinNumericTypeSpecifier.class);
                for (DeclarationMember member : outDeclarationMatcher.getNodeMatch("name*", DeclarationMember.class).getAncestor(TypeAndInitDeclaration.class).getMembers()) {
                    String name = member.getName().getName();
                    if (name.startsWith("gl_")) continue;
                    outDeclarations.put(name, extractedType);
                }
            }
            for (PatchShaderType currentType : patchTypes) {
                TranslationUnit currentTree = trees.get((Object)currentType);
                if (currentTree == null) continue;
                Root currentRoot = currentTree.getRoot();
                for (ExternalDeclaration externalDeclaration : currentRoot.nodeIndex.get(DeclarationExternalDeclaration.class)) {
                    if (!inDeclarationMatcher.matchesExtract(externalDeclaration)) continue;
                    BuiltinNumericTypeSpecifier inTypeSpecifier = inDeclarationMatcher.getNodeMatch("type", BuiltinNumericTypeSpecifier.class);
                    for (DeclarationMember inDeclarationMember : inDeclarationMatcher.getNodeMatch("name*", DeclarationMember.class).getAncestor(TypeAndInitDeclaration.class).getMembers()) {
                        String name = inDeclarationMember.getName().getName();
                        if (name.startsWith("gl_")) continue;
                        if (!outDeclarations.containsKey(name)) {
                            if (!currentRoot.identifierIndex.getAncestors(name, ReferenceExpression.class).findAny().isPresent()) continue;
                            if (inTypeSpecifier == null) {
                                LOGGER.warn("The in declaration '" + name + "' in the " + currentType.glShaderType.name() + " shader that has a missing corresponding out declaration in the previous stage " + prevType.name() + " has a non-numeric type and could not be compatibility-patched. See debugging.md for more information.");
                                continue;
                            }
                            Type inType = inTypeSpecifier.type;
                            TypeQualifier outQualifier = (TypeQualifier)inDeclarationMatcher.getNodeMatch("qualifier").cloneInto(prevRoot);
                            CompatibilityTransformer.makeQualifierOut(outQualifier);
                            prevTree.injectNode(ASTInjectionPoint.BEFORE_DECLARATIONS, declarationTemplate.getInstanceFor(prevRoot, outQualifier, inTypeSpecifier.cloneInto(prevRoot), new Identifier(name)));
                            prevTree.prependMainFunctionBody(CompatibilityTransformer.getInitializer(prevRoot, name, inType));
                            outDeclarations.put(name, null);
                            LOGGER.warn("The in declaration '" + name + "' in the " + currentType.glShaderType.name() + " shader is missing a corresponding out declaration in the previous stage " + prevType.name() + " and has been compatibility-patched. See debugging.md for more information.");
                            continue;
                        }
                        BuiltinNumericTypeSpecifier outTypeSpecifier = (BuiltinNumericTypeSpecifier)outDeclarations.get(name);
                        if (outTypeSpecifier == null) continue;
                        Type inType = inTypeSpecifier.type;
                        Type outType = outTypeSpecifier.type;
                        if (outTypeSpecifier.getArraySpecifier() != null) {
                            LOGGER.warn("The out declaration '" + name + "' in the " + prevPatchTypes.glShaderType.name() + " shader that has a missing corresponding in declaration in the next stage " + type.name() + " has an array type and could not be compatibility-patched. See debugging.md for more information.");
                            continue;
                        }
                        if (inType == outType) {
                            if (prevRoot.identifierIndex.get(name).size() > 1) continue;
                            prevTree.prependMainFunctionBody(CompatibilityTransformer.getInitializer(prevRoot, name, inType));
                            outDeclarations.put(name, null);
                            LOGGER.warn("The in declaration '" + name + "' in the " + currentType.glShaderType.name() + " shader that is never assigned to in the previous stage " + prevType.name() + " has been compatibility-patched by adding an initialization for it. See debugging.md for more information.");
                            continue;
                        }
                        if (outType.getDimension() != inType.getDimension()) {
                            LOGGER.warn("The in declaration '" + name + "' in the " + currentType.glShaderType.name() + " shader has a mismatching dimensionality (scalar/vector/matrix) with the out declaration in the previous stage " + prevType.name() + " and could not be compatibility-patched. See debugging.md for more information.");
                            continue;
                        }
                        boolean isVector = outType.isVector();
                        String newName = tagPrefix + name;
                        prevRoot.identifierIndex.rename(name, newName);
                        TypeAndInitDeclaration outDeclaration = outTypeSpecifier.getAncestor(TypeAndInitDeclaration.class);
                        if (outDeclaration == null) continue;
                        ChildNodeList<DeclarationMember> outMembers = outDeclaration.getMembers();
                        DeclarationMember outMember = null;
                        for (DeclarationMember member : outMembers) {
                            if (!member.getName().getName().equals(newName)) continue;
                            outMember = member;
                        }
                        if (outMember == null) {
                            throw new TransformationException("The targeted out declaration member is missing!");
                        }
                        outMember.getName().replaceByAndDelete(new Identifier(name));
                        if (outMembers.size() > 1) {
                            outMember.detach();
                            outTypeSpecifier = outTypeSpecifier.cloneInto(prevRoot);
                            DeclarationExternalDeclaration singleOutDeclaration = (DeclarationExternalDeclaration)declarationTemplate.getInstanceFor(prevRoot, CompatibilityTransformer.makeQualifierOut(outDeclaration.getType().getTypeQualifier().cloneInto(prevRoot)), outTypeSpecifier, new Identifier(name));
                            ((TypeAndInitDeclaration)singleOutDeclaration.getDeclaration()).getMembers().set(0, outMember);
                            prevTree.injectNode(ASTInjectionPoint.BEFORE_DECLARATIONS, singleOutDeclaration);
                        }
                        prevTree.injectNode(ASTInjectionPoint.BEFORE_DECLARATIONS, variableTemplate.getInstanceFor(prevRoot, outTypeSpecifier.cloneInto(prevRoot), new Identifier(newName)));
                        prevTree.appendMainFunctionBody((isVector && outType.getDimensions()[0] < inType.getDimensions()[0] ? statementTemplateVector : statementTemplate).getInstanceFor(prevRoot, new Identifier(name), new Identifier(newName), inTypeSpecifier.cloneInto(prevRoot)));
                        outTypeSpecifier.replaceByAndDelete(inTypeSpecifier.cloneInto(prevRoot));
                        outDeclarations.put(name, null);
                        LOGGER.warn("The out declaration '" + name + "' in the " + prevType.name() + " shader has a different type " + outType.getMostCompactName() + " than the corresponding in declaration of type " + inType.getMostCompactName() + " in the following stage " + currentType.glShaderType.name() + " and has been compatibility-patched. See debugging.md for more information.");
                    }
                }
            }
            prevType = type;
        }
    }

    public static void transformFragmentCore(ASTParser t, TranslationUnit tree, Root root, Parameters parameters) {
        ArrayList<NewDeclarationData> newDeclarationData = new ArrayList<NewDeclarationData>();
        ArrayList<DeclarationExternalDeclaration> declarationsToRemove = new ArrayList<DeclarationExternalDeclaration>();
        for (DeclarationExternalDeclaration declarationExternalDeclaration : root.nodeIndex.get(DeclarationExternalDeclaration.class)) {
            if (!nonLayoutOutDeclarationMatcher.matchesExtract(declarationExternalDeclaration)) continue;
            ChildNodeList<DeclarationMember> members = nonLayoutOutDeclarationMatcher.getNodeMatch("name*", DeclarationMember.class).getAncestor(TypeAndInitDeclaration.class).getMembers();
            TypeQualifier typeQualifier = nonLayoutOutDeclarationMatcher.getNodeMatch("qualifier", TypeQualifier.class);
            BuiltinNumericTypeSpecifier typeSpecifier = nonLayoutOutDeclarationMatcher.getNodeMatch("type", BuiltinNumericTypeSpecifier.class);
            int addedDeclarations = 0;
            for (DeclarationMember member : members) {
                int number;
                String numberSuffix;
                String name = member.getName().getName();
                if (!name.startsWith(attachTargetPrefix) || (numberSuffix = name.substring(attachTargetPrefix.length())).isEmpty()) continue;
                try {
                    number = Integer.parseInt(numberSuffix);
                }
                catch (NumberFormatException e) {
                    continue;
                }
                if (number < 0 || 7 < number) continue;
                newDeclarationData.add(new NewDeclarationData(typeQualifier, typeSpecifier, member, number));
                ++addedDeclarations;
            }
            if (addedDeclarations != members.size()) continue;
            declarationsToRemove.add(declarationExternalDeclaration);
        }
        ((ProxyArrayList)tree.getChildren()).removeAll(declarationsToRemove);
        for (ExternalDeclaration externalDeclaration : declarationsToRemove) {
            externalDeclaration.detachParent();
        }
        ArrayList<ExternalDeclaration> newDeclarations = new ArrayList<ExternalDeclaration>();
        for (NewDeclarationData data : newDeclarationData) {
            DeclarationMember member = data.member;
            member.detach();
            TypeQualifier newQualifier = data.qualifier.cloneInto(root);
            ((ProxyArrayList)newQualifier.getChildren()).add(new LayoutQualifier(Stream.of(new NamedLayoutQualifierPart(new Identifier("location"), new LiteralExpression(Type.INT32, data.number)))));
            ExternalDeclaration newDeclaration = layoutedOutDeclarationTemplate.getInstanceFor(root, newQualifier, data.type.cloneInto(root), member);
            newDeclarations.add(newDeclaration);
        }
        tree.injectNodes(ASTInjectionPoint.BEFORE_DECLARATIONS, newDeclarations);
    }

    static {
        declarationTemplate.markLocalReplacement((ASTNode)((Object)CompatibilityTransformer.declarationTemplate.getSourceRoot().nodeIndex.getUnique(TypeQualifier.class)));
        declarationTemplate.markLocalReplacement("__type", TypeSpecifier.class);
        declarationTemplate.markIdentifierReplacement("__name");
        initTemplate.markIdentifierReplacement("__decl");
        initTemplate.markLocalReplacement("__value", ReferenceExpression.class);
        variableTemplate.markLocalReplacement("__type", TypeSpecifier.class);
        variableTemplate.markIdentifierReplacement("__internalDecl");
        statementTemplate.markIdentifierReplacement("__oldDecl");
        statementTemplate.markIdentifierReplacement("__internalDecl");
        statementTemplate.markLocalReplacement(CompatibilityTransformer.statementTemplate.getSourceRoot().nodeIndex.getStream(BuiltinNumericTypeSpecifier.class).filter(specifier -> specifier.type == Type.F32VEC3).findAny().get());
        statementTemplateVector.markIdentifierReplacement("__oldDecl");
        statementTemplateVector.markIdentifierReplacement("__internalDecl");
        statementTemplateVector.markLocalReplacement(CompatibilityTransformer.statementTemplateVector.getSourceRoot().nodeIndex.getStream(BuiltinNumericTypeSpecifier.class).filter(specifier -> specifier.type == Type.F32VEC3).findAny().get());
        layoutedOutDeclarationTemplate.markLocalReplacement((ASTNode)((Object)CompatibilityTransformer.layoutedOutDeclarationTemplate.getSourceRoot().nodeIndex.getOne(TypeQualifier.class)));
        layoutedOutDeclarationTemplate.markLocalReplacement("__type", TypeSpecifier.class);
        layoutedOutDeclarationTemplate.markLocalReplacement("__name", DeclarationMember.class);
    }

    record NewDeclarationData(TypeQualifier qualifier, TypeSpecifier type, DeclarationMember member, int number) {
    }

    private static class DeclarationMatcher
    extends Matcher<ExternalDeclaration> {
        private final StorageQualifier.StorageType storageType;

        public DeclarationMatcher(StorageQualifier.StorageType storageType) {
            super("out float name;", ParseShape.EXTERNAL_DECLARATION);
            this.markClassWildcard("qualifier", (ASTNode)((Object)((ExternalDeclaration)this.pattern).getRoot().nodeIndex.getUnique(TypeQualifier.class)));
            this.markClassWildcard("type", (ASTNode)((Object)((ExternalDeclaration)this.pattern).getRoot().nodeIndex.getUnique(BuiltinNumericTypeSpecifier.class)));
            this.markClassWildcard("name*", ((Identifier)((ExternalDeclaration)this.pattern).getRoot().identifierIndex.getUnique("name")).getAncestor(DeclarationMember.class));
            this.storageType = storageType;
        }

        @Override
        public boolean matchesExtract(ExternalDeclaration tree) {
            boolean result = super.matchesExtract(tree);
            if (!result) {
                return false;
            }
            TypeQualifier qualifier = this.getNodeMatch("qualifier", TypeQualifier.class);
            for (TypeQualifierPart part : qualifier.getParts()) {
                if (!(part instanceof StorageQualifier)) continue;
                StorageQualifier storageQualifier = (StorageQualifier)part;
                if (storageQualifier.storageType != this.storageType) continue;
                return true;
            }
            return false;
        }
    }
}

