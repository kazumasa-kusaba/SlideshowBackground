package xyz.kusaba.slideshowbackground;

import android.content.res.Resources;

public abstract class SlideshowBackgroundState {
    public void append(SlideshowBackground slideshowBackground, Resources resources, int resourceId) {
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.resources = resources;
        resourceInfo.resourceId = resourceId;
        slideshowBackground.addResourceInfoToList(resourceInfo);
    }

    public void setSpeed(SlideshowBackground slideshowBackground, int pixel) {
        // TODO: write the process to transition to the change setting state
        slideshowBackground.setSpeed(pixel);
    }

    public void setRandomPlayback(SlideshowBackground slideshowBackground, boolean isRandom) {
        // TODO: write the process to transition to the change setting state
        slideshowBackground.setIsRandomPlayback(isRandom);
    }

    public void play(SlideshowBackground slideshowBackground) {
        // implement the process in concrete classes
    }

    public void pause(SlideshowBackground slideshowBackground) {
        // implement the process in concrete classes
    }

    public void stop(SlideshowBackground slideshowBackground) {
        // implement the process in concrete classes
    }
}
