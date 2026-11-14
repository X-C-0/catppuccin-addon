package me.pindour.catppuccin.gui.themes.catppuccin.colors;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Range;

public class ColorLink implements ISerializable<ColorLink> {
    private static final String KEY_COLOR = "color";
    private static final String KEY_OPACITY = "opacity";

    private CatppuccinColor color;
    private int opacity = 255;

    public ColorLink(CatppuccinColor color) {
        this.color = color;
    }

    public ColorLink() { }

    public CatppuccinColor color() {
        return color;
    }

    public int opacity() {
        return opacity;
    }

    public void setOpacity(@Range(from = 0, to = 255) int opacity) {
        this.opacity = opacity;
    }

    public void apply(ColorSetting setting) {
        if (!(GuiThemes.get() instanceof CatppuccinGuiTheme theme)) return;
        if (color == null || setting == null) return;

        Color finalColor = ColorUtils.withAlpha(theme.getColor(color), opacity);
        if (setting.get().getPacked() == finalColor.getPacked()) return;

        setting.get().set(finalColor);
        setting.onChanged();
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        if (color != null) {
            tag.putString(KEY_COLOR, color.name());
            tag.putInt(KEY_OPACITY, opacity);
        }

        return tag;
    }

    @Override
    public ColorLink fromTag(CompoundTag tag) {
        //? if <=1.21.4 {
        /*String name = tag.getString(KEY_COLOR);
        int op = tag.contains(KEY_OPACITY) ? tag.getInt(KEY_OPACITY) : 255;
        *///? } else {
        String name = tag.getStringOr(KEY_COLOR, "");
        int op = tag.getIntOr(KEY_OPACITY, 255);
        //? }

        color = parseColor(name);
        opacity = Math.clamp(op, 0, 255);
        return this;
    }

    public boolean isValid() {
        return color != null;
    }

    private static CatppuccinColor parseColor(String name) {
        if (name == null || name.isEmpty()) return null;

        for (CatppuccinColor c : CatppuccinColor.values()) {
            if (c.name().equalsIgnoreCase(name)) return c;
        }

        return null;
    }
}
