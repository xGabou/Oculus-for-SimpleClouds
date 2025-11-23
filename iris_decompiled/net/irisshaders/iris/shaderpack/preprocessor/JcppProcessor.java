/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.anarres.cpp.Feature
 *  org.anarres.cpp.LexerException
 *  org.anarres.cpp.Preprocessor
 *  org.anarres.cpp.PreprocessorListener
 *  org.anarres.cpp.Source
 *  org.anarres.cpp.StringLexerSource
 *  org.anarres.cpp.Token
 */
package net.irisshaders.iris.shaderpack.preprocessor;

import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.shaderpack.preprocessor.GlslCollectingListener;
import org.anarres.cpp.Feature;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.PreprocessorListener;
import org.anarres.cpp.Source;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;

public class JcppProcessor {
    public static String glslPreprocessSource(String source, Iterable<StringPair> environmentDefines) {
        if (((String)source).contains("#warning IRIS_JCPP_GLSL_VERSION") || ((String)source).contains("#warning IRIS_JCPP_GLSL_EXTENSION")) {
            throw new RuntimeException("Some shader author is trying to exploit internal Iris implementation details, stop!");
        }
        source = ((String)source).replace("#version", "#warning IRIS_JCPP_GLSL_VERSION");
        source = ((String)source).replace("#extension", "#warning IRIS_JCPP_GLSL_EXTENSION");
        source = ((String)source).replace("\u0000", "");
        GlslCollectingListener listener = new GlslCollectingListener();
        Preprocessor pp = new Preprocessor();
        try {
            for (StringPair envDefine : environmentDefines) {
                pp.addMacro(envDefine.key(), envDefine.value());
            }
        }
        catch (LexerException e) {
            throw new RuntimeException("Unexpected LexerException processing macros", e);
        }
        pp.setListener((PreprocessorListener)listener);
        pp.addInput((Source)new StringLexerSource((String)source, true));
        pp.addFeature(Feature.KEEPCOMMENTS);
        StringBuilder builder = new StringBuilder();
        try {
            Token tok;
            while ((tok = pp.token()) != null && tok.getType() != 265) {
                builder.append(tok.getText());
            }
        }
        catch (Exception e) {
            throw new RuntimeException("GLSL source pre-processing failed", e);
        }
        builder.append("\n");
        source = listener.collectLines() + builder;
        return source;
    }
}

