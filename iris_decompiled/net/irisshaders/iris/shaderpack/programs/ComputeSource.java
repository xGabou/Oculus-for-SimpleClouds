/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 *  org.joml.Vector3i
 */
package net.irisshaders.iris.shaderpack.programs;

import java.util.Optional;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.properties.IndirectPointer;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.joml.Vector2f;
import org.joml.Vector3i;

public class ComputeSource {
    private final String name;
    private final String source;
    private final ProgramSet parent;
    private final IndirectPointer indirectPointer;
    private Vector3i workGroups;
    private Vector2f workGroupRelative;

    public ComputeSource(String name, String source, ProgramSet parent, ShaderProperties properties) {
        this.name = name;
        this.source = source;
        this.parent = parent;
        this.indirectPointer = (IndirectPointer)((Object)properties.getIndirectPointers().get((Object)name));
    }

    public String getName() {
        return this.name;
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(this.source);
    }

    public ProgramSet getParent() {
        return this.parent;
    }

    public boolean isValid() {
        return this.source != null;
    }

    public Vector2f getWorkGroupRelative() {
        return this.workGroupRelative;
    }

    public void setWorkGroupRelative(Vector2f workGroupRelative) {
        this.workGroupRelative = workGroupRelative;
    }

    public Vector3i getWorkGroups() {
        return this.workGroups;
    }

    public void setWorkGroups(Vector3i workGroups) {
        this.workGroups = workGroups;
    }

    public IndirectPointer getIndirectPointer() {
        return this.indirectPointer;
    }

    public Optional<ComputeSource> requireValid() {
        if (this.isValid()) {
            return Optional.of(this);
        }
        return Optional.empty();
    }
}

