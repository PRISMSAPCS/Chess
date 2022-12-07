package utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;

public class ImagePanel extends JPanel {
    private BufferedImage dspImg;
    private BufferedImage origImg;
    private int owid, ohei;
    private double aspRate;

    public ImagePanel(String path) {
        try {
            dspImg = ImageIO.read(new File(path));
            origImg = ImageIO.read(new File(path));
            owid = origImg.getWidth();
            ohei = origImg.getHeight();
            aspRate = (double) owid / (double) ohei;
        } catch (IOException ex) {
            System.out.println("exception thrown when opening image file: " + ex.getMessage());
            // handle exception...
        }
    }

    public void resize(int wid, int hei) {
        Image tmp = origImg.getScaledInstance(wid, hei, Image.SCALE_SMOOTH);
        dspImg = new BufferedImage(wid, hei, BufferedImage.TYPE_INT_ARGB);
        dspImg.getGraphics().drawImage(tmp, 0, 0, null);
    }

    public void resize(Dimension d) {
        resize(d.width, d.height);
    }

    public ImagePanel(String path, int wid, int hei) {
        try {
            dspImg = ImageIO.read(new File(path));
            origImg = ImageIO.read(new File(path));
            owid = origImg.getWidth();
            ohei = origImg.getHeight();
            aspRate = (double) owid / (double) ohei;
        } catch (IOException ex) {
            // handle exception...
        }
        resize(wid, hei);
    }

    public BufferedImage getDspImg() {
        return dspImg;
    }

    public void setDspImg(BufferedImage dspImg) {
        this.dspImg = dspImg;
    }

    public BufferedImage getOrigImg() {
        return origImg;
    }

    public void setOrigImg(BufferedImage origImg) {
        this.origImg = origImg;
    }

    public int getOwid() {
        return owid;
    }

    public void setOwid(int owid) {
        this.owid = owid;
    }

    public int getOhei() {
        return ohei;
    }

    public void setOhei(int ohei) {
        this.ohei = ohei;
    }

    public double getAspRate() {
        return aspRate;
    }

    public void setAspRate(double aspRate) {
        this.aspRate = aspRate;
    }

    public BufferedImage getBufImg() {
        return dspImg;
    }

    public void setBufImg(BufferedImage bufImg) {
        this.dspImg = bufImg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("repaint called");
        super.paintComponent(g);
        Dimension dependW = new Dimension(getWidth(), (int) Math.round((double) getWidth() / aspRate));
        Dimension dependH = new Dimension((int) Math.round((double) getHeight() * aspRate), getHeight());

        if (dependW.height <= getHeight()) {
            // resize the image but make sure that the asp ratio is correct
            resize(dependW);
        } else {
            resize(dependH);
        }

        // default is to center the image
        int wOff = getWidth() - dspImg.getWidth();
        int hOff = getHeight() - dspImg.getHeight();

        g.drawImage(dspImg, (int) Math.round((double) wOff / 2.0), (int) Math.round((double) hOff / 2.0), this);
    }

    @Override
    public Dimension getPreferredSize() {
        System.out.println("getPreferredSize called");
        return new Dimension(owid, ohei);
    }
}
