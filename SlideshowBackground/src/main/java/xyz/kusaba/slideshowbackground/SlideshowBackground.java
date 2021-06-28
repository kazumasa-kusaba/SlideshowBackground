package xyz.kusaba.slideshowbackground;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SlideshowBackground extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SlideshowBackgroundState state = null;
    private SurfaceHolder surfaceHolder = getHolder();
    private Thread thread = new Thread(this);
    private List<ResourceInfo> resourceInfoList = new ArrayList<ResourceInfo>();
    private int speed = 5;
    private boolean isRandomPlayback = false;
    private boolean isFlowing = false;
    private List<ImageOnScreen> imageOnScreenList = new ArrayList<ImageOnScreen>();

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

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) { }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }

    public void append(Resources resources, int resourceId) {
        state.append(this, resources, resourceId);
    }

    public void setFlowSpeed(int pixel) {
        state.setSpeed(this, pixel);
    }

    public void setRandomPlayback(boolean isRandom) {
        state.setRandomPlayback(this, isRandom);
    }

    public void play() {
        state.play(this);
    }

    public void pause() {
        state.pause(this);
    }

    public void stop() {
        state.stop(this);
    }

    public void changeState(SlideshowBackgroundState state) {
        this.state = state;
    }

    public void addResourceInfoToList(ResourceInfo resourceInfo) {
        resourceInfoList.add(resourceInfo);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setIsRandomPlayback(boolean isRandomPlayback) {
        this.isRandomPlayback = isRandomPlayback;
    }

    @Override
    public void run() {
        while (true) {
            if (!isFlowing) {
                continue;
            }

            try {
                slideImages();
                addImagesToScreen();
                deleteImagesOnScreen();
                updateScreen();
                Thread.sleep(16); // 60fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        state = new SlideshowBackgroundStateInit();
        surfaceHolder.addCallback(this);
        state = new SlideshowBackgroundStateStop();
        thread.start();

        // test
        state = new SlideshowBackgroundStatePlay();
        isFlowing = true;
    }

    private void slideImages() {
        if (imageOnScreenList.size() == 0) {
            // if images are not registered, nothing to do.
            return;
        }
        // MEMO: can this code use iterator?
        for (int i = 0; i < imageOnScreenList.size(); i++) {
            ImageOnScreen imageOnScreen = imageOnScreenList.get(i);
            imageOnScreen.rect.left += speed;
            imageOnScreen.rect.right += speed;
            imageOnScreenList.set(i, imageOnScreen);
        }
    }

    private void addImagesToScreen() {
        // right flow
        if (speed > 0) {
            addImagesToScreenWhileRightFlow();
        }
        // left flow
        else if (speed < 0) {
            addImagesToScreenWhileLeftFlow();
        }
    }

    // TODO: refactor the following methods
    //       * addImagesToScreenWhileRightFlow
    //       * addImagesToScreenWhileLeftFlow

    // TODO: fix the following methods
    //       if the flow direction is changed, an error occurs
    //       the methods can not add images properly

    private void addImagesToScreenWhileRightFlow() {
        int edgeXPosOfImages;
        if (imageOnScreenList.size() == 0) {
            edgeXPosOfImages = this.getWidth();
        }
        else {
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            edgeXPosOfImages = lastImageOnScreen.rect.left;
        }

        while (0 < edgeXPosOfImages) {
            Random random = new Random();
            int randomVal = random.nextInt(resourceInfoList.size());
            ImageOnScreen imageOnScreen = new ImageOnScreen();
            imageOnScreen.bitmap = BitmapFactory.decodeResource(resourceInfoList.get(randomVal).resources, resourceInfoList.get(randomVal).resourceId);
            imageOnScreen.rect.top = 0;
            imageOnScreen.rect.bottom = this.getHeight();
            imageOnScreen.rect.left = imageOnScreen.rect.right - (int)(imageOnScreen.bitmap.getWidth() * ((float)imageOnScreen.rect.bottom) / imageOnScreen.bitmap.getHeight());
            imageOnScreen.rect.right = edgeXPosOfImages;
            imageOnScreenList.add(imageOnScreen);
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            edgeXPosOfImages = lastImageOnScreen.rect.left;
        }
    }

    private void addImagesToScreenWhileLeftFlow() {
        int edgeXPosOfImages;
        if (imageOnScreenList.size() == 0) {
            edgeXPosOfImages = 0;
        }
        else {
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            edgeXPosOfImages = lastImageOnScreen.rect.right;
        }

        while (edgeXPosOfImages < this.getWidth()) {
            Random random = new Random();
            int randomVal = random.nextInt(resourceInfoList.size());
            ImageOnScreen imageOnScreen = new ImageOnScreen();
            imageOnScreen.bitmap = BitmapFactory.decodeResource(resourceInfoList.get(randomVal).resources, resourceInfoList.get(randomVal).resourceId);
            imageOnScreen.rect.left = edgeXPosOfImages;
            imageOnScreen.rect.top = 0;
            imageOnScreen.rect.bottom = this.getHeight();
            imageOnScreen.rect.right = imageOnScreen.rect.left + (int)(imageOnScreen.bitmap.getWidth() * ((float)imageOnScreen.rect.bottom) / imageOnScreen.bitmap.getHeight());
            imageOnScreenList.add(imageOnScreen);
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            edgeXPosOfImages = lastImageOnScreen.rect.right;
        }
    }

    private void deleteImagesOnScreen() {
        Iterator<ImageOnScreen> imageOnScreenIterator = imageOnScreenList.iterator();
        while (imageOnScreenIterator.hasNext()){
            ImageOnScreen imageOnScreen = imageOnScreenIterator.next();
            // right flow
            if (0 < speed && this.getWidth() < imageOnScreen.rect.left) {
                imageOnScreenIterator.remove();
            }
            // left flow
            else if (speed < 0 && imageOnScreen.rect.right < 0) {
                imageOnScreenIterator.remove();
            }
        }
    }

    private void updateScreen() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        Iterator<ImageOnScreen> imageOnScreenIterator = imageOnScreenList.iterator();
        while(imageOnScreenIterator.hasNext()){
            ImageOnScreen imageOnScreen = imageOnScreenIterator.next();
            canvas.drawBitmap(imageOnScreen.bitmap, null, imageOnScreen.rect, null);
        }

        surfaceHolder.unlockCanvasAndPost(canvas);
    }
}