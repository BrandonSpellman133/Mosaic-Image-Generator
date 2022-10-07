import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarverMosaic {
    private Picture currentPic; // the picture we will be altering

    // create a seam carver object based on the given picture
    public SeamCarverMosaic(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        currentPic = new Picture(picture); // deep copy duplicate of the picture given
    }

    // current picture
    public Picture picture() {
        Picture deepCopy = new Picture(currentPic);
        return deepCopy;
    }

    // width of current picture
    public int width() {
        return currentPic.width();
    }

    // height of current picture
    public int height() {
        return currentPic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x >= width() || y >= height()) throw new IllegalArgumentException();

        // standard case x's & y's needed for computation
        int xLess = x - 1;
        int xMore = x + 1;
        int yLess = y - 1;
        int yMore = y + 1;
        // if on left edge
        if (x == 0) {
            xLess = width() - 1;
        }
        // if on right edge
        if (x == (width() - 1)) {
            xMore = 0;
        }
        // if on top edge
        if (y == 0) {
            yLess = height() - 1;
        }
        // if on bottom edge
        if (y == (height() - 1)) {
            yMore = 0;
        }
        return energyCalc(x, xLess, xMore, y, yLess, yMore);
    }

    // private helper method for Calculating Energy of Pixels
    private double energyCalc(int x, int xLess, int xMore, int y, int yLess,
                              int yMore) {
        // get colors of the pixel to the right of the pixel
        Color xCol1 = currentPic.get(xMore, y);
        int xColRed = xCol1.getRed();
        int xColBlue = xCol1.getBlue();
        int xColGreen = xCol1.getGreen();
        // get colors of the pixel to the left of the pixel
        Color xCol2 = currentPic.get(xLess, y);
        int xCol2Red = xCol2.getRed();
        int xCol2Blue = xCol2.getBlue();
        int xCol2Green = xCol2.getGreen();
        // get colors of the pixel to the top of the pixel
        Color yCol1 = currentPic.get(x, yMore);
        int yColRed = yCol1.getRed();
        int yColBlue = yCol1.getBlue();
        int yColGreen = yCol1.getGreen();
        // get colors of the pixel to the bottom of the pixel
        Color yCol2 = currentPic.get(x, yLess);
        int yCol2Red = yCol2.getRed();
        int yCol2Blue = yCol2.getBlue();
        int yCol2Green = yCol2.getGreen();

        // calculate the delta across the pixel
        int rX = xColRed - xCol2Red;
        int gX = xColGreen - xCol2Green;
        int bX = xColBlue - xCol2Blue;
        int rY = yColRed - yCol2Red;
        int gY = yColGreen - yCol2Green;
        int bY = yColBlue - yCol2Blue;
        // calculate total energy of the pixel
        return Math.sqrt(rX * rX + gX * gX + bX * bX + rY * rY + gY * gY + bY * bY);
    }

    // private helper method to find seam (vertically)
    private int[] findSeam(int width, int height) {
        // Dijkstra's Algorithm
        /* @citation Adapted from: https://www.cs.princeton.edu/courses/archive/
        fall21/cos226/lectures/44ShortestPaths.pdf. Accessed 11/15/2021. */

        // 2D array stores the energies
        double[][] energy2D = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                energy2D[col][row] = energy(col, row);
            }
        }

        int numPixels = width * height;
        // used to store path with least energy
        int[] edgeTo = new int[numPixels];
        double[] energyTo = new double[numPixels];
        //Minimum-oriented indexed Priority Queue implementation using a binary heap
        IndexMinPQ<Double> pq = new IndexMinPQ<Double>(numPixels);

        // initialize all distTo[] to infinity
        for (int v = 0; v < numPixels; v++) {
            energyTo[v] = Double.POSITIVE_INFINITY;
        }
        // sets energyTo[] for all of the first row
        for (int i = 0; i < width; i++) {
            energyTo[i] = energy2D[i][0];
        }

        // Implementing Dijkstra's
        // add all pixels in top row to pq
        for (int i = 0; i < width; i++) {
            pq.insert(i, energy2D[i][0]);
        }

        while (!pq.isEmpty()) {
            int v = pq.delMin();
            // convert to Row/Column form
            int vCol = v % width;
            int vRow = v / width;
            // Connected pixels are a row below (left, center, and right)
            for (int i = -1; i < 2; i++) {
                int newRow = vRow + 1;
                int newCol = vCol + i;
                // check they are valid rows & columns
                if (newCol >= 0 && newCol < width && newRow < height) {
                    // now relax the edge of all three connected pixels
                    int newPixel = newRow * width + newCol;
                    if (energyTo[newPixel] > energyTo[v] + energy2D[newCol][newRow]) {
                        energyTo[newPixel] = energyTo[v] + energy2D[newCol][newRow];
                        edgeTo[newPixel] = v;
                        // update the PQ
                        if (!pq.contains(newPixel)) {
                            pq.insert(newPixel, energyTo[newPixel]);
                        }
                        else pq.decreaseKey(newPixel, energyTo[newPixel]);
                    }
                }
            }
        }

        // find pixel in bottom row with minimum energyTo[]
        double lowestE = Double.POSITIVE_INFINITY;
        int lowestEPix = 0;
        for (int x = 0; x < width; x++) {
            int y = height - 1;
            int sampPixl = y * width + x;
            if (energyTo[sampPixl] < lowestE) {
                lowestE = energyTo[sampPixl];
                lowestEPix = sampPixl;
            }
        }
        // start at lowestEPix, trace lowest energy path back to top row of pixels
        int[] lowestEPathTemp = new int[height];
        int nextPix = lowestEPix;
        for (int i = 0; i < height; i++) {
            lowestEPathTemp[i] = nextPix;
            nextPix = edgeTo[nextPix]; // gives 'parent' pixel
        }
        // reverse order of array & only consider x value
        int[] lowestEPath = new int[height];
        for (int i = 0; i < height; i++) {
            lowestEPath[i] = lowestEPathTemp[height - i - 1] % width;
        }
        return lowestEPath;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findSeam(width(), height());
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Picture tempHold = currentPic;
        // create new transpose image with height & width dimensions switched
        Picture transpose = new Picture(height(), width());
        // make the new picture a transpose of the original
        for (int y = 0; y < transpose.height(); y++) {
            for (int x = 0; x < transpose.width(); x++) {
                transpose.set(x, y, currentPic.get(y, x));
            }
        }
        // update currentPic to be the transpose of currentPic
        currentPic = transpose;

        // run findSeam on the transpose of currentPic
        int[] foundSeam = findSeam(width(), height());

        // return current pic to its not transposed version
        currentPic = tempHold;

        return foundSeam;
    }

    // private helper method removes seam (vertically)
    private Picture removeSeam(int[] seam, Picture baseModel) {
        Picture slimPic = new Picture(baseModel.width() - 1, baseModel.height());
        for (int y = 0; y < slimPic.height(); y++) {
            int currentXPlace = 0;
            for (int x = 0; x <= slimPic.width(); x++) {
                if (x != seam[y]) {
                    slimPic.set(currentXPlace, y, baseModel.get(x, y));
                    currentXPlace++;
                }
            }
        }
        return slimPic;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != width()) throw new IllegalArgumentException();
        // check successive entries in seam differ by a maximum of 1
        int lastPixl = -1;
        for (int i = 0; i < seam.length; i++) {
            if (lastPixl == -1) {
                lastPixl = seam[i];
            }
            else {
                if (Math.abs(seam[i] - lastPixl) > 1) {
                    throw new IllegalArgumentException();
                }
                lastPixl = seam[i];
            }
        }

        // create new transpose image with height & width dimensions switched
        Picture transpose = new Picture(height(), width());
        // make the new picture a transpose of the original
        for (int y = 0; y < transpose.height(); y++) {
            for (int x = 0; x < transpose.width(); x++) {
                transpose.set(x, y, currentPic.get(y, x));
            }
        }

        // run private helper method remove seam
        Picture slimTransposed = removeSeam(seam, transpose);


        // now reverse the transpose and update our currentPic
        // make the new picture a transpose of the original
        Picture updated = new Picture(width(), height() - 1);
        for (int y = 0; y < updated.height(); y++) {
            for (int x = 0; x < updated.width(); x++) {
                updated.set(x, y, slimTransposed.get(y, x));
            }
        }
        currentPic = updated;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != height()) throw new IllegalArgumentException();
        // check successive entries in seam differ by a maximum of 1
        int lastPixl = -1;
        for (int i = 0; i < seam.length; i++) {
            if (lastPixl == -1) {
                lastPixl = seam[i];
            }
            else {
                if (Math.abs(seam[i] - lastPixl) > 1) {
                    throw new IllegalArgumentException();
                }
                lastPixl = seam[i];
            }
        }

        // replace currentPic with updated tempPic
        currentPic = removeSeam(seam, currentPic);
    }

    //  unit testing (required)
    public static void main(String[] args) {
        Picture thisPic = new Picture(args[0]);
        SeamCarverMosaic tester = new SeamCarverMosaic(thisPic);
        System.out.println(Arrays.toString(tester.findVerticalSeam()));
        System.out.println(tester.energy(0, 0));
        System.out.println(tester.picture().height() == tester.height());
        System.out.println(tester.picture().width() == tester.width());
        System.out.println("Vertical Seam: " +
                                   Arrays.toString(tester.findVerticalSeam()));
        tester.removeVerticalSeam(tester.findVerticalSeam());
        System.out.println("Horizontal Seam: " +
                                   Arrays.toString(tester.findHorizontalSeam()));
        tester.removeHorizontalSeam(tester.findHorizontalSeam());
    }
}
