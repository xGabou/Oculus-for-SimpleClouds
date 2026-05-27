package net.Gabou.oculus_for_simpleclouds.auth;

import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ClientLauncherGuards {
    private static final String SUSPICIOUS_FILE_NAME = "TLauncherAdditional.json";
    private static volatile String detectedReason;

    private ClientLauncherGuards() {
    }

    public static void enforce() {
        if (!FMLLoader.isProduction()) {
            detectedReason = null;
            return;
        }
        detectedReason = detectSuspiciousLauncher();
    }

    public static String detectSuspiciousLauncher() {
        String suspiciousFile = findSuspiciousFile();
        return suspiciousFile == null ? null : "file:" + suspiciousFile;
    }

    public static String getDetectedReason() {
        return detectedReason;
    }

    private static String findSuspiciousFile() {
        Path cwd = Paths.get(System.getProperty("user.dir", "."));
        Path candidate = cwd.resolve(SUSPICIOUS_FILE_NAME);
        if (Files.exists(candidate)) {
            return candidate.toAbsolutePath().toString();
        }
        return null;
    }
}
