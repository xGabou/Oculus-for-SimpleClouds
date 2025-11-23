/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.irisshaders.iris.shaderpack.error;

import org.apache.commons.lang3.StringUtils;

public record RusticError(String severity, String message, String detailMessage, String file, int lineNumber, String badLine) {
    public String toString() {
        return this.severity + ": " + this.message + "\n --> " + this.file + ":" + this.lineNumber + "\n  |\n  | " + this.badLine + "\n  | " + StringUtils.repeat((char)'^', (int)this.badLine.length()) + " " + this.detailMessage + "\n  |";
    }
}

