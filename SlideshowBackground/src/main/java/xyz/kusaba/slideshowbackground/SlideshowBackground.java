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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SlideshowBackground extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private boolean flagSetData = false;
    private boolean flagRequestReset = false;
    private SurfaceHolder surfaceHolder;
    private Thread thread = null;
    private Resources res = null;
    private List<Integer> resIdList = new ArrayList<>();
    private class ImageOnScreen {
        Bitmap bitmap = null;
        Rect rect = new Rect();
    }
    private List<ImageOnScreen> imageOnScreenList = new ArrayList<>();

    public SlideshowBackground(Context context) {
        super(context);
    }
    public SlideshowBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SlideshowBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) { }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }

    public void setData(Resources res, List<Integer> resIdList) {
        this.res = res;
        this.resIdList = resIdList;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        flagSetData = true;
        thread = new Thread(this);
    }

    public boolean start() {
        if (flagSetData) {
            thread.start();
            return true;
        }
        else {
            return false;
        }
    }

    public boolean reset() {
        if (flagSetData) {
            setFlagRequestReset(true);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                slideImagesToLeft(imageOnScreenList, 5);
                addImagesToScreen(imageOnScreenList);
                deleteImagesOnScreen(imageOnScreenList);
                drawScreen(imageOnScreenList);
                Thread.sleep(16);
                resetImageOnScreenList(imageOnScreenList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void slideImagesToLeft(List<ImageOnScreen> imageOnScreenList, int step) {
        if (imageOnScreenList.size() == 0) {
            // if there are no images on screen, nothing to do.
            return;
        }
        for (int i = 0; i < imageOnScreenList.size(); i++) {
            ImageOnScreen imageOnScreen = imageOnScreenList.get(i);
            imageOnScreen.rect.left -= step;
            imageOnScreen.rect.right -= step;
            imageOnScreenList.set(i, imageOnScreen);
        }
    }

    private void addImagesToScreen(List<ImageOnScreen> imageOnScreenList) {
        int edgeXPosOfImages;
        if (imageOnScreenList.size() == 0) {
            edgeXPosOfImages = 0;
        }
        else {
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            edgeXPosOfImages = lastImageOnScreen.rect.right;
        }

        while (edgeXPosOfImages < this.getWidth()) {
            ImageOnScreen imageOnScreen = new ImageOnScreen();
            Random random = new Random();
            imageOnScreen.bitmap = BitmapFactory.decodeResource(res, resIdList.get(random.nextInt(resIdList.size())));
            imageOnScreen.rect.left = edgeXPosOfImages;
            imageOnScreen.rect.top = 0;
            imageOnScreen.rect.bottom = this.getHeight();
            imageOnScreen.rect.right = imageOnScreen.rect.left + (int)(imageOnScreen.bitmap.getWidth() * ((float)imageOnScreen.rect.bottom) / imageOnScreen.bitmap.getHeight());
            imageOnScreenList.add(imageOnScreen);
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            edgeXPosOfImages = lastImageOnScreen.rect.right;
        }
    }

    private void deleteImagesOnScreen(List<ImageOnScreen> imageOnScreenList) {
        Iterator<ImageOnScreen> imageOnScreenIterator = imageOnScreenList.iterator();
        while(imageOnScreenIterator.hasNext()){
            ImageOnScreen imageOnScreen = imageOnScreenIterator.next();
            if (imageOnScreen.rect.right < 0) {
                imageOnScreenIterator.remove();
            }
        }
    }

    private void drawScreen(List<ImageOnScreen> imageOnScreenList) {
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

    private void resetImageOnScreenList(List<ImageOnScreen> imageOnScreenList) {
        if (getFlagRequestReset()) {
            imageOnScreenList.clear();
            setFlagRequestReset(false);
        }
    }

    private void setFlagRequestReset(boolean rhs) {
        synchronized(this) {
            flagRequestReset = rhs;
        }
    }

    private boolean getFlagRequestReset() {
        synchronized(this) {
            return flagRequestReset;
        }
    }
}