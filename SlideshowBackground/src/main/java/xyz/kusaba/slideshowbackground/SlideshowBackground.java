package xyz.kusaba.slideshowbackground;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class SlideshowBackground extends SurfaceView {
    private SlideshowBackgroundThread slideshowBackgroundThread = new SlideshowBackgroundThread();

    public SlideshowBackground(Context context) {
        super(context);
        init();
    }
    public SlideshowBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SlideshowBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void append(Resources resources, int resourceId) {
        slideshowBackgroundThread.requestAppend(resources, resourceId);
    }

    public void setFlowSpeed(int pixel) {
        slideshowBackgroundThread.requestSetFlowSpeed(pixel);
    }

    public void setRandomPlayback(boolean isRandom) {
        slideshowBackgroundThread.requestSetRandomPlayback(isRandom);
    }

    public void play() {
        slideshowBackgroundThread.requestPlay();
    }

    public void pause() {
        slideshowBackgroundThread.requestPause();
    }

    public void stop() {
        slideshowBackgroundThread.requestStop();
    }

    private void init() {
        slideshowBackgroundThread.setContext(this.getContext());
        slideshowBackgroundThread.setSurfaceHolder(getHolder());
    }
}