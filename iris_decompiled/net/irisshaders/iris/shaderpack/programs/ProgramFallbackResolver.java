/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.shaderpack.programs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.irisshaders.iris.shaderpack.loading.ProgramId;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import org.jetbrains.annotations.Nullable;

public class ProgramFallbackResolver {
    private final ProgramSet programs;
    private final Map<ProgramId, ProgramSource> cache;

    public ProgramFallbackResolver(ProgramSet programs) {
        this.programs = programs;
        this.cache = new HashMap<ProgramId, ProgramSource>();
    }

    public Optional<ProgramSource> resolve(ProgramId id) {
        return Optional.ofNullable(this.resolveNullable(id));
    }

    public boolean has(ProgramId id) {
        return this.programs.get(id).isPresent();
    }

    @Nullable
    public ProgramSource resolveNullable(ProgramId id) {
        ProgramId fallback;
        if (this.cache.containsKey((Object)id)) {
            return this.cache.get((Object)id);
        }
        ProgramSource source = this.programs.get(id).orElse(null);
        if (source == null && (fallback = (ProgramId)id.getFallback().orElse(null)) != null) {
            source = this.resolveNullable(fallback);
        }
        this.cache.put(id, source);
        return source;
    }
}

