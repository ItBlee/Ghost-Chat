package com.itblee.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AnimatedImage extends JComponent {

    private ImageIcon[] images;

    private Timer timer;

    private final int totalImages;
    private int currentImage;
    private int loop;
    private int loopCount;
    private final int delay;

    public AnimatedImage(ImageIcon[] images) {
        this(images, 33);
    }

    public AnimatedImage(ImageIcon[] images, int delay) {
        this.images = images;
        this.totalImages = images.length;
        currentImage = 0;
        loop = 0;
        loopCount = 0;
        this.delay = delay;
    }

    public AnimatedImage(ImageIcon[] images, int delay, int loop) {
        this(images, delay);
        this.loop = loop;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timer == null || !timer.isRunning())
            return;
        if (images[currentImage].getImageLoadStatus() == MediaTracker.COMPLETE) {
            images[currentImage].paintIcon(this, g, 0, 0);
            currentImage = (currentImage + 1) % totalImages;
        }
    }

    private void paintImage() {
        repaint();
        if (currentImage == 0 && loop > 0) {
            loopCount++;
            if (loopCount == loop)
                stopAnimation();
        }
    }

    public void startAnimation() {
        currentImage = 0;
        if (timer == null) {
            timer = new Timer(delay, e -> paintImage());
            timer.start();
        } else if (!timer.isRunning())
            timer.restart();
    }

    public void stopAnimation() {
        if (timer != null)
            timer.stop();
    }

    public void waitFinish() {
        if (loop == 0)
            throw new IllegalThreadStateException();
        while (timer != null && timer.isRunning()) {
            try {
                Thread.sleep(timer.getDelay());
            } catch (InterruptedException ignored) {}
        }
    }

    public void setImages(ImageIcon[] images) {
        this.images = images;
    }
}
