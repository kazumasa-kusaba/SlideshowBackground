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
    private boolean isResetRequired = false;
    private int imageOnScreenListIndex = 0;
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
        long startTimeMillis = 0;

        while (true) {
            startTimeMillis = System.currentTimeMillis();
            synchronized (this) {
                if (isResetRequired) {
                    clearScreen();
                    isResetRequired = false;
                }
                if (isFlowing) {
                    moveImagesOnScreen();
                }
            }
            adjustFrame(System.currentTimeMillis() - startTimeMillis);
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
                            isResetRequired = true;
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void clearScreen() {
        imageOnScreenList.clear();
        updateScreen();
    }

    private void moveImagesOnScreen() {
        slideImages();
        addImagesToScreen();
        deleteImagesOnScreen();
        updateScreen();
    }

    private void slideImages() {
        Iterator<ImageOnScreen> imageOnScreenIterator = imageOnScreenList.iterator();
        while (imageOnScreenIterator.hasNext()) {
            ImageOnScreen imageOnScreen = imageOnScreenIterator.next();
            imageOnScreen.rect.left += speed;
            imageOnScreen.rect.right += speed;
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

    private void addImagesToScreenWhileRightFlow() {
        int leftOfLeftmostImage;
        if (imageOnScreenList.size() == 0) {
            leftOfLeftmostImage = context.getResources().getDisplayMetrics().widthPixels;
        }
        else {
            leftOfLeftmostImage = getLeftOfLeftmostImage();
        }

        while (0 < leftOfLeftmostImage) {
            if (isRandomPlayback) {
                Random random = new Random();
                int randomVal = random.nextInt(resourceInfoList.size());
                imageOnScreenListIndex = randomVal;
            } else {
                imageOnScreenListIndex++;
                if (resourceInfoList.size() <= imageOnScreenListIndex) {
                    imageOnScreenListIndex = 0;
                }
            }
            ImageOnScreen imageOnScreen = new ImageOnScreen();
            imageOnScreen.bitmap = BitmapFactory.decodeResource(resourceInfoList.get(imageOnScreenListIndex).resources, resourceInfoList.get(imageOnScreenListIndex).resourceId);
            imageOnScreen.rect.top = 0;
            imageOnScreen.rect.bottom = context.getResources().getDisplayMetrics().heightPixels;
            imageOnScreen.rect.right = leftOfLeftmostImage;
            imageOnScreen.rect.left = imageOnScreen.rect.right - (int)(imageOnScreen.bitmap.getWidth() * ((float)imageOnScreen.rect.bottom) / imageOnScreen.bitmap.getHeight());
            imageOnScreenList.add(imageOnScreen);
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            leftOfLeftmostImage = lastImageOnScreen.rect.left;
        }
    }

    private int getLeftOfLeftmostImage() {
        int leftOfLeftmostImage = context.getResources().getDisplayMetrics().widthPixels;
        Iterator<ImageOnScreen> imageOnScreenIterator = imageOnScreenList.iterator();

        while (imageOnScreenIterator.hasNext()){
            ImageOnScreen imageOnScreen = imageOnScreenIterator.next();
            if (imageOnScreen.rect.left < leftOfLeftmostImage) {
                leftOfLeftmostImage = imageOnScreen.rect.left;
            }
        }

        return leftOfLeftmostImage;
    }

    private void addImagesToScreenWhileLeftFlow() {
        int rightOfRightmostImage;
        if (imageOnScreenList.size() == 0) {
            rightOfRightmostImage = 0;
        }
        else {
            rightOfRightmostImage = getRightOfRightmostImage();
        }

        while (rightOfRightmostImage < context.getResources().getDisplayMetrics().widthPixels) {
            if (isRandomPlayback) {
                Random random = new Random();
                int randomVal = random.nextInt(resourceInfoList.size());
                imageOnScreenListIndex = randomVal;
            } else {
                imageOnScreenListIndex++;
                if (resourceInfoList.size() <= imageOnScreenListIndex) {
                    imageOnScreenListIndex = 0;
                }
            }
            ImageOnScreen imageOnScreen = new ImageOnScreen();
            imageOnScreen.bitmap = BitmapFactory.decodeResource(resourceInfoList.get(imageOnScreenListIndex).resources, resourceInfoList.get(imageOnScreenListIndex).resourceId);
            imageOnScreen.rect.left = rightOfRightmostImage;
            imageOnScreen.rect.top = 0;
            imageOnScreen.rect.bottom = context.getResources().getDisplayMetrics().heightPixels;
            imageOnScreen.rect.right = imageOnScreen.rect.left + (int)(imageOnScreen.bitmap.getWidth() * ((float)imageOnScreen.rect.bottom) / imageOnScreen.bitmap.getHeight());
            imageOnScreenList.add(imageOnScreen);
            ImageOnScreen lastImageOnScreen = imageOnScreenList.get(imageOnScreenList.size() - 1);
            rightOfRightmostImage = lastImageOnScreen.rect.right;
        }
    }

    private int getRightOfRightmostImage() {
        int rightOfRighttmostImage = 0;
        Iterator<ImageOnScreen> imageOnScreenIterator = imageOnScreenList.iterator();

        while (imageOnScreenIterator.hasNext()){
            ImageOnScreen imageOnScreen = imageOnScreenIterator.next();
            if (rightOfRighttmostImage < imageOnScreen.rect.right) {
                rightOfRighttmostImage = imageOnScreen.rect.right;
            }
        }

        return rightOfRighttmostImage;
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

    void adjustFrame(long timePerLoop) {
        //long targetWaitTime = 16;  // 60fps
        long targetWaitTime = 33;  // 30fps
        long waitTime = targetWaitTime - timePerLoop;
        if (waitTime <= 0) {
            return;
        }
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
