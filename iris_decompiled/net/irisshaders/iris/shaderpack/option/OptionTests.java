/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package net.irisshaders.iris.shaderpack.option;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import net.irisshaders.iris.shaderpack.option.OptionAnnotatedSource;
import net.irisshaders.iris.shaderpack.option.OptionType;
import org.apache.commons.lang3.StringUtils;

public class OptionTests {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("run/shaderpacks/Sildurs Vibrant Shaders v1.29 Medium/shaders/shaders.settings", new String[0]));
        OptionAnnotatedSource source = new OptionAnnotatedSource((ImmutableList<String>)ImmutableList.copyOf(lines));
        System.out.println("Boolean Options:");
        if (source.getBooleanOptions().isEmpty()) {
            System.out.println("(none)");
        } else {
            System.out.println("[Line] Type   | Name                             | Value | Comment");
            System.out.println("       ------ | -------------------------------- | ----- | -------");
        }
        source.getBooleanOptions().forEach((index, option) -> {
            String type = option.getType() == OptionType.CONST ? " Const" : "Define";
            System.out.println("[" + StringUtils.leftPad((String)Integer.toString(index + 1), (int)4, (char)' ') + "] " + type + " | " + StringUtils.rightPad((String)option.getName(), (int)32, (char)' ') + " | " + StringUtils.leftPad((String)Boolean.toString(option.getDefaultValue()), (int)5, (char)' ') + " | " + option.getComment().orElse(""));
        });
        System.out.println("String Options:");
        if (source.getStringOptions().isEmpty()) {
            System.out.println("(none)");
        } else {
            System.out.println("[Line] | Type   | Name                             | Value    | Allowed Values");
            System.out.println("       | ------ | -------------------------------- | -------- | -------");
        }
        source.getStringOptions().forEach((index, option) -> {
            String type = option.getType() == OptionType.CONST ? " Const" : "Define";
            System.out.println("[" + StringUtils.leftPad((String)Integer.toString(index + 1), (int)4, (char)' ') + "] | " + type + " | " + StringUtils.rightPad((String)option.getName(), (int)32, (char)' ') + " | " + StringUtils.leftPad((String)option.getDefaultValue(), (int)8, (char)' ') + " | " + option.getAllowedValues());
            System.out.println("       " + option.getComment().orElse("(no comment)"));
        });
        System.out.println("Diagnostics:");
        source.getDiagnostics().forEach((index, diagnostic) -> System.out.println("[" + StringUtils.leftPad((String)Integer.toString(index + 1), (int)4, (char)' ') + "] " + diagnostic));
        if (source.getDiagnostics().isEmpty()) {
            System.out.println("(none)");
        }
    }
}

