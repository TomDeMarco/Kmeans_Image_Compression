package cs1501_p5;
public class SquaredEuclideanMetric implements DistanceMetric_Inter{
    public double colorDistance(Pixel p1, Pixel p2){
        int redDiff = (p1.getRed() - p2.getRed());
        int redDiffSq = redDiff * redDiff;

        int greenDiff = (p1.getGreen() - p2.getGreen());
        int greenDiffSq = greenDiff * greenDiff;

        int blueDiff = (p1.getBlue() - p2.getBlue());
        int blueDiffSq = blueDiff * blueDiff;

        return redDiffSq + greenDiffSq + blueDiffSq;

    }
}