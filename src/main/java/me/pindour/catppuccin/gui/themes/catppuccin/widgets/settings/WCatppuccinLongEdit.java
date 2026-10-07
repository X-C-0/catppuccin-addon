package me.pindour.catppuccin.gui.themes.catppuccin.widgets.settings;
//? if >=26.2 {
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinTextBox;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;

public class WCatppuccinLongEdit extends WHorizontalList implements CatppuccinWidget {
    private final CharFilter FILTER = (text, c) -> Character.isDigit(c) || c == '-' && !text.contains("-");

    private long value;
    public final long min, max;
    public Runnable action;

    private final String title;
    private WCatppuccinTextBox textBox;

    public WCatppuccinLongEdit(String title, long value, long min, long max) {
        this.title = title;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public void init() {
        textBox = (WCatppuccinTextBox) add(theme().textBox(Long.toString(value), title, FILTER))
                .expandX()
                .widget();

        textBox.actionOnUnfocused = () -> {
            long lastValue = value;

            try {
                long parsed = Long.parseLong(textBox.get().trim());
                if (parsed >= min && parsed <= max) value = parsed;
            } catch (NumberFormatException _) { }

            textBox.set(Long.toString(value));

            if (action != null && value != lastValue) action.run();
        };
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
        textBox.set(Long.toString(value));
    }
}
//? }