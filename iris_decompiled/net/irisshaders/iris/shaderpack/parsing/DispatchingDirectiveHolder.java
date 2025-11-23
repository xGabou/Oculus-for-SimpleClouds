/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 *  org.joml.Vector2f
 *  org.joml.Vector3i
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.shaderpack.parsing;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.IrisLogging;
import net.irisshaders.iris.shaderpack.parsing.ConstDirectiveParser;
import net.irisshaders.iris.shaderpack.parsing.DirectiveHolder;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class DispatchingDirectiveHolder
implements DirectiveHolder {
    private final Map<String, BooleanConsumer> booleanConstVariables = new HashMap<String, BooleanConsumer>();
    private final Map<String, Consumer<String>> stringConstVariables = new HashMap<String, Consumer<String>>();
    private final Map<String, IntConsumer> intConstVariables = new HashMap<String, IntConsumer>();
    private final Map<String, FloatConsumer> floatConstVariables = new HashMap<String, FloatConsumer>();
    private final Map<String, Consumer<Vector2f>> vec2ConstVariables = new HashMap<String, Consumer<Vector2f>>();
    private final Map<String, Consumer<Vector3i>> ivec3ConstVariables = new HashMap<String, Consumer<Vector3i>>();
    private final Map<String, Consumer<Vector4f>> vec4ConstVariables = new HashMap<String, Consumer<Vector4f>>();

    public void processDirective(ConstDirectiveParser.ConstDirective directive) {
        ConstDirectiveParser.Type type = directive.getType();
        String key = directive.getKey();
        String value = directive.getValue();
        if (type == ConstDirectiveParser.Type.BOOL) {
            BooleanConsumer consumer = this.booleanConstVariables.get(key);
            if (consumer != null) {
                if ("true".equals(value)) {
                    consumer.accept(true);
                } else if ("false".equals(value)) {
                    consumer.accept(false);
                } else {
                    Iris.logger.error("Failed to process " + directive + ": " + value + " is not a valid boolean value");
                }
                return;
            }
            if (IrisLogging.ENABLE_SPAM) {
                Iris.logger.info("Found potential unhandled directive: " + directive);
            }
            this.typeCheckHelper("int", this.intConstVariables, directive);
            this.typeCheckHelper("int", this.stringConstVariables, directive);
            this.typeCheckHelper("float", this.floatConstVariables, directive);
            this.typeCheckHelper("vec4", this.vec4ConstVariables, directive);
        } else if (type == ConstDirectiveParser.Type.INT) {
            Consumer<String> stringConsumer = this.stringConstVariables.get(key);
            if (stringConsumer != null) {
                stringConsumer.accept(value);
                return;
            }
            IntConsumer intConsumer = this.intConstVariables.get(key);
            if (intConsumer != null) {
                try {
                    intConsumer.accept(Integer.parseInt(value));
                }
                catch (NumberFormatException e) {
                    Iris.logger.error("Failed to process " + directive, e);
                }
                return;
            }
            if (IrisLogging.ENABLE_SPAM) {
                Iris.logger.info("Found potential unhandled directive: " + directive);
            }
            this.typeCheckHelper("bool", this.booleanConstVariables, directive);
            this.typeCheckHelper("float", this.floatConstVariables, directive);
            this.typeCheckHelper("vec4", this.vec4ConstVariables, directive);
        } else if (type == ConstDirectiveParser.Type.FLOAT) {
            FloatConsumer consumer = this.floatConstVariables.get(key);
            if (consumer != null) {
                try {
                    consumer.accept(Float.parseFloat(value));
                }
                catch (NumberFormatException e) {
                    Iris.logger.error("Failed to process " + directive, e);
                }
                return;
            }
            if (IrisLogging.ENABLE_SPAM) {
                Iris.logger.info("Found potential unhandled directive: " + directive);
            }
            this.typeCheckHelper("bool", this.booleanConstVariables, directive);
            this.typeCheckHelper("int", this.intConstVariables, directive);
            this.typeCheckHelper("int", this.stringConstVariables, directive);
            this.typeCheckHelper("vec4", this.vec4ConstVariables, directive);
        } else if (type == ConstDirectiveParser.Type.VEC2) {
            Consumer<Vector2f> consumer = this.vec2ConstVariables.get(key);
            if (consumer != null) {
                String vec2Args;
                if (!value.startsWith("vec2")) {
                    Iris.logger.error("Failed to process " + directive + ": value was not a valid vec2 constructor");
                }
                if (!(vec2Args = value.substring("vec2".length()).trim()).startsWith("(") || !vec2Args.endsWith(")")) {
                    Iris.logger.error("Failed to process " + directive + ": value was not a valid vec2 constructor");
                }
                vec2Args = vec2Args.substring(1, vec2Args.length() - 1);
                String[] parts = vec2Args.split(",");
                for (int i = 0; i < parts.length; ++i) {
                    parts[i] = parts[i].trim();
                }
                if (parts.length != 2) {
                    Iris.logger.error("Failed to process " + directive + ": expected 2 arguments to a vec2 constructor, got " + parts.length);
                }
                try {
                    consumer.accept(new Vector2f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1])));
                }
                catch (NumberFormatException e) {
                    Iris.logger.error("Failed to process " + directive, e);
                }
                return;
            }
            this.typeCheckHelper("bool", this.booleanConstVariables, directive);
            this.typeCheckHelper("int", this.intConstVariables, directive);
            this.typeCheckHelper("int", this.stringConstVariables, directive);
            this.typeCheckHelper("float", this.floatConstVariables, directive);
        } else if (type == ConstDirectiveParser.Type.IVEC3) {
            Consumer<Vector3i> consumer = this.ivec3ConstVariables.get(key);
            if (consumer != null) {
                String ivec3Args;
                if (!value.startsWith("ivec3")) {
                    Iris.logger.error("Failed to process " + directive + ": value was not a valid ivec3 constructor");
                }
                if (!(ivec3Args = value.substring("ivec3".length()).trim()).startsWith("(") || !ivec3Args.endsWith(")")) {
                    Iris.logger.error("Failed to process " + directive + ": value was not a valid ivec3 constructor");
                }
                ivec3Args = ivec3Args.substring(1, ivec3Args.length() - 1);
                String[] parts = ivec3Args.split(",");
                for (int i = 0; i < parts.length; ++i) {
                    parts[i] = parts[i].trim();
                }
                if (parts.length != 3) {
                    Iris.logger.error("Failed to process " + directive + ": expected 3 arguments to a ivec3 constructor, got " + parts.length);
                }
                try {
                    consumer.accept(new Vector3i(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                }
                catch (NumberFormatException e) {
                    Iris.logger.error("Failed to process " + directive, e);
                }
                return;
            }
            this.typeCheckHelper("bool", this.booleanConstVariables, directive);
            this.typeCheckHelper("int", this.intConstVariables, directive);
            this.typeCheckHelper("int", this.stringConstVariables, directive);
            this.typeCheckHelper("float", this.floatConstVariables, directive);
        } else if (type == ConstDirectiveParser.Type.VEC4) {
            Consumer<Vector4f> consumer = this.vec4ConstVariables.get(key);
            if (consumer != null) {
                String vec4Args;
                if (!value.startsWith("vec4")) {
                    Iris.logger.error("Failed to process " + directive + ": value was not a valid vec4 constructor");
                }
                if (!(vec4Args = value.substring("vec4".length()).trim()).startsWith("(") || !vec4Args.endsWith(")")) {
                    Iris.logger.error("Failed to process " + directive + ": value was not a valid vec4 constructor");
                }
                vec4Args = vec4Args.substring(1, vec4Args.length() - 1);
                String[] parts = vec4Args.split(",");
                for (int i = 0; i < parts.length; ++i) {
                    parts[i] = parts[i].trim();
                }
                if (parts.length != 4) {
                    Iris.logger.error("Failed to process " + directive + ": expected 4 arguments to a vec4 constructor, got " + parts.length);
                }
                try {
                    consumer.accept(new Vector4f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                }
                catch (NumberFormatException e) {
                    Iris.logger.error("Failed to process " + directive, e);
                }
                return;
            }
            this.typeCheckHelper("bool", this.booleanConstVariables, directive);
            this.typeCheckHelper("int", this.intConstVariables, directive);
            this.typeCheckHelper("int", this.stringConstVariables, directive);
            this.typeCheckHelper("float", this.floatConstVariables, directive);
        }
    }

    private void typeCheckHelper(String expected, Map<String, ?> candidates, ConstDirectiveParser.ConstDirective directive) {
        if (candidates.containsKey(directive.getKey())) {
            Iris.logger.warn("Ignoring " + directive + " because it is of the wrong type, a type of " + expected + " is expected.");
        }
    }

    @Override
    public void acceptUniformDirective(String name, Runnable onDetected) {
        if (IrisLogging.ENABLE_SPAM) {
            Iris.logger.warn("Not looking for a uniform directive with the name " + name + " since this type of directive is not currently supported.");
        }
    }

    @Override
    public void acceptCommentStringDirective(String name, Consumer<String> consumer) {
        if (IrisLogging.ENABLE_SPAM) {
            Iris.logger.warn("Not looking for a comment string directive with the name " + name + " since this type of directive is not currently supported.");
        }
    }

    @Override
    public void acceptCommentIntDirective(String name, IntConsumer consumer) {
        if (IrisLogging.ENABLE_SPAM) {
            Iris.logger.warn("Not looking for a comment int directive with the name " + name + " since this type of directive is not currently supported.");
        }
    }

    @Override
    public void acceptCommentFloatDirective(String name, FloatConsumer consumer) {
        if (IrisLogging.ENABLE_SPAM) {
            Iris.logger.warn("Not looking for a comment float directive with the name " + name + " since this type of directive is not currently supported.");
        }
    }

    @Override
    public void acceptConstBooleanDirective(String name, BooleanConsumer consumer) {
        this.booleanConstVariables.put(name, consumer);
    }

    @Override
    public void acceptConstStringDirective(String name, Consumer<String> consumer) {
        this.stringConstVariables.put(name, consumer);
    }

    @Override
    public void acceptConstIntDirective(String name, IntConsumer consumer) {
        this.intConstVariables.put(name, consumer);
    }

    @Override
    public void acceptConstFloatDirective(String name, FloatConsumer consumer) {
        this.floatConstVariables.put(name, consumer);
    }

    @Override
    public void acceptConstVec2Directive(String name, Consumer<Vector2f> consumer) {
        this.vec2ConstVariables.put(name, consumer);
    }

    @Override
    public void acceptConstIVec3Directive(String name, Consumer<Vector3i> consumer) {
        this.ivec3ConstVariables.put(name, consumer);
    }

    @Override
    public void acceptConstVec4Directive(String name, Consumer<Vector4f> consumer) {
        this.vec4ConstVariables.put(name, consumer);
    }
}

