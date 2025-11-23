/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.anarres.cpp.Feature
 *  org.anarres.cpp.LexerException
 *  org.anarres.cpp.Preprocessor
 *  org.anarres.cpp.PreprocessorCommand
 *  org.anarres.cpp.PreprocessorListener
 *  org.anarres.cpp.Source
 *  org.anarres.cpp.StringLexerSource
 *  org.anarres.cpp.Token
 */
package net.irisshaders.iris.shaderpack.preprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.preprocessor.PropertiesCommentListener;
import net.irisshaders.iris.shaderpack.preprocessor.PropertyCollectingListener;
import org.anarres.cpp.Feature;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.PreprocessorCommand;
import org.anarres.cpp.PreprocessorListener;
import org.anarres.cpp.Source;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;

public class PropertiesPreprocessor {
    public static String preprocessSource(String source, ShaderPackOptions shaderPackOptions, Iterable<StringPair> environmentDefines) {
        String string;
        if (source.contains("#warning IRIS_PASSTHROUGH ") || source.contains("IRIS_PASSTHROUGHBACKSLASH")) {
            throw new RuntimeException("Some shader author is trying to exploit internal Iris implementation details, stop!");
        }
        List<String> booleanValues = PropertiesPreprocessor.getBooleanValues(shaderPackOptions);
        Map<String, String> stringValues = PropertiesPreprocessor.getStringValues(shaderPackOptions);
        Preprocessor pp = new Preprocessor();
        try {
            for (String value2 : booleanValues) {
                pp.addMacro(value2);
            }
            for (StringPair envDefine : environmentDefines) {
                pp.addMacro(envDefine.key(), envDefine.value());
            }
            stringValues.forEach((name, value) -> {
                try {
                    pp.addMacro(name, value);
                }
                catch (LexerException e) {
                    Iris.logger.fatal("Failed to preprocess property file!", e);
                }
            });
            string = PropertiesPreprocessor.process(pp, source);
        }
        catch (Throwable throwable) {
            try {
                try {
                    pp.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new RuntimeException("Unexpected IOException while processing macros", e);
            }
            catch (LexerException e) {
                throw new RuntimeException("Unexpected LexerException processing macros", e);
            }
        }
        pp.close();
        return string;
    }

    public static String preprocessSource(String source, Iterable<StringPair> environmentDefines) {
        if (source.contains("#warning IRIS_PASSTHROUGH ")) {
            throw new RuntimeException("Some shader author is trying to exploit internal Iris implementation details, stop!");
        }
        Preprocessor preprocessor = new Preprocessor();
        try {
            for (StringPair envDefine : environmentDefines) {
                preprocessor.addMacro(envDefine.key(), envDefine.value());
            }
        }
        catch (LexerException e) {
            Iris.logger.fatal("Failed to preprocess property file!", e);
        }
        return PropertiesPreprocessor.process(preprocessor, source);
    }

    private static String process(Preprocessor preprocessor, String source) {
        preprocessor.setListener((PreprocessorListener)new PropertiesCommentListener());
        PropertyCollectingListener listener = new PropertyCollectingListener();
        preprocessor.setListener((PreprocessorListener)listener);
        source = Arrays.stream(((String)source).split("\\R")).map(String::trim).filter(s -> !s.isBlank()).map(line -> {
            if (line.startsWith("#")) {
                for (PreprocessorCommand command : PreprocessorCommand.values()) {
                    if (!line.startsWith("#" + command.name().replace("PP_", "").toLowerCase(Locale.ROOT))) continue;
                    return line;
                }
                return "";
            }
            return line.replace("#", "");
        }).collect(Collectors.joining("\n")) + "\n";
        source = ((String)source).replace("\\", "IRIS_PASSTHROUGHBACKSLASH");
        preprocessor.addInput((Source)new StringLexerSource((String)source, true));
        preprocessor.addFeature(Feature.KEEPCOMMENTS);
        StringBuilder builder = new StringBuilder();
        try {
            Token tok;
            while ((tok = preprocessor.token()) != null && tok.getType() != 265) {
                builder.append(tok.getText());
            }
        }
        catch (Exception e) {
            Iris.logger.error("Properties pre-processing failed", e);
        }
        source = builder.toString();
        return (listener.collectLines() + (String)source).replace("IRIS_PASSTHROUGHBACKSLASH", "\\");
    }

    private static List<String> getBooleanValues(ShaderPackOptions shaderPackOptions) {
        ArrayList<String> booleanValues = new ArrayList<String>();
        shaderPackOptions.getOptionSet().getBooleanOptions().forEach((string, value) -> {
            boolean trueValue = shaderPackOptions.getOptionValues().getBooleanValueOrDefault((String)string);
            if (trueValue) {
                booleanValues.add((String)string);
            }
        });
        return booleanValues;
    }

    private static Map<String, String> getStringValues(ShaderPackOptions shaderPackOptions) {
        HashMap<String, String> stringValues = new HashMap<String, String>();
        shaderPackOptions.getOptionSet().getStringOptions().forEach((optionName, value) -> stringValues.put((String)optionName, shaderPackOptions.getOptionValues().getStringValueOrDefault((String)optionName)));
        return stringValues;
    }
}

