package xyz.kusaba.slideshowbackground;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SlideshowBackgroundThread implements Runnable, SurfaceHolder.Callback{
    private static final int MSG_WHAT_APPEND_IMAGE = 0;
    private static final int MSG_WHAT_SET_FLOW_SPEED = 1;
    private static final int MSG_WHAT_SET_RANDOM_PLAYBACK = 2;
    private static final int MSG_WHAT_PLAY = 3;
    private static final int MSG_WHAT_PAUSE = 4;
    private static final int MSG_WHAT_STOP = 5;

    private Context context = null;
    private Thread thread = new Thread(this);
    private SurfaceHolder surfaceHolder = null;
    private Handler handler = null;
    private List<ResourceInfo> resourceInfoList = new ArrayList<ResourceInfo>();
    private int speed = 5;
    private boolean isRandomPlayback = false;
    private boolean isFlowing = false;
    private List<ImageOnScreen> imageOnScreenList = new ArrayList<ImageOnScreen>();

    public SlideshowBackgroundThread() {
        initHandler();
        thread.start();
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.surfaceHolder.addCallback(this);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) { }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) { }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) { }

    public void requestAppend(Resources resources, int resourceId) {
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.resources = resources;
        resourceInfo.resourceId = resourceId;
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_APPEND_IMAGE;
        msg.obj = (Object)resourceInfo;
        handler.sendMessage(msg);
    }

    public void requestSetFlowSpeed(int pixel) {
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_SET_FLOW_SPEED;
        msg.obj = (Object)pixel;
        handler.sendMessage(msg);
    }

    public void requestSetRandomPlayback(boolean isRandom) {
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_SET_RANDOM_PLAYBACK;
        msg.obj = (Object)isRandom;
        handler.sendMessage(msg);
    }

    public void requestPlay() {
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_PLAY;
        handler.sendMessage(msg);
    }

    public void requestPause() {
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_PAUSE;
        handler.sendMessage(msg);
    }

    public void requestStop() {
        Message msg = Message.obtain();
        msg.what = MSG_WHAT_STOP;
        handler.sendMessage(msg);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!isFlowing) {
                    continue;
                }
                threadAction();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_WHAT_APPEND_IMAGE:
                        synchronized (this) {
                            ResourceInfo resourceInfo = (ResourceInfo) msg.obj;
                            resourceInfoList.add(resourceInfo);
                        }
                        break;
                    case MSG_WHAT_SET_FLOW_SPEED:
                        synchronized (this) {
                            int pixel = (int) msg.obj;
                            speed = pixel;
                        }
                        break;
                    case MSG_WHAT_SET_RANDOM_PLAYBACK:
                        synchronized (this) {
                            boolean isRandom = (boolean) msg.obj;
                            isRandomPlayback = isRandom;
                        }
                        break;
                    case MSG_WHAT_PLAY:
                        synchronized (this) {
                            isFlowing = true;
                        }
                        break;
                    case MSG_WHAT_PAUSE:
                        synchronized (this) {
                            isFlowing = false;
                        }
                        break;
                    case MSG_WHAT_STOP:
                        synchronized (this) {
                            isFlowing = false;
                            // TODO: write the process to reset view
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void threadAction() {
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
            edgeXPosOfImages = context.getResources().getDisplayMetrics().widthPixels;
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
            imageOnScreen.rect.bottom = context.getResources().getDisplayMetrics().heightPixels;
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

        while (edgeXPosOfImages < context.getResources().getDisplayMetrics().widthPixels) {
            Random random = new Random();
            int randomVal = random.nextInt(resourceInfoList.size());
            ImageOnScreen imageOnScreen = new ImageOnScreen();
            imageOnScreen.bitmap = BitmapFactory.decodeResource(resourceInfoList.get(randomVal).resources, resourceInfoList.get(randomVal).resourceId);
            imageOnScreen.rect.left = edgeXPosOfImages;
            imageOnScreen.rect.top = 0;
            imageOnScreen.rect.bottom = context.getResources().getDisplayMetrics().heightPixels;
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
            if (0 < speed && context.getResources().getDisplayMetrics().widthPixels < imageOnScreen.rect.left) {
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