/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.transform;

import net.irisshaders.iris.shaderpack.transform.Transformations;

public class StringTransformations
implements Transformations {
    private String prefix;
    private String extensions;
    private StringBuilder injections;
    private String body;
    private StringBuilder suffix;

    public StringTransformations(String base) {
        int versionStringStart = base.indexOf("#version");
        if (versionStringStart == -1) {
            throw new IllegalArgumentException("A valid shader should include a version string");
        }
        String prefix = base.substring(0, versionStringStart);
        base = base.substring(versionStringStart);
        int splitPoint = base.indexOf("\n") + 1;
        this.prefix = prefix + base.substring(0, splitPoint);
        this.extensions = "";
        this.injections = new StringBuilder();
        this.body = base.substring(splitPoint);
        this.suffix = new StringBuilder("\n");
        if (!this.body.contains("#extension")) {
            return;
        }
        StringBuilder extensions = new StringBuilder();
        StringBuilder body = new StringBuilder();
        boolean inBody = false;
        for (String line : this.body.split("\\R")) {
            String trimmedLine = line.trim();
            if (!(trimmedLine.isEmpty() || trimmedLine.startsWith("#extension") || trimmedLine.startsWith("//"))) {
                inBody = true;
            }
            if (inBody) {
                body.append(line);
                body.append('\n');
                continue;
            }
            extensions.append(line);
            extensions.append('\n');
        }
        this.extensions = extensions.toString();
        this.body = body.toString();
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean contains(String content) {
        return this.toString().contains(content);
    }

    @Override
    public void define(String key, String value) {
        this.extensions = this.extensions + "#define " + key + " " + value + "\n";
    }

    @Override
    public void injectLine(Transformations.InjectionPoint at, String line) {
        if (at == Transformations.InjectionPoint.BEFORE_CODE) {
            this.injections.append(line);
            this.injections.append('\n');
        } else if (at == Transformations.InjectionPoint.DEFINES) {
            this.extensions = this.extensions + line + "\n";
        } else if (at == Transformations.InjectionPoint.END) {
            this.suffix.append(line);
            this.suffix.append('\n');
        } else {
            throw new IllegalArgumentException("Unsupported injection point: " + at);
        }
    }

    @Override
    public void replaceExact(String from, String to) {
        if (from.contains("\n")) {
            throw new UnsupportedOperationException();
        }
        this.prefix = this.prefix.replace(from, to);
        this.extensions = this.extensions.replace(from, to);
        this.injections = new StringBuilder(this.injections.toString().replace(from, to));
        this.body = this.body.replace(from, to);
        this.suffix = new StringBuilder(this.suffix.toString().replace(from, to));
    }

    @Override
    public void replaceRegex(String regex, String to) {
        this.prefix = this.prefix.replaceAll(regex, to);
        this.extensions = this.extensions.replaceAll(regex, to);
        this.injections = new StringBuilder(this.injections.toString().replaceAll(regex, to));
        this.body = this.body.replaceAll(regex, to);
        this.suffix = new StringBuilder(this.suffix.toString().replaceAll(regex, to));
    }

    public String toString() {
        return this.prefix + this.extensions + this.injections + this.body + this.suffix;
    }
}

