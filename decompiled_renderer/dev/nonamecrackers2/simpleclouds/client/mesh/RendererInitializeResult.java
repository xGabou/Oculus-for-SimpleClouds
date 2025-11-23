/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.Util
 *  net.minecraft.network.chat.Component
 */
package dev.nonamecrackers2.simpleclouds.client.mesh;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

public class RendererInitializeResult {
    private final State state;
    private final List<Error> errors;
    @Nullable
    private List<Path> savedReportsPaths;
    @Nullable
    private List<CrashReport> crashReports;

    private RendererInitializeResult(State state, List<Error> errors) {
        this.state = state;
        this.errors = errors;
    }

    public State getState() {
        return this.state;
    }

    public List<Error> getErrors() {
        return this.errors;
    }

    public List<CrashReport> createCrashReports() {
        ArrayList reports = Lists.newArrayList();
        for (Error error : this.errors) {
            CrashReport report = CrashReport.m_127521_((Throwable)error.error(), (String)("Simple Clouds mesh generator initialization; " + error.title()));
            CrashReportCategory category = report.m_127514_("Initialization details");
            category.m_128159_("Recommendation", (Object)error.text().getString());
            if (this.errors.size() > 1) {
                category.m_128159_("Notice", (Object)"Multiple crash reports have been generated during mesh generator initialization");
            }
            reports.add(report);
        }
        this.crashReports = reports;
        return reports;
    }

    public void saveCrashReports(File gameDirectory) {
        this.savedReportsPaths = Lists.newArrayList();
        boolean flag = this.crashReports.size() > 1;
        for (int i = 0; i < this.crashReports.size(); ++i) {
            CrashReport report = this.crashReports.get(i);
            File crashReportPath = new File(gameDirectory, "crash-reports");
            String fileName = "crash-" + Util.m_241986_() + "-simpleclouds-mesh-generator";
            fileName = flag ? fileName + "-" + i + ".txt" : fileName + ".txt";
            File file = new File(crashReportPath, fileName);
            if (report.m_127527_() != null) continue;
            report.m_127512_(file);
            this.savedReportsPaths.add(file.toPath());
        }
    }

    @Nullable
    public List<Path> getSavedCrashReportPaths() {
        return this.savedReportsPaths;
    }

    public static RendererInitializeResult success() {
        return new RendererInitializeResult(State.SUCCESS, (List<Error>)ImmutableList.of());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static enum State {
        SUCCESS,
        ERROR;

    }

    public record Error(@Nullable Throwable error, String title, @Nullable Component text) {
    }

    public static class Builder {
        private State state = State.SUCCESS;
        private final ImmutableList.Builder<Error> errors = ImmutableList.builder();

        private Builder() {
        }

        public Builder addError(@Nullable Throwable error, String title, Component text) {
            this.errors.add((Object)new Error(error, title, Objects.requireNonNull(text)));
            this.state = State.ERROR;
            return this;
        }

        public Builder errorUnknown(@Nullable Throwable error, String title) {
            return this.addError(error, title, (Component)Component.m_237115_((String)"gui.simpleclouds.error.unknown"));
        }

        public Builder errorRecommendations(@Nullable Throwable error, String title) {
            return this.addError(error, title, (Component)Component.m_237115_((String)"gui.simpleclouds.error.recommendations"));
        }

        public Builder errorOpenGL() {
            return this.addError(null, "Outdated OpenGL Version", (Component)Component.m_237115_((String)"gui.simpleclouds.error.opengl"));
        }

        public Builder errorCouldNotLoadMeshScript(@Nullable Throwable error, String title) {
            return this.addError(error, title, (Component)Component.m_237115_((String)"gui.simpleclouds.error.couldNotLoadMeshScript"));
        }

        public Builder coreShadersNotInitialized(@Nullable Throwable error) {
            return this.addError(error, "Core Shader Initialization Error", (Component)Component.m_237115_((String)"gui.simpleclouds.error.coreShadersInitialization"));
        }

        public RendererInitializeResult build() {
            return new RendererInitializeResult(this.state, (List<Error>)this.errors.build());
        }
    }
}

