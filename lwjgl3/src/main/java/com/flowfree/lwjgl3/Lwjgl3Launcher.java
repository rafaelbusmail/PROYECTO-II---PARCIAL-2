package com.flowfree.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.flowfree.FlowFreeGame;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) {
            return;
        }
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new FlowFreeGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("FlowFree");
        cfg.useVsync(true);
        cfg.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);


        cfg.setMaximized(true);

        cfg.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return cfg;
    }
}
