package cs1501_p5;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

public class ColorQuantizer implements ColorQuantizer_Inter {

        //instance variables
        Pixel[][] thisImage;
        ColorMapGenerator_Inter typeOfGenerator;

        //constructor
        public ColorQuantizer(Pixel[][] image, ColorMapGenerator_Inter chosenTypeOfGenerator){
            this.thisImage = image;
            this.typeOfGenerator =chosenTypeOfGenerator;
        }

        //create alternate constructor that takes file as input instead of image
        public ColorQuantizer(String bmpFilename, ColorMapGenerator_Inter chosenTypeOfGenerator){
                try {
        // Load bitmap image
        BufferedImage image = ImageIO.read(new File(bmpFilename));

        // Create pixel matrix
        Pixel[][] pixelMatrix = convertBitmapToPixelMatrix(image);

        // Set instance variables
        this.thisImage = pixelMatrix;
        this.typeOfGenerator = chosenTypeOfGenerator;

        // Handle the exception (e.g., throw it or set default values)
        } catch (IOException e) {
            e.printStackTrace();
        }
        }



    /**
     * Performs color quantization. If numColors is less than 1, this method throws an
     * IllegalArgumentException.
     *
     * @param numColors number of colors to use for color quantization
     * @return A two dimensional array where each index represents the pixel from the original bitmap
     *         image and contains Pixel representing its color after quantization
     * @throws java.lang.IllegalArgumentException if numColors is less than 1
     */
    public Pixel[][] quantizeTo2DArray(int numColors) throws IllegalArgumentException{
        if(numColors < 1){
            throw new IllegalArgumentException();
        }


        //create pallete
        Pixel[] colorPalette = typeOfGenerator.generateColorPalette(this.thisImage,numColors);

        //create color map
        Map<Pixel,Pixel> colorMap = new HashMap<>();
        colorMap = typeOfGenerator.generateColorMap(thisImage,colorPalette);

        //create new image using the color map
        Pixel[][] newImage = new Pixel[this.thisImage.length][this.thisImage[0].length];
        
        for(int i =0; i<this.thisImage.length;i++){
            for(int j=0;j<this.thisImage[0].length;j++){
                Pixel originalPixel = this.thisImage[i][j];
                Pixel newPixel = colorMap.get(originalPixel);
                newImage[i][j]= newPixel;
            }
        }

        return newImage;

    }

    /**
     * Performs color quantization (but saves to a file!). Should perform quantization like
     * quantizeToArray, but instead of returning a 2D Pixel array, returns nothing and writes
     * the resulting image to a file. If numColors is less than 1, this method throws an
     * IllegalArgumentException
     *
     * @param numColors number of colors to use for color quantization
     * @param fileName File to write resulting image to
     * @throws java.lang.IllegalArgumentException if numColors is less than 1
     */
    public void quantizeToBMP(String fileName, int numColors) throws IllegalArgumentException{
        if(numColors<1){
            throw new IllegalArgumentException();
        }

        Pixel[][] newImage =quantizeTo2DArray(numColors);
        savePixelMatrixToFile(fileName, newImage);
    }


    //helper methods--------------------------------------------------------------------------------

    public static Pixel[][] convertBitmapToPixelMatrix(BufferedImage image) {
         Pixel[][] pixelMatrix = new Pixel[image.getWidth()][image.getHeight()];

         for (int x = 0; x < image.getWidth(); x++) {
             for (int y = 0; y < image.getHeight(); y++) {
                 int rgb = image.getRGB(x, y);
                 int red = (rgb >> 16) & 0xFF;
                 int green = (rgb >> 8) & 0xFF;
                 int blue = rgb & 0xFF;
                 pixelMatrix[x][y] = new Pixel(red, green, blue);
             }
         }

         return pixelMatrix;
    }

    public static void savePixelMatrixToFile(String filePath, Pixel[][] matrix) {

         try {
             // Open file for writing
             BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

             // Write matrix to file
             for (int i = 0; i < matrix.length; i++) {
                 for (int j = 0; j < matrix[i].length; j++) {
                     writer.write(matrix[i][j] + String.valueOf('\t'));
                 }
                 writer.newLine();
             }

             // Close file
             writer.close();

         } catch (IOException e) {
             e.printStackTrace();
         }
}
}
