/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 */
package net.irisshaders.iris.pipeline.transform.transformer;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinFixedTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.helpers.Tri;
import net.irisshaders.iris.shaderpack.texture.TextureStage;

public class TextureTransformer {
    public static void transform(ASTParser t, TranslationUnit tree, Root root, TextureStage stage, Object2ObjectMap<Tri<String, TextureType, TextureStage>, String> textureMap) {
        textureMap.forEach((stringTextureTypeTextureStageTri, s) -> {
            if (stringTextureTypeTextureStageTri.third() == stage) {
                String name = (String)stringTextureTypeTextureStageTri.first();
                for (Identifier id : root.identifierIndex.get(name)) {
                    TypeSpecifier patt1898$temp;
                    DeclarationExternalDeclaration declaration;
                    TypeAndInitDeclaration initDeclaration = (TypeAndInitDeclaration)id.getAncestor(2, 0, TypeAndInitDeclaration.class::isInstance);
                    if (initDeclaration == null || (declaration = (DeclarationExternalDeclaration)initDeclaration.getAncestor(1, 0, DeclarationExternalDeclaration.class::isInstance)) == null || !((patt1898$temp = initDeclaration.getType().getTypeSpecifier()) instanceof BuiltinFixedTypeSpecifier)) continue;
                    BuiltinFixedTypeSpecifier fixed = (BuiltinFixedTypeSpecifier)patt1898$temp;
                    if (!TextureTransformer.isTypeValid((TextureType)((Object)((Object)stringTextureTypeTextureStageTri.second())), fixed.type)) continue;
                    root.rename((String)stringTextureTypeTextureStageTri.first(), (String)s);
                    break;
                }
            }
        });
    }

    private static boolean isTypeValid(TextureType expectedType, BuiltinFixedTypeSpecifier.BuiltinType extractedType) {
        return switch (expectedType) {
            case TextureType.TEXTURE_1D -> {
                if (extractedType == BuiltinFixedTypeSpecifier.BuiltinType.SAMPLER1D || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.ISAMPLER1D || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.USAMPLER1D) {
                    yield true;
                }
                yield false;
            }
            case TextureType.TEXTURE_RECTANGLE -> {
                if (extractedType == BuiltinFixedTypeSpecifier.BuiltinType.SAMPLER2DRECT || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.ISAMPLER2DRECT || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.USAMPLER2DRECT) {
                    yield true;
                }
                yield false;
            }
            case TextureType.TEXTURE_2D -> {
                if (extractedType == BuiltinFixedTypeSpecifier.BuiltinType.SAMPLER2D || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.ISAMPLER2D || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.USAMPLER2D) {
                    yield true;
                }
                yield false;
            }
            case TextureType.TEXTURE_3D -> {
                if (extractedType == BuiltinFixedTypeSpecifier.BuiltinType.SAMPLER3D || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.ISAMPLER3D || extractedType == BuiltinFixedTypeSpecifier.BuiltinType.USAMPLER3D) {
                    yield true;
                }
                yield false;
            }
            default -> throw new IllegalStateException("Unexpected enum! " + expectedType);
        };
    }
}

