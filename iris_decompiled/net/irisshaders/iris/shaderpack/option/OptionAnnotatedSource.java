/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.irisshaders.iris.shaderpack.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.irisshaders.iris.helpers.OptionalBoolean;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.option.BooleanOption;
import net.irisshaders.iris.shaderpack.option.OptionLocation;
import net.irisshaders.iris.shaderpack.option.OptionSet;
import net.irisshaders.iris.shaderpack.option.OptionType;
import net.irisshaders.iris.shaderpack.option.StringOption;
import net.irisshaders.iris.shaderpack.option.values.OptionValues;
import net.irisshaders.iris.shaderpack.parsing.ParsedString;
import net.irisshaders.iris.shaderpack.transform.line.LineTransform;

public final class OptionAnnotatedSource {
    private static final ImmutableSet<String> VALID_CONST_OPTION_NAMES;
    private final ImmutableList<String> lines;
    private final ImmutableMap<Integer, BooleanOption> booleanOptions;
    private final ImmutableMap<Integer, StringOption> stringOptions;
    private final ImmutableMap<Integer, String> diagnostics;
    private final ImmutableMap<String, IntList> booleanDefineReferences;

    public OptionAnnotatedSource(String source) {
        this((ImmutableList<String>)ImmutableList.copyOf((Object[])source.split("\\R")));
    }

    public OptionAnnotatedSource(ImmutableList<String> lines) {
        this.lines = lines;
        AnnotationsBuilder builder = new AnnotationsBuilder();
        for (int index = 0; index < lines.size(); ++index) {
            String line = (String)lines.get(index);
            OptionAnnotatedSource.parseLine(builder, index, line);
        }
        this.booleanOptions = builder.booleanOptions.build();
        this.stringOptions = builder.stringOptions.build();
        this.diagnostics = builder.diagnostics.build();
        this.booleanDefineReferences = ImmutableMap.copyOf(builder.booleanDefineReferences);
    }

    private static void parseLine(AnnotationsBuilder builder, int index, String lineText) {
        if (!(lineText.contains("#define") || lineText.contains("const") || lineText.contains("#ifdef") || lineText.contains("#ifndef"))) {
            return;
        }
        ParsedString line = new ParsedString(lineText.trim());
        if (line.takeLiteral("#ifdef") || line.takeLiteral("#ifndef")) {
            OptionAnnotatedSource.parseIfdef(builder, index, line);
        } else if (line.takeLiteral("const")) {
            OptionAnnotatedSource.parseConst(builder, index, line);
        } else if (line.currentlyContains("#define")) {
            OptionAnnotatedSource.parseDefineOption(builder, index, line);
        }
    }

    private static void parseIfdef(AnnotationsBuilder builder, int index, ParsedString line) {
        if (!line.takeSomeWhitespace()) {
            return;
        }
        String name = line.takeWord();
        line.takeSomeWhitespace();
        if (name == null || !line.isEnd()) {
            return;
        }
        builder.booleanDefineReferences.computeIfAbsent(name, n -> new IntArrayList()).add(index);
    }

    private static void parseConst(AnnotationsBuilder builder, int index, ParsedString line) {
        String comment;
        boolean isString;
        if (!line.takeSomeWhitespace()) {
            builder.diagnostics.put((Object)index, (Object)"Expected whitespace after const and before type declaration");
            return;
        }
        if (line.takeLiteral("int") || line.takeLiteral("float")) {
            isString = true;
        } else if (line.takeLiteral("bool")) {
            isString = false;
        } else {
            builder.diagnostics.put((Object)index, (Object)"Unexpected type declaration after const. Expected int, float, or bool. Vector const declarations cannot be configured using shader options.");
            return;
        }
        if (!line.takeSomeWhitespace()) {
            builder.diagnostics.put((Object)index, (Object)"Expected whitespace after type declaration.");
            return;
        }
        String name = line.takeWord();
        if (name == null) {
            builder.diagnostics.put((Object)index, (Object)"Expected name of option after type declaration, but an unexpected character was detected first.");
            return;
        }
        line.takeSomeWhitespace();
        if (!line.takeLiteral("=")) {
            builder.diagnostics.put((Object)index, (Object)"Unexpected characters before equals sign in const declaration.");
            return;
        }
        line.takeSomeWhitespace();
        String value = line.takeWordOrNumber();
        if (value == null) {
            builder.diagnostics.put((Object)index, (Object)"Unexpected non-whitespace characters after equals sign");
            return;
        }
        line.takeSomeWhitespace();
        if (!line.takeLiteral(";")) {
            builder.diagnostics.put((Object)index, (Object)"Value between the equals sign and the semicolon wasn't parsed as a valid word or number.");
            return;
        }
        line.takeSomeWhitespace();
        if (line.takeComments()) {
            comment = line.takeRest().trim();
        } else {
            if (!line.isEnd()) {
                builder.diagnostics.put((Object)index, (Object)"Unexpected non-whitespace characters outside of comment after semicolon");
                return;
            }
            comment = null;
        }
        if (!isString) {
            boolean booleanValue;
            if ("true".equals(value)) {
                booleanValue = true;
            } else if ("false".equals(value)) {
                booleanValue = false;
            } else {
                builder.diagnostics.put((Object)index, (Object)("Expected true or false as the value of a boolean const option, but got " + value + "."));
                return;
            }
            if (!VALID_CONST_OPTION_NAMES.contains((Object)name)) {
                builder.diagnostics.put((Object)index, (Object)("This was a valid const boolean option declaration, but " + name + " was not recognized as being a name of one of the configurable const options."));
                return;
            }
            builder.booleanOptions.put((Object)index, (Object)new BooleanOption(OptionType.CONST, name, comment, booleanValue));
            return;
        }
        if (!VALID_CONST_OPTION_NAMES.contains((Object)name)) {
            builder.diagnostics.put((Object)index, (Object)("This was a valid const option declaration, but " + name + " was not recognized as being a name of one of the configurable const options."));
            return;
        }
        StringOption option = StringOption.create(OptionType.CONST, name, comment, value);
        if (option != null) {
            builder.stringOptions.put((Object)index, (Object)option);
        } else {
            builder.diagnostics.put((Object)index, (Object)"Ignoring this const option because it is missing an allowed values listin a comment, but is not a boolean const option.");
        }
    }

    private static void parseDefineOption(AnnotationsBuilder builder, int index, ParsedString line) {
        String comment;
        StringOption option;
        boolean hasLeadingComment = line.takeComments();
        line.takeSomeWhitespace();
        if (!line.takeLiteral("#define")) {
            builder.diagnostics.put((Object)index, (Object)"This line contains an occurrence of \"#define\" but it wasn't in a place we expected, ignoring it.");
            return;
        }
        if (!line.takeSomeWhitespace()) {
            builder.diagnostics.put((Object)index, (Object)"This line properly starts with a #define statement but doesn't have any whitespace characters after the #define.");
            return;
        }
        String name = line.takeWord();
        if (name == null) {
            builder.diagnostics.put((Object)index, (Object)"Invalid syntax after #define directive. No alphanumeric or underscore characters detected.");
            return;
        }
        boolean tookWhitespace = line.takeSomeWhitespace();
        if (line.isEnd()) {
            builder.booleanOptions.put((Object)index, (Object)new BooleanOption(OptionType.DEFINE, name, null, !hasLeadingComment));
            return;
        }
        if (line.takeComments()) {
            String comment2 = line.takeRest().trim();
            builder.booleanOptions.put((Object)index, (Object)new BooleanOption(OptionType.DEFINE, name, comment2, !hasLeadingComment));
            return;
        }
        if (!tookWhitespace) {
            builder.diagnostics.put((Object)index, (Object)"Invalid syntax after #define directive. Only alphanumeric or underscore characters are allowed in option names.");
            return;
        }
        if (hasLeadingComment) {
            builder.diagnostics.put((Object)index, (Object)"Ignoring potential non-boolean #define option since it has a leading comment. Leading comments (//) are only allowed on boolean #define options.");
            return;
        }
        String value = line.takeWordOrNumber();
        if (value == null) {
            builder.diagnostics.put((Object)index, (Object)"Ignoring this #define directive because it doesn't appear to be a boolean #define, and its potential value wasn't a valid number or a valid word.");
            return;
        }
        tookWhitespace = line.takeSomeWhitespace();
        if (line.isEnd()) {
            builder.diagnostics.put((Object)index, (Object)"Ignoring this #define because it doesn't have a comment containing a list of allowed values afterwards, but it has a value so is therefore not a boolean.");
            return;
        }
        if (!tookWhitespace) {
            if (!line.takeComments()) {
                builder.diagnostics.put((Object)index, (Object)"Invalid syntax after value #define directive. Invalid characters after number or word.");
                return;
            }
        } else if (!line.takeComments()) {
            builder.diagnostics.put((Object)index, (Object)"Invalid syntax after value #define directive. Only comments may come after the value.");
            return;
        }
        if ((option = StringOption.create(OptionType.DEFINE, name, comment = line.takeRest().trim(), value)) == null) {
            builder.diagnostics.put((Object)index, (Object)"Ignoring this #define because it is missing an allowed values listin a comment, but is not a boolean define.");
            return;
        }
        builder.stringOptions.put((Object)index, (Object)option);
    }

    private static boolean hasLeadingComment(String line) {
        return line.trim().startsWith("//");
    }

    private static String removeLeadingComment(String line) {
        ParsedString parsed = new ParsedString(line);
        parsed.takeSomeWhitespace();
        parsed.takeComments();
        return parsed.takeRest();
    }

    private static String setBooleanDefineValue(String line, OptionalBoolean newValue, boolean defaultValue) {
        if (OptionAnnotatedSource.hasLeadingComment(line) && newValue.orElse(defaultValue)) {
            return OptionAnnotatedSource.removeLeadingComment(line);
        }
        if (!newValue.orElse(defaultValue)) {
            return "//" + line;
        }
        return line;
    }

    public ImmutableMap<Integer, BooleanOption> getBooleanOptions() {
        return this.booleanOptions;
    }

    public ImmutableMap<Integer, StringOption> getStringOptions() {
        return this.stringOptions;
    }

    public ImmutableMap<Integer, String> getDiagnostics() {
        return this.diagnostics;
    }

    public ImmutableMap<String, IntList> getBooleanDefineReferences() {
        return this.booleanDefineReferences;
    }

    public OptionSet getOptionSet(AbsolutePackPath filePath, Set<String> booleanDefineReferences) {
        OptionSet.Builder builder = OptionSet.builder();
        this.booleanOptions.forEach((lineIndex, option) -> {
            if (booleanDefineReferences.contains(option.getName())) {
                OptionLocation location = new OptionLocation(filePath, (int)lineIndex);
                builder.addBooleanOption(location, (BooleanOption)option);
            }
        });
        this.stringOptions.forEach((lineIndex, option) -> {
            OptionLocation location = new OptionLocation(filePath, (int)lineIndex);
            builder.addStringOption(location, (StringOption)option);
        });
        return builder.build();
    }

    public LineTransform asTransform(OptionValues values) {
        return (index, line) -> this.edit(values, index, line);
    }

    public String apply(OptionValues values) {
        StringBuilder source = new StringBuilder();
        for (int index = 0; index < this.lines.size(); ++index) {
            source.append(this.edit(values, index, (String)this.lines.get(index)));
            source.append('\n');
        }
        return source.toString();
    }

    private String edit(OptionValues values, int index, String existing) {
        BooleanOption booleanOption = (BooleanOption)this.booleanOptions.get((Object)index);
        if (booleanOption != null) {
            OptionalBoolean value2 = values.getBooleanValue(booleanOption.getName());
            if (booleanOption.getType() == OptionType.DEFINE) {
                return OptionAnnotatedSource.setBooleanDefineValue(existing, value2, booleanOption.getDefaultValue());
            }
            if (booleanOption.getType() == OptionType.CONST) {
                if (value2 != OptionalBoolean.DEFAULT) {
                    return this.editConst(existing, Boolean.toString(booleanOption.getDefaultValue()), Boolean.toString(value2.orElse(booleanOption.getDefaultValue())));
                }
                return existing;
            }
            throw new AssertionError((Object)("Unknown option type " + booleanOption.getType()));
        }
        StringOption stringOption = (StringOption)this.stringOptions.get((Object)index);
        if (stringOption != null) {
            return values.getStringValue(stringOption.getName()).map(value -> {
                if (stringOption.getType() == OptionType.DEFINE) {
                    return "#define " + stringOption.getName() + " " + value + " // OptionAnnotatedSource: Changed option";
                }
                if (stringOption.getType() == OptionType.CONST) {
                    return this.editConst(existing, stringOption.getDefaultValue(), (String)value);
                }
                throw new AssertionError((Object)("Unknown option type " + stringOption.getType()));
            }).orElse(existing);
        }
        return existing;
    }

    private String editConst(String line, String currentValue, String newValue) {
        int equalsIndex = line.indexOf(61);
        if (equalsIndex == -1) {
            throw new IllegalStateException();
        }
        String firstPart = line.substring(0, equalsIndex);
        String secondPart = line.substring(equalsIndex);
        secondPart = secondPart.replaceFirst(Pattern.quote(currentValue), Matcher.quoteReplacement(newValue));
        return firstPart + secondPart;
    }

    static {
        ImmutableSet.Builder values = ImmutableSet.builder().add((Object[])new String[]{"shadowMapResolution", "shadowDistance", "voxelDistance", "shadowDistanceRenderMul", "entityShadowDistanceMul", "shadowIntervalSize", "generateShadowMipmap", "generateShadowColorMipmap", "shadowHardwareFiltering", "shadowtex0Mipmap", "shadowtexMipmap", "shadowtex1Mipmap", "shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest", "shadowtex1Nearest", "shadow1MinMagNearest", "wetnessHalflife", "drynessHalflife", "eyeBrightnessHalflife", "centerDepthHalflife", "sunPathRotation", "ambientOcclusionLevel", "superSamplingLevel", "noiseTextureResolution"});
        for (int i = 0; i < 8; ++i) {
            values.add((Object)("shadowcolor" + i + "Mipmap"));
            values.add((Object)("shadowColor" + i + "Mipmap"));
            values.add((Object)("shadowcolor" + i + "Nearest"));
            values.add((Object)("shadowColor" + i + "Nearest"));
            values.add((Object)("shadowcolor" + i + "MinMagNearest"));
            values.add((Object)("shadowColor" + i + "MinMagNearest"));
            values.add((Object)("shadowHardwareFiltering" + i));
        }
        VALID_CONST_OPTION_NAMES = values.build();
    }

    private static class AnnotationsBuilder {
        private final ImmutableMap.Builder<Integer, BooleanOption> booleanOptions = ImmutableMap.builder();
        private final ImmutableMap.Builder<Integer, StringOption> stringOptions = ImmutableMap.builder();
        private final ImmutableMap.Builder<Integer, String> diagnostics = ImmutableMap.builder();
        private final Map<String, IntList> booleanDefineReferences = new HashMap<String, IntList>();

        private AnnotationsBuilder() {
        }
    }
}

