/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package net.irisshaders.iris.gui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class FileDialogUtil {
    private static final ExecutorService FILE_DIALOG_EXECUTOR = Executors.newSingleThreadExecutor();

    private FileDialogUtil() {
    }

    public static CompletableFuture<Optional<Path>> fileSelectDialog(DialogType dialog, String title, @Nullable Path origin, @Nullable String filterLabel, String ... filters) {
        CompletableFuture<Optional<Path>> future = new CompletableFuture<Optional<Path>>();
        FILE_DIALOG_EXECUTOR.submit(() -> {
            String result = null;
            try (MemoryStack stack = MemoryStack.stackPush();){
                String path;
                PointerBuffer filterBuffer = stack.mallocPointer(filters.length);
                for (String filter : filters) {
                    filterBuffer.put(stack.UTF8((CharSequence)filter));
                }
                filterBuffer.flip();
                String string = path = origin != null ? origin.toAbsolutePath().toString() : null;
                if (dialog == DialogType.SAVE) {
                    result = TinyFileDialogs.tinyfd_saveFileDialog((CharSequence)title, (CharSequence)path, (PointerBuffer)filterBuffer, (CharSequence)filterLabel);
                } else if (dialog == DialogType.OPEN) {
                    result = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)title, (CharSequence)path, (PointerBuffer)filterBuffer, (CharSequence)filterLabel, (boolean)false);
                }
            }
            future.complete(Optional.ofNullable(result).map(x$0 -> Paths.get(x$0, new String[0])));
        });
        return future;
    }

    public static enum DialogType {
        SAVE,
        OPEN;

    }
}

