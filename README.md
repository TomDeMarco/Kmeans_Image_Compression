# CS1501 Project 5

## Goal:

To gain a better understanding of bitmap images, color quantization, and
clustering algorithms through practical implementation.

## Background:

A bitmap is an array of binary data that represents the values of the pixels in
an image. A full description of the .bmp file format can be found at
[bmp file format](https://en.wikipedia.org/wiki/BMP_file_format).

In this project, your program will be performing color quantization as defined
at [color_quantization](https://en.wikipedia.org/wiki/Color_quantization). In
order to understand color quantization, let us first consider the following two
bitmap images seen in the previous link:

![Original photo using 24-bit RGB values](imgs/Dithering_example_undithered.png "Original Photo")
![Reduced photo with 16 colors](imgs/Dithering_example_undithered_16color_palette.png "Reduced Photo")

While image on the left
([source](https://commons.wikimedia.org/wiki/File:Dithering_example_undithered.png)),
consisting of 50,000 pixels (250 x 200), and the image on the right
([source](https://en.wikipedia.org/wiki/File:Dithering_example_undithered_16color_palette.png)),
consisting of 54,000 pixels (250 x 216), are visually similar, the number
of colors used to represent each image differs greatly. The original image,
displayed on the left, uses a color palette for 24-bit RGB values (8 bits red,
8 bits green, 8 bits blue) while the image on the right is displayed using a
palette for only 16 colors which are displayed along the bottom edge of the
image.

As seen in the example above, color quantization reduces the number of distinct
colors used in an image while attempting to keep the *color reduced* image as
visually similar to the original image as possible. Color quantization is often
useful when displaying images on devices that only support a limited number of
colors and allows efficient lossy compression for bmp images.

To perform color quantization, you will produce *color maps*. A color map is a
symbol table that maps each distinct color in the original image to one of the
colors from the reduced color palette. Color quantization can proceed by
replacing each pixel’s color with the mapped color from the reduced color
palette.


## High-level description:

Your program will read in a bitmap image (`.bmp` file) and perform color
quantization on the image using two different algorithms. First, you'll use
basic bucketing to split the color space into uniformly-sized buckets, and map
each pixel in the bucket to the color at the center of the bucket. Second,
you'll use a variant of $k$-means clustering with a changeable distance
metric. You’ll implement *squared Euclidean distance* as well as *circular hue
distance*.


## Specifications:

1. You will need to implement a class named `BucketingMapGenerator` in
  `./app/src/main/java/cs1501/p5/BucketingMapGenerator.java` which implements
  the interface `ColorMapGenerator_Inter`. This class will thus include the
  method `generateColorMap` in order to return a color map.

    The color map in this implementation will be based on partitioning the color
    space into evenly-sized buckets. Specifically, consider the combined color
    (with red, green, and blue values) as a single 24-bit integer. Let the red
    value constitute the most significant 8 bits, the green value constitute the
    middle 8 bits, and the blue value constitute the least significant 8 bits.
    Then, partition this range of values (from 0 through $2^{24} - 1$) evenly.

    For instance, if 256 colors are desired, we note that $2^{24} / 256 =
    65536$. This means the $i^{th}$ bucket (starting with $i=0$) begins at
    color value $65536 \times i$. Thus, the first bucket contains any colors
    between 0 and 65,535, the next contains colors between 65,536 and 131,071,
    and so on, with the final bucket containing colors between 16,711,680 and
    16,777,215.

    The mapping, then, maps each original color to the color in the center of the
    bucket. Using the above 256-bucket example, any color in the first bucket
    would be mapped to 32768, any color in the second bucket would be mapped to
    98304, and so on. In general, the center of the $i^{th}$ bucket is $65536
    \times i + 32768$.

1. To provide distance metrics needed for $k$-means clustering, you will write
  two implementations of the interface `DistanceMetric_Inter`. Each one should
  be a stateless class: that is, it should have no attributes, only methods.

  1. `SquaredEuclideanMetric` (in
    `./app/src/main/java/cs1501/p5/SquaredEuclideanMetric.java`) will implement
    the `colorDistance` method from `DistanceMetric_Inter` using squared
    Euclidean distance. That is, for two pixels $\left(r_1, g_1, b_1 \right)$
    and $\left(r_2, g_2, b_2 \right)$, the distance would be $(r_1-r_2)^2 +
    (g_1-g_2)^2 + (b_1-b_2)^2$. A full description of square Euclidean distance
    can be found at [Squared Euclidean distance](https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance).

  1. `CircularHueMetric` (in
    `./app/src/main/java/cs1501/p5/CircularHueMetric.java`) will implement the
    `colorDistance` method from `DistanceMetric_Inter` using circular hue
    distance. Note that the provided `Pixel` class has a `getHue` method which
    returns the *hue* of the color. The hue of a color ignores saturation
    and darkness and focuses on where the color falls along the "red, orange,
    yellow, green, blue, violet" spectrum. The hue is returned as an integer
    between 0 and 359. Note that 0 is pure red, 120 is pure green, and 240
    is pure blue. More details about hue can be found at [Hue](https://en.wikipedia.org/wiki/Hue).

      Because hue is a circular spectrum (e.g., 359 is visually in between 358
      and 0), the distance should be calculated with this in mind. As an
      example, the distance between hues 50 and 90 is 40. The distance between
      hues 20 and 340 *is also 40*; rather than measuring the distance from 20
      upward to 340, it’s shorter to go from 340 upward past 360, looping around
      to 20. Thus, the distance from this metric should never be higher than
      180.

3. You will need to implement a class named `ClusteringMapGenerator` in
  `./app/src/main/java/cs1501/p5/ClusteringMapGenerator.java` which implements
  the interface `ColorMapGenerator_Inter`. This class must have a constructor
  that accepts an object that is a subtype of `DistanceMetric_Inter`:

    `public ClusteringMapGenerator(DistanceMetric_Inter metric)`

    That is, when instantiating `ClusteringMapGenerator`, one must specify an
    object that can be used (via the `colorDistance` method) to determine the
    distance between two colors, each represented as an instance of `Pixel`.

    `ClusteringMapGenerator` will perform color quantization via a variation of
    $k$-means clustering as follows.

  1. `Pixel[] generateColorPalette(Pixel[][] pixelArray, int numColors);`

      - This method computes an initial color palette (centroids) by utilizing a
        custom initialization procedure that we will call `k-means--` that is
        described as follows.

          - Pick the first initial color as the first pixel in the `.bmp` image:
            the color of (0, 0)

          - Select the remaining `numColors - 1` colors by choosing the pixel
            with the greatest computed distance from its closest existing
            centroid. Distance is computed using whichever metric was specified
            at initialization of `ClusteringMapGenerator`. On ties, the pixel
            with the highest RGB value (when considered as a single 24-bit
            integer as described above) should be chosen.

  1. `Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArray, Pixel[] initialColorPalette);`

      - This method should implement the naive $k$-means clustering algorithm
        we discussed in lecture (Lloyd’s algorithm). The `initialColorPalette`
        parameter identifies the starting $k$ centroids. This clustering
        should produce a final color palette and then return a map of each
        distinct color in `pixelArray` to its value in the final color palette.

1. You will need to implement a class named `ColorQuantizer` in
  `./app/src/main/java/cs1501/p5/ColorQuantizer.java` which implements
  `ColorQuantizer_Inter`. This class must have two constructors.

    The first constructor must accept a two-dimensional `Pixel` array which
    represents the pixels from a `.bmp` file and an object that is a subtype of
    `ColorMapGenerator_Inter`. Similarly to how you specify which distance
    metric to use in your `ColorMapGenerator`, here you can specify which type of
    `ColorMapGenerator` to use in your color quantizer class.

    `public ColorQuantizer(Pixel[][] pixelArray, ColorMapGenerator_Inter gen)`

    The second constructor must accept the name of `.bmp` file to read (as a
    `String`) and a `ColorMapGenerator_Inter` as above.

    `public ColorQuantizer(String bmpFilename, ColorMapGenerator_Inter gen)`


## Submission Guidelines:

- **DO NOT** add `./app/build/` to your git repository.
  - Leave the `./app/build.gradle` file there, however

- Be sure to remember to push the latest copy of your code back to your GitHub
  repository before submitting. To submit, log into Gradescope from Canvas and
  have GradeScope pull your repository from GitHub.


## Additional Notes/Hints:

- You can use JCL classes, or code from our official course textbook, to solve
  this project. No other outside code is permitted.

- `./app/src/java/cs1501_p5/Util.java` contains some potentially-helpful utility
  methods for reading and writing bitmap images.

## Grading Rubric

| Feature                                                                                | Points 
|----------------------------------------------------------------------------------------|--------
| Squared Euclidean `colorDistance` works correctly                                      | 5
| Cicular hue `colorDistance` works correctly                                            | 5
| `BucketingMapGenerator`'s `generateColorPalette` works correctly                       | 2
| `BucketingMapGenerator`'s `generateColorMap` works correctly                           | 5
| `ClusteringMapGenerator`'s `generateColorPalette` works correctly with Euclidean       | 10
| `ClusteringMapGenerator`'s `generateColorPalette` works correctly with Circular hue    | 10
| `ClusteringMapGenerator`'s `generateColorMap` works correctly with Euclidean           | 10
| `ClusteringMapGenerator`'s `generateColorMap` works correctly with Circular hue        | 10
| `quantizeTo2DArray` works correctly with `BucketingMapGenerator`                       | 10
| `quantizeToBMP` works correctly with `BucketingMapGenerator`                           | 3
| `quantizeTo2DArray` works correctly with `ClusteringMapGenerator` and Eucliean         | 10
| `quantizeToBMP` works correctly with `ClusteringMapGenerator` and Euclidean            | 3
| `quantizeTo2DArray` works correctly with `ClusteringMapGenerator` and Cicrular hue     | 10
| `quantizeToBMP` works correctly with `ClusteringMapGenerator` and Circular hue         | 2
| Proper assignment submission                                                           | 5
