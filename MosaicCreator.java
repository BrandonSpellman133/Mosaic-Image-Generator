import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class MosaicCreator {
    private Picture forePic; // the picture we will be altering
    private Picture backPic; // the background pic to superimpose
    private double ratio; // ratio of foreground to background
    private BufferedImage[] picsList;

    // create a mosaic object based on the given picture
    public MosaicCreator(Picture foregroundPic, Picture backgroundPic, double percent) {
        if (foregroundPic == null) throw new IllegalArgumentException();
        if (backgroundPic == null) throw new IllegalArgumentException();
        backPic = new Picture(backgroundPic); // deep copy duplicate of the picture given
        forePic = new Picture(foregroundPic); // deep copy duplicate of the picture given

        // check that percentage of foreground shown is between 1 and 100 percent
        if (percent > 100 || percent <= 0) throw new IllegalArgumentException();
        ratio = percent;
    }


    public Picture combine() {
        int combineWidth = Math.max(forePic.width(), backPic.width());
        int combineHeight = Math.max(forePic.height(), backPic.height());
        Picture combined = new Picture(combineWidth, combineHeight);

        for (int col = 0; col < combineWidth; col++) {
            for (int row = 0; row < combineHeight; row++) {
                // get colors of the pixel in the foreground
                Color foreColor = forePic.get(col, row);
                // int foreAlpha = foreColor.getAlpha();
                int foreRed = foreColor.getRed();
                int foreBlue = foreColor.getBlue();
                int foreGreen = foreColor.getGreen();
                // get colors of the pixel in the background
                Color backColor = backPic.get(col, row);
                // int backAlpha = backColor.getAlpha();
                int backRed = backColor.getRed();
                int backBlue = backColor.getBlue();
                int backGreen = backColor.getGreen();

                double forePercent = ratio / 100;
                double backPercent = (100 - ratio) / 100;

                // int comboAlpha = (int) ((foreAlpha * forePercent) + (backAlpha * backPercent));
                int comboRed = (int) ((foreRed * forePercent) + (backRed * backPercent));
                int comboBlue = (int) ((foreBlue * forePercent) + (backBlue * backPercent));
                int comboGreen = (int) ((foreGreen * forePercent) + (backGreen * backPercent));

                Color thisColor = new Color(comboRed, comboGreen, comboBlue);
                combined.set(col, row, thisColor);
            }
        }
        return combined;
    }

    // generates an array based on how many pictures you want to upload
    public void arrayGen(int arraySize) {
        if (arraySize <= 0) throw new IllegalArgumentException();
        BufferedImage[] picsList = new BufferedImage[arraySize];
    }


    // private helper method
    //private BufferedImage gridAssemble(int gridWidth, int gridHeight, BufferedImage[] picsList) {
    //    return picsList;
    //}


    public void resizeImages(int numRows) {
        // find smallest x and smallest y dimensions in the array
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (int i = 0; i < picsList.length; i++) {
            minX = Math.min(minX, picsList[i].getTileWidth());
            minY = Math.min(minY, picsList[i].getTileHeight());
        }

        // divide foreground picture pixel width by number of rows
        int gridWidth = forePic.width() / numRows; //rounded down
        int gridHeight = gridWidth * (minY / minX); //ratio width to height

        int numCols = forePic.height() / gridHeight; // rounded down

        // now crop all images in the array to be the same size
        for (int i = 0; i < picsList.length; i++) {
            picsList[i] = picsList[i].getSubimage(0, 0, minX, minY);
            picsList[i] = (BufferedImage) picsList[i].getScaledInstance(gridWidth, gridHeight, 2);
        }

        //create new image with the dimensions of the grid
        BufferedImage grid = new BufferedImage(gridWidth * numRows, gridHeight * numCols,
                                               BufferedImage.TYPE_INT_RGB);

        // assemble the grid
        int arrayPlacekeeper = 0;
        for (int row = 1; row <= numRows; row++) {
            for (int col = 0; col <= numCols; col++) {


                for (int xPixel = 0; xPixel < gridWidth; xPixel++) {
                    for (int yPixel = 0; yPixel < gridHeight; yPixel++) {
                        grid.setRGB(xPixel, yPixel, currentPic.get(y, x))
                    }
                }


            }
        }


    }


    public static void main(String[] args) {
        System.out.println("How many photos do you want to input?");
        System
        Picture fore = new Picture(args[0]);
        Picture back = new Picture(args[1]);
        int ratio = Integer.parseInt(args[2]);

        MosaicCreator tester = new MosaicCreator(fore, back, ratio);

        tester.combine().show();

    }
}
