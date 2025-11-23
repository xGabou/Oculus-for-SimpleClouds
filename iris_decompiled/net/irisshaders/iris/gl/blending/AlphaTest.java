/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.gl.blending;

import net.irisshaders.iris.gl.blending.AlphaTestFunction;
import net.irisshaders.iris.gl.blending.AlphaTests;

public record AlphaTest(AlphaTestFunction function, float reference) {
    public static final AlphaTest ALWAYS = new AlphaTest(AlphaTestFunction.ALWAYS, 0.0f);

    public String toExpression(String indentation) {
        return this.toExpression("gl_FragData[0].a", "iris_currentAlphaTest", indentation);
    }

    public String toExpression(String alphaAccessor, String alphaThreshold, String indentation) {
        String expr = this.function.getExpression();
        if (this.function == AlphaTestFunction.ALWAYS) {
            return "// alpha test disabled\n";
        }
        if (this == AlphaTests.VERTEX_ALPHA) {
            return indentation + "if (!(" + alphaAccessor + " > iris_vertexColorAlpha)) {\n" + indentation + "    discard;\n" + indentation + "}\n";
        }
        if (this.function == AlphaTestFunction.NEVER) {
            return "discard;\n";
        }
        return indentation + "if (!(" + alphaAccessor + " " + expr + " " + alphaThreshold + ")) {\n" + indentation + "    discard;\n" + indentation + "}\n";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (((Object)((Object)this)).getClass() != obj.getClass()) {
            return false;
        }
        AlphaTest other = (AlphaTest)((Object)obj);
        if (this.function != other.function) {
            return false;
        }
        return Float.floatToIntBits(this.reference) == Float.floatToIntBits(other.reference);
    }
}

