package fr.delcey.colorhuefastscrollrecyclerview_poc;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

public class Hue implements Comparable<Hue> {

    private float hue;
    private float saturation;
    private float value;

    private final String name;

    public Hue(float[] hsv, String name) {
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.value = hsv[2];

        this.name = name;
    }

    @ColorInt
    public int getColor() {
        return Color.HSVToColor(new float[]{hue, saturation, value});
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NonNull Hue o) {
        int hueCompare = Float.compare(o.hue, hue);

        if (hueCompare != 0) {
            return hueCompare;
        }

        int saturationCompare = Float.compare(o.saturation, saturation);
        if (saturationCompare != 0) {
            return saturationCompare;
        }

        return Float.compare(o.value, value);
    }
}
