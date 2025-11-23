/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.shaderpack.loading;

import net.irisshaders.iris.shaderpack.loading.ProgramGroup;

public enum ProgramArrayId {
    Setup(ProgramGroup.Setup, 100),
    Begin(ProgramGroup.Begin, 100),
    ShadowComposite(ProgramGroup.ShadowComposite, 100),
    Prepare(ProgramGroup.Prepare, 100),
    Deferred(ProgramGroup.Deferred, 100),
    Composite(ProgramGroup.Composite, 100);

    private final ProgramGroup group;
    private final int numPrograms;

    private ProgramArrayId(ProgramGroup group, int numPrograms) {
        this.group = group;
        this.numPrograms = numPrograms;
    }

    public ProgramGroup getGroup() {
        return this.group;
    }

    public String getSourcePrefix() {
        return this.group.getBaseName();
    }

    public int getNumPrograms() {
        return this.numPrograms;
    }
}

