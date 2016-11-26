package com.example.ljh.launcher_test1;

/**
 * Created by 知晓 on 2016/11/16.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 *  垃圾桶类
 */
public class Trash{
    // 垃圾桶的位置，宽高
    private int x;
    private int y;
    private int width;
    private int height;
    private Context context;
    private Bitmap bitmap;
    private ImageView imageView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams winParams;
    private boolean inTrash;

    public Trash(Context context, int x, int y) {
        this(context, x, y, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }
    public Trash(Context context, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        inTrash = false;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // 将垃圾桶显示出来
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void openTrash() {
        imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.back);

/*            imageView.destroyDrawingCache();
            imageView.setDrawingCacheEnabled(true);
            bitmap = imageView.getDrawingCache();*/

        winParams = new WindowManager.LayoutParams();
        winParams.gravity = Gravity.START | Gravity.CENTER;
        winParams.x = x;
        winParams.y = y;
        winParams.width = width;
        winParams.height = height;
        winParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //           winParams.format = PixelFormat.TRANSLUCENT;
        windowManager.addView(imageView, winParams);
    }

    // 将垃圾桶隐藏
    public void closeTrash(){
        if(imageView != null) {
            try {
                windowManager.removeView(imageView);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  改变垃圾桶的背景
     * @param inTrash 图标是否在垃圾桶
     */
    public void changeDrawable(boolean inTrash){
        if(inTrash == this.inTrash){
            return;
        }
        if(inTrash) {
            imageView.setBackgroundResource(R.drawable.delete);
        }
        else{

            imageView.setBackgroundResource(R.drawable.back);
        }
        this.inTrash = inTrash;
    }
}


