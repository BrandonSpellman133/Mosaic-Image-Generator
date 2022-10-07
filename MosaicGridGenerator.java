import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MosaicGridGenerator {
    private BufferedImage forePic; // the picture we will be altering
    //private Picture backPic; // the background pic to superimpose
    //private double ratio; // ratio of foreground to background
    private BufferedImage[] picsList;
    private BufferedImage grid;
    private BufferedImage combinedResult;

    // create a mosaic object based on the given picture
    public MosaicGridGenerator(BufferedImage foreground, int arraySize) {
        if (foreground == null) throw new IllegalArgumentException();
        forePic = foreground;

        if (arraySize == 0) throw new IllegalArgumentException();
        picsList = new BufferedImage[arraySize];
    }

    public void arrayFiller(String[] pics) throws IOException {
        for (int i = 0; (i + 2) < pics.length; i++) {
            File file = new File(pics[i + 2]);
            picsList[i] = ImageIO.read(file);
        }

    }

    public void arrayDisplay() {
        for (int i = 0; i < picsList.length; i++) {
            ImageIcon imageIcon = new ImageIcon(picsList[i]);
            JFrame jFrame = new JFrame();

            jFrame.setLayout(new FlowLayout());

            jFrame.setSize(picsList[i].getWidth(), picsList[i].getHeight());
            JLabel jLabel = new JLabel();

            jLabel.setIcon(imageIcon);
            jFrame.add(jLabel);
            jFrame.setVisible(true);

            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public void resizeImages(int numRows) {
        // find smallest x and smallest y dimensions in the array
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (int i = 0; i < picsList.length; i++) {
            minX = Math.min(minX, picsList[i].getWidth());
            minY = Math.min(minY, picsList[i].getHeight());
        }

        // divide foreground picture pixel width by number of rows
        //System.out.println(minX);
        //System.out.println(minY);
        //System.out.println(forePic.width());
        int gridWidth = forePic.getWidth() / numRows; //rounded down
        //System.out.println(gridWidth);
        double ratioYX = (double) minY / (double) minX;
        //System.out.println(ratioYX);
        double decimalGridHeight = (double) gridWidth * ratioYX;
        //System.out.println(decimalGridHeight);
        int gridHeight = (int) decimalGridHeight; //ratio width to height
        //System.out.println(gridHeight);

        int numCols = (forePic.getHeight() / gridHeight); // rounded down

        // now crop all images in the array to be the same size
        for (int i = 0; i < picsList.length; i++) {
            picsList[i] = picsList[i].getSubimage(0, 0, minX, minY);
            Image img = picsList[i].getScaledInstance(gridWidth, gridHeight, 1);

            // Create a buffered image with transparency
            BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null),
                                                     BufferedImage.TYPE_INT_ARGB);

            // Draw the image on to the buffered image
            Graphics2D bGr = bimage.createGraphics();
            bGr.drawImage(img, 0, 0, null);
            bGr.dispose();

            // Return the buffered image
            picsList[i] = bimage;
        }


        //create new image with the dimensions of the grid
        grid = new BufferedImage(gridWidth * numRows, gridHeight * numCols,
                                 BufferedImage.TYPE_INT_ARGB);

        int arrayPlacekeeper = 0;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // Draw the image on to the buffered image
                Graphics2D bGr = grid.createGraphics();
                bGr.drawImage(picsList[arrayPlacekeeper], col * gridWidth, row * gridHeight, null);
                bGr.dispose();
                arrayPlacekeeper++;
                if (arrayPlacekeeper == picsList.length) arrayPlacekeeper = 0;
            }
        }
        // for (int i = 0; i < picsList.length; i++) {
        // Draw the image on to the buffered image
        //    Graphics2D bGr = grid.createGraphics();
        //    bGr.drawImage(picsList[i], i * gridWidth, 0, null);
        //    bGr.dispose();
        //}

        // assemble the grid
        //int arrayPlacekeeper = 0;
        //for (int row = 1; row <= numRows; row++) {
        //    for (int col = 0; col <= numCols; col++) {


        //        for (int xPixel = 0; xPixel < gridWidth; xPixel++) {
        //            for (int yPixel = 0; yPixel < gridHeight; yPixel++) {
        //                grid.setRGB(xPixel, yPixel, currentPic.get(y, x))
        //            }
        //        }


        //    }
        //}
    }

    public void gridDisplay() {
        ImageIcon imageIcon = new ImageIcon(grid);
        JFrame jFrame = new JFrame();

        jFrame.setLayout(new FlowLayout());

        jFrame.setSize(grid.getWidth(), grid.getHeight());
        JLabel jLabel = new JLabel();

        jLabel.setIcon(imageIcon);
        jFrame.add(jLabel);
        jFrame.setVisible(true);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void combine(double ratio) {

        if (ratio > 100 || ratio <= 0) throw new IllegalArgumentException();

        int combineWidth = Math.max(forePic.getWidth(), grid.getWidth());
        int combineHeight = Math.max(forePic.getHeight(), grid.getWidth());

        BufferedImage combined = new BufferedImage(combineWidth, combineHeight,
                                                   BufferedImage.TYPE_INT_ARGB);

        for (int col = 0; col < combineWidth; col++) {
            for (int row = 0; row < combineHeight; row++) {
                // get colors of the pixel in the foreground
                Color foreColor = new Color(forePic.getRGB(col, row));
                // int foreAlpha = foreColor.getAlpha();
                int foreRed = foreColor.getRed();
                int foreBlue = foreColor.getBlue();
                int foreGreen = foreColor.getGreen();
                // get colors of the pixel in the background
                Color backColor = new Color(grid.getRGB(col, row));
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
                combined.setRGB(col, row, thisColor.getRGB());
            }
        }
        combinedResult = combined;
    }

    public void finalDisplay() {
        ImageIcon imageIcon = new ImageIcon(combinedResult);
        JFrame jFrame = new JFrame();

        jFrame.setLayout(new FlowLayout());

        jFrame.setSize(combinedResult.getWidth(), combinedResult.getHeight());
        JLabel jLabel = new JLabel();

        jLabel.setIcon(imageIcon);
        jFrame.add(jLabel);
        jFrame.setVisible(true);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void sizeDown(int factor) {
        Image img = combinedResult.getScaledInstance(combinedResult.getWidth() / factor,
                                                     combinedResult.getHeight() / factor, 1);

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null),
                                                 BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        combinedResult = bimage;
    }

    //NOT TESTED YET
    public void BuffImageToFile() throws IOException {
        File outputfile = new File("THISISMYCOSPROJECT.jpg");
        ImageIO.write(combinedResult, "jpg", new File("c:\\test\\image.jpg"));
    }

    public static void main(String[] args) throws IOException {
        //System.out.println("Upload foreground picture:");
        File file = new File(args[0]);
        BufferedImage foreground = ImageIO.read(file);
        //System.out.println("How many background images would you like?");
        int arraySize = Integer.parseInt(args[1]);

        MosaicGridGenerator tester = new MosaicGridGenerator(foreground, arraySize);
        tester.arrayFiller(args);
        tester.arrayDisplay();

        tester.resizeImages(8);
        tester.arrayDisplay();

        //tester.resizeImages(3);
        //tester.arrayDisplay();

        tester.gridDisplay();

        //tester.combine().show();

        //BufferedImage test = new BufferedImage(new File("10x10.png"));
        //test.photoArray(1);
        tester.combine(70);
        tester.sizeDown(2);
        tester.finalDisplay();

    }
}
