package com.itblee.gui.component;

import javax.swing.*;
import java.awt.*;

public class AnimatedImage extends JComponent {

    private ImageIcon[] images;

    private final int totalImages;
    private int currentImage;
    private int loop;
    private int loopCount;
    private final int animationDelay;
    private boolean isRunning;

    private Timer animationTimer;

    public AnimatedImage(ImageIcon[] images) {
        this(images, 33);
    }

    public AnimatedImage(ImageIcon[] images, int animationDelay) {
        this.images = images;
        this.totalImages = images.length;
        this.currentImage = 0;
        this.loop = 0;
        this.loopCount = 0;
        this.animationDelay = animationDelay;
        isRunning = false;
    }

    public AnimatedImage(ImageIcon[] images, int animationDelay, int loop) {
        this(images, animationDelay);
        this.loop = loop;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (images[currentImage].getImageLoadStatus() == MediaTracker.COMPLETE) {
            images[currentImage].paintIcon(this, g, 0, 0);
            currentImage = (currentImage + 1) % totalImages;
        }
        if (currentImage == 0 && loop > 0) {
            loopCount++;
            if (loopCount == loop)
                stopAnimation();
        }
    }

    public void startAnimation() {
        currentImage = 0;
        if (animationTimer == null) {
            animationTimer = new Timer(animationDelay, e -> repaint());
            animationTimer.start();
        } else if (!animationTimer.isRunning())
            animationTimer.restart();
        isRunning = true;
    }

    public void stopAnimation() {
        animationTimer.stop();
        isRunning = false;
    }

    public void waitFinish() {
        if (loop == 0)
            throw new IllegalThreadStateException();
        while (isRunning()) {
            try {
                System.out.println("w");
                Thread.sleep(animationDelay);
            } catch (InterruptedException ignored) {}
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setImages(ImageIcon[] images) {
        this.images = images;
    }
}
