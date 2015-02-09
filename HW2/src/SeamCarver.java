/**
 * Created by lb_k on 3/28/14.
 */

import java.awt.Color;

public class SeamCarver {
    private int w, h;
    private static final int RED = 255, GREEN = 255 << 8, BLUE = 255 << 16;
    private energy[][], mask[][];
    
    private boolean isRotated = false, bOnce = false;

    public SeamCarver(Picture picture) {
        w = picture.width();
        h = picture.height();
        energy  = new int[w][h];
        mask    = new int[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Color col = picture.get(x, y);
            	mask[x][y] = col.getRed() + (col.getGreen() << 8) + (col.getBlue() << 16);
            }
        }

        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                energy[x][y] = getEnergy(x, y);
    }

    private int getEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == w-1 || y == h-1)
            return 195075;

        int L, R, U, D;
        L = mask[x-1][y];
        R = mask[x+1][y];
        U = mask[x][y-1];
        D = mask[x][y+1];

        int Rx = ((R & RED)   - (L & RED))       , Ry = ((D & RED)    - (U & RED));         //Red
        int Gx = ((R & GREEN) - (L & GREEN)) >> 8, Gy = ((D & GREEN)  - (U & GREEN)) >> 8;  //Green
        int Bx = ((R & BLUE)  - (L & BLUE)) >> 16, By = ((D & BLUE)   - (U & BLUE)) >> 16;  //Blue

        return Rx*Rx + Ry*Ry + Gx*Gx + Gy*Gy + Bx*Bx + By*By;
    }

    public Picture picture() {
        if (isRotated)
            rotate();

        Picture pic = new Picture(w, h);
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                pic.set(x, y, new Color(mask[x][y] & 255, (mask[x][y] & 255 << 8) >> 8, (mask[x][y] & 255 << 16) >> 16));
        return pic;
    }
    public     int width() {
        if (!isRotated)
            return w;
        return h;
    }
    public     int height() {
        if (!isRotated)
            return h;
        return w;
    }
    public  double energy(int x, int y) {
        if (isRotated)
            rotate();

        if (x < 0 || x >= w || y < 0 || y >= h)
            throw new IndexOutOfBoundsException();
        return (double) (energy[x][y]);
    }

    private void rotate() {
        int[][] newEnergy 	= new int[h][w];;
        int[][] newMask 	= new int[h][w];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                newEnergy[j][i] = energy[i][j];
                newMask[j][i] = mask[i][j];
            }
        }

        energy = newEnergy;
        mask   = newMask;

        int tmp = w;
        w = h;
        h = tmp;

        isRotated = !isRotated;
    }

    public int[] findHorizontalSeam() {
        if (!bOnce && isRotated)
            rotate();

        int[] seam = new int[w]; //by dynamic programming
        int[][] s = new int[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (x == 0)
                    s[x][y] = energy[x][y];
                else {
                    int min = s[x-1][y];
                    if (y > 0 && min > s[x-1][y-1])
                        min = s[x-1][y-1];
                    if (y < h-1 && min > s[x-1][y+1])
                        min = s[x-1][y+1];
                    s[x][y] = min + energy[x][y];
                }
            }
        }

        int tmp = 0;
        int Y = 0;
        for (int x = w-1; x >= 0; x--) { //reconstruct the path
            if (x == w-1) {
                int min = Integer.MAX_VALUE;
                for (int y = 0; y < h; y++) {
                    if (min > s[x][y]) {
                        Y = y;
                        min = s[x][y];
                    }
                }
                tmp = s[x][Y];
            }
            else {
                if ((Y > 0) && (s[x][Y-1] == tmp))
                    Y--;
                else if ((Y < h-1) && (s[x][Y+1] == tmp))
                    Y++;
            }

            tmp -= energy[x][Y];
            seam[x] = Y;
        }

        bOnce = false;
        return seam;
    }

    public int[] findVerticalSeam() {
        if (!isRotated)
            rotate();

        bOnce = true;
        return findHorizontalSeam();
    }

    public void removeHorizontalSeam(int[] seam) {
        if (!bOnce && isRotated)
            rotate();

    	if (seam.length != w)
    		throw new java.lang.IllegalArgumentException();

        for (int x = 0; x < w; x++) {
            for (int y = seam[x]+1; y < h; y++) {
                mask[x][y-1] = mask[x][y];
                energy[x][y-1] = energy[x][y];
            }
        }

        h--;

        for (int x = 0; x < w; x++) {
        	int Y = seam[x];
        	for (int y = Y-1; y < Y+1; y++) {
        		if (y >= 0 && y < h)
        			energy[x][y] = getEnergy(x, y);
        	}
        }

        bOnce = false;
    }

    public void removeVerticalSeam(int[] seam) {
        if (!isRotated)
            rotate();

        bOnce = true;
        removeHorizontalSeam(seam);
    }

    public static void main(String[] args) {
        /*StdOut.println(9 & 4 - 4 & 5);
        SeamCarver sc = new SeamCarver(new Picture(args[0]));
        sc.findVerticalSeam();
        PrintSeams.printVerticalSeam(sc);
        sc.removeVerticalSeam(sc.findVerticalSeam());
        sc.findVerticalSeam();
        PrintSeams.printVerticalSeam(sc);*/
    }

}
