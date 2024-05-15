package cs1501_p5;

import java.awt.Color;
import java.lang.Math;

public class Pixel {
    private int red;
    private int green;
    private int blue;
    private int hue;
    private float saturation;
    private float value;    // also referred to as brightness

    public Pixel(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;

        // Use rgb values to obtain hsv/hsb values
        float[] hsv = new float[3];
        Color.RGBtoHSB(red, green, blue, hsv);
        this.hue = (int) Math.floor(hsv[0] * 360);
        this.saturation = hsv[1];
        this.value = hsv[2];
    }

    @Override
    public String toString() {
        return "RGB: (" + this.red + "," + this.green + "," + this.blue + "), HSV: (" + this.hue + "," + this.saturation + "," + this.value + ")";
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Pixel)) return false;
        Pixel otherPix = (Pixel) other;

        // Check RGB values
        if (this.getRed() != otherPix.getRed()) return false;
        if (this.getGreen() != otherPix.getGreen()) return false;
        if (this.getBlue() != otherPix.getBlue()) return false;

        // else
        return true;
    }

    @Override
    public int hashCode() {
        return (100 * this.red) + (10 * this.green) + (this.blue);
    }
}
