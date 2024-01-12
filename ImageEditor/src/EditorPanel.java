import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class EditorPanel extends JPanel implements KeyListener{

    Color[][] pixels;
    
    public EditorPanel() {
        BufferedImage imageIn = null;
        try {
            // the image should be in the main project folder, not in \src or \bin
            imageIn = ImageIO.read(new File("duck2.jpg"));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length, pixels.length));
        setBackground(Color.BLACK);
        addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        // paints the array pixels onto the screen
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
    }

    public void run() {
        // call your image-processing methods here OR call them from keyboard event
        // handling methods
        // write image-processing methods as pure functions - for example: pixels =
        
        
        pixels = blur(pixels);

        repaint();
    }

    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        // System.out.println("Loaded image: width: " +width + " height: " + height);
        return result;
    }
    public static Color[][] flipHorizontal(Color[][] image){
        Color[][] flippedHoriz = new Color[image.length][image[0].length];
        for (int r = 0; r < image.length; r++){
            for (int c = 0; c < image[r].length; c++){
                flippedHoriz[r][image[0].length - 1 -c] = image[r][c];
            }
        }
        return flippedHoriz;
    }

    public static Color[][] flipVertical(Color[][] image){
        Color[][] flippedVert = new Color[image.length][image[0].length];
        for (int r = 0; r < image.length; r++){
            for (int c = 0; c < image[r].length; c++){
                flippedVert[image.length - 1 - r][c] = image[r][c];
            }
        }
        return flippedVert;
    }

    public static Color[][] grayScale(Color[][] image){
        final double RED_FACTOR = .299;
        final double GREEN_FACTOR = .587;
        final double BLUE_FACTOR = .114;
        Color[][] newGray = new Color[image.length][image[0].length];
        for (int r = 0; r < image.length; r++){
            for (int c = 0; c < image[r].length; c++){
                int red = image[r][c].getRed();
                int green = image[r][c].getGreen();
                int blue = image[r][c].getBlue();
                int gray = (int) ((red * RED_FACTOR) + (green * GREEN_FACTOR) + (blue * BLUE_FACTOR));
                newGray[r][c] = new Color(gray, gray, gray);
            }
        }
        return newGray;
    }

    public static Color[][] contrast(Color[][] image){
        final double DECREASED_CONTRAST_FACTOR = 0.5;
        final double INCREASED_CONTRAST_FACTOR = 1.5;
        final int COLOR_MIDDLE = 127;
        final int COLOR_MIN_VALUE = 0;
        final int COLOR_MAX_VALUE = 255;
        Color[][] contrast = new Color[image.length][image[0].length];
        for (int r = 0; r < image.length; r++){
            for (int c = 0; c < image[r].length; c++){
                int red = image[r][c].getRed();
                int green = image[r][c].getGreen();
                int blue = image[r][c].getBlue();
                if (image[r][c].getRed() >= COLOR_MIDDLE){
                    red = Math.min((int)(red * INCREASED_CONTRAST_FACTOR), COLOR_MAX_VALUE);
                } else {
                    red = Math.max((int)(red * DECREASED_CONTRAST_FACTOR), COLOR_MIN_VALUE);
                }
                if (image[r][c].getGreen() >= COLOR_MIDDLE){
                    green = Math.min((int)(green * INCREASED_CONTRAST_FACTOR), COLOR_MAX_VALUE);
                } else {
                    green = Math.max((int)(green * DECREASED_CONTRAST_FACTOR), COLOR_MIN_VALUE);
                }
                if (image[r][c].getBlue() >= COLOR_MIDDLE){
                    blue = Math.min((int)(blue * INCREASED_CONTRAST_FACTOR), COLOR_MAX_VALUE);
                } else {
                    blue = Math.max((int)(blue * DECREASED_CONTRAST_FACTOR), COLOR_MIN_VALUE);
                }
                contrast[r][c] = new Color(red, green, blue);
            }
        }
        return contrast;
    }

    public static Color[][] posterize(Color[][] image){
        final Color DARK_BLUE = new Color(3, 30, 70);
        final Color LIGHT_BLUE = new Color(60,140,250);
        final Color RED = new Color(230,20,10);
        final Color CREAM = new Color(230,220,190);
        Color[][] posterize = new Color[image.length][image[0].length];
        for (int r = 0; r < image.length; r++){
            for (int c = 0; c < image[r].length; c++){
                Color point = image[r][c];
                if ((distance(point, DARK_BLUE) < distance(point, LIGHT_BLUE))
                        && (distance(point, DARK_BLUE) < distance(point, RED)) &&
                        (distance(point, DARK_BLUE) < distance(point, CREAM))) {
                    posterize[r][c] = DARK_BLUE;
                } else if ((distance(point, LIGHT_BLUE) < distance(point, DARK_BLUE))
                        && (distance(point, LIGHT_BLUE) < distance(point, RED)) &&
                        (distance(point, LIGHT_BLUE) < distance(point, CREAM))) {
                    posterize[r][c] = LIGHT_BLUE;
                } else if ((distance(point, RED) < distance(point, DARK_BLUE))
                        && (distance(point, RED) < distance(point, LIGHT_BLUE)) &&
                        (distance(point, RED) < distance(point, CREAM))) {
                    posterize[r][c] = RED;
                } else {
                    posterize[r][c] = CREAM;
                }
            }
        }
        return posterize;
    }

    public static double distance(Color x, Color testPoint){
        double distanceR = Math.pow(((double)(x.getRed())) - (double)(testPoint.getRed()) , 2);
        double distanceG = Math.pow(((double)(x.getGreen())) - (double)(testPoint.getGreen()) , 2);
        double distanceB = Math.pow(((double)(x.getBlue())) - (double)(testPoint.getBlue()) , 2);
        double distanceTotal = Math.sqrt(distanceR + distanceG + distanceB);
        return distanceTotal;
    }

    public static Color[][] blur(Color[][] image){
        final int RADIUS = 12;
        Color[][] blurred = new Color[image.length][image[0].length];
        for (int r = 0; r < image.length; r++){
            for (int c = 0; c < image[r].length; c++){
                int overallRed = 0;
                int overallGreen = 0;
                int overallBlue = 0;
                int count = 0;
                for (int i = r - RADIUS; i <= r + RADIUS; i++){
                    for (int j = c - RADIUS; j <= c + RADIUS; j++){
                        if (i >= 0 && i <= blurred.length && j >= 0 && j <= blurred[0].length){
                            overallRed += image[i][j].getRed();
                            overallGreen += image[i][j].getGreen();
                            overallBlue += image[i][j].getBlue();
                            count++;
                        }
                    }
                }
                int averageRed = overallRed / count;
                int averageGreen = overallGreen / count;
                int averageBlue = overallBlue / count;
                blurred[r][c] = new Color(averageRed, averageGreen, averageBlue);
            }
        }
        return blurred;
    } 

    


    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == 'h'){
            pixels = flipHorizontal(pixels);
        }
        if(e.getKeyChar() == 'v'){
            pixels = flipVertical(pixels);
        }
        if(e.getKeyChar() == 'g'){
            pixels = grayScale(pixels);
        }
        if(e.getKeyChar() == 'c'){
            pixels = contrast(pixels);
        }
        if(e.getKeyChar() == 'p'){
            pixels = posterize(pixels);
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
}

