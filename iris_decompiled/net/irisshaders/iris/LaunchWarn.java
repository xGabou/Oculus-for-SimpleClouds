/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URI;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LaunchWarn {
    public static void main(String[] args) {
        String message = "This file is the Forge version of Oculus, meant to be installed as a mod. Would you like to get the NeoForge Installer instead?";
        String fallback = "This file is the Forge version of Oculus, meant to be installed as a mod. Please download the NeoForge Installer from https://neoforged.net.";
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println(fallback);
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (ReflectiveOperationException | UnsupportedLookAndFeelException exception) {
                // empty catch block
            }
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                int option = JOptionPane.showOptionDialog(null, message, "Oculus Installer", 0, 1, null, null, null);
                if (option == 0) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://neoforged.net"));
                    }
                    catch (IOException e) {
                        System.out.println("Welp; we're screwed.");
                        e.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, fallback);
            }
        }
        System.exit(0);
    }
}

