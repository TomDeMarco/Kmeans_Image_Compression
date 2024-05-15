package cs1501_p5;
public class CircularHueMetric implements DistanceMetric_Inter{
    public double colorDistance(Pixel p1, Pixel p2){
        int hue1 = p1.getHue();
        int hue2 = p2.getHue();
        int distance = Math.abs(hue1 - hue2) % 360;
        return Math.min(distance, 360 - distance);

    }
}