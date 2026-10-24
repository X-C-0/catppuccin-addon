package me.pindour.catppuccin.utils;

import meteordevelopment.meteorclient.settings.Setting;

import java.util.HashSet;
import java.util.Set;

/**
 * Collects the settings read while a visibility condition is being evaluated.
 * Setting.get() runs on every thread, so the collected reads are kept per
 * thread: a shared collection would be mutated while it is being iterated.
 */
public class SettingWatcher {
    private static final ThreadLocal<Set<Setting<?>>> touched = new ThreadLocal<>();

    public static void start() {
        Set<Setting<?>> set = touched.get();

        if (set == null) touched.set(new HashSet<>());
        else set.clear();
    }

    public static Set<Setting<?>> stop() {
        Set<Setting<?>> set = touched.get();
        touched.remove();

        return set == null ? Set.of() : set;
    }

    public static void touch(Setting<?> setting) {
        Set<Setting<?>> set = touched.get();
        if (set != null) set.add(setting);
    }
}
