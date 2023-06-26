package com.itblee.gui.component;

import javax.swing.*;
import java.awt.*;

public class AnimatedImage extends JLabel {

    private ImageIcon[] images;

    private boolean freezeLastFrame;
    private Timer timer;

    private int currentImage;
    private int loop;
    private int loopCount;
    private int delay;

    public AnimatedImage(ImageIcon[] images) {
        this(images, 33);
    }

    public AnimatedImage(ImageIcon[] images, int delay) {
        this.images = images;
        currentImage = 0;
        loop = 0;
        loopCount = 0;
        this.delay = delay;
        freezeLastFrame = false;
    }

    public AnimatedImage(ImageIcon[] images, int delay, int loop) {
        this(images, delay);
        this.loop = loop;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timer == null && isVisible())
            currentImage = 0;
        if (timer != null && !timer.isRunning()) {
            if (!freezeLastFrame)
                return;
            if (currentImage == 0)
                currentImage = images.length - 1;
        }
        if (images[currentImage].getImageLoadStatus() == MediaTracker.COMPLETE) {
            images[currentImage].paintIcon(this, g, 0, 0);
            System.out.println(images[currentImage]);
        }
    }

    private void paintImage() {
        repaint();
        currentImage = (currentImage + 1) % images.length;
        if (currentImage == 0 && loop > 0) {
            loopCount++;
            if (loopCount == loop)
                stopAnimation();
        }
    }

    public void startAnimation() {
        if (images.length <= 0)
            return;
        currentImage = 0;
        loopCount = 0;
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

    public void setDelay(int delay) {
        if (delay <= 0)
            throw new IllegalStateException("invalid delay");
        this.delay = delay;
    }

    public void freezeLastFrame(boolean b) {
        freezeLastFrame = b;
    }

}
