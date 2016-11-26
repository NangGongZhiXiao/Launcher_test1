package com.example.ljh.launcher_test1;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by 知晓 on 2016/11/13.
 */
public class MyGridView extends GridView implements AdapterView.OnItemLongClickListener {
    WindowManager.LayoutParams windowParams;
    WindowManager windowManager;
    TranslateAnimation animation;
    private boolean haveAnimation;                    // 判断当前是否有动画
    private boolean isDrag;                           // 判断当前是否为拖动状态
    private float currentTOuchWinX;                   // 当前触摸点在屏幕上的位置x
    private float currentTouchWinY;                   // 当前触摸点在屏幕上的位置y
    private float currentTouchX;                      // 当前触摸点的位置x
    private float currentTouchY;                      // 当前触摸点的位置y
    private float viewFingerDX;                       // 长按触摸点和view位置的差x
    private float viewFingerDY;                       // 长按触摸点和view位置的差y
    private int dragVirtualPosition;                  // 当前拖动虚拟图像的position
    private int dragCurrentPosition;                  // 拖动时当前的position
    private int dragLastPosition;                     // 拖动时的上一个position
    private int deletePosition;                       // 需要删除的position
    private int dragEndPosition;                      // 拖动结束时的position
    private int dragStartPosition;                    // 拖动开始时的position
    private ImageView virtualImageView;               // 虚拟的imageview
    private Trash trash;

    public MyGridView(Context context) {
        this(context, null);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        isDrag = false;
        dragCurrentPosition = -1;
        deletePosition = -1;
        setOnItemLongClickListener(this);
    }

    /**
     *  通过当前屏幕下item的position获取bitmap
     * @param i 需要显示的position
     * @return item的bitmap
     */
    private Bitmap getBitmap(int i) {
        // i - getFirstVisiblePosition()，i 需要显示的position
        ViewGroup viewGroup = (ViewGroup) getChildAt(i - getFirstVisiblePosition());
        viewGroup.destroyDrawingCache();
        viewGroup.setDrawingCacheEnabled(true);
        return Bitmap.createBitmap(viewGroup.getDrawingCache());
    }

    /**
     *  将虚拟的item显示出来
     * @param virtualBitmap 虚拟的item图像
     * @param x 要显示在屏幕上的x值
     * @param y 要显示在屏幕上的y值
     * @return 显示在屏幕上的imageview
     */
    private ImageView showVirtualView(Bitmap virtualBitmap, float x, float y) {
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.START | Gravity.TOP;
        windowParams.x = (int) x;
        windowParams.y = (int) y;
        windowParams.alpha = 0.5f;
        windowParams.width = (int) (virtualBitmap.getWidth() * 1.2);
        windowParams.height = (int) (virtualBitmap.getHeight() * 1.2);
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(virtualBitmap);
        windowManager.addView(imageView, windowParams);
        return imageView;
    }

    /**
     *  开始item移动的动画
     * @param view 移动的item
     * @param startPosition 开始的position
     * @param endPosition 结束的position
     */
    private void startItemMoveAnimation(final View view, final int startPosition, final int endPosition) {
        // 获取view的宽高和列数
        int height = view.getHeight();
        int width = view.getWidth();
        int numColemns = getNumColumns();
        // 计算item所在的行数和列数
        int startRow = startPosition / numColemns;
        int startColemn = startPosition % numColemns;
        int endRow = endPosition / numColemns;
        int endColemn = endPosition % numColemns;
        // 计算x和y方向的偏移量
        int xDelta = (endColemn - startColemn) * (getHorizontalSpacing() + width);
        int yDelta = (endRow - startRow) * (getVerticalSpacing() + height);

        // 开始动画
        animation = new TranslateAnimation(0, xDelta, 0, yDelta);
        animation.setDuration(300L);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                haveAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                haveAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        isDrag = true;
        // 获取触摸点和item的差值,实际大小比原图像大
        viewFingerDX = (float) ((currentTouchX - view.getLeft()) * 1.2);
        viewFingerDY = (float) ((currentTouchY - view.getTop()) * 1.2);
        // 显示垃圾桶
        int[] location = new int[2];
        getLocationOnScreen(location);
        trash = new Trash(getContext(), 0, location[1], location[0], getHeight());
        // 显示虚拟图像
        virtualImageView = showVirtualView(getBitmap(position), currentTOuchWinX - viewFingerDX, currentTouchWinY - viewFingerDY);
        view.setVisibility(INVISIBLE);

        trash.openTrash();


        dragVirtualPosition = position;
        dragCurrentPosition = position;
        dragLastPosition = dragCurrentPosition;

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 获得当前触摸点的位置
        currentTouchX = ev.getX();
        currentTouchY = ev.getY();
        currentTOuchWinX = ev.getRawX();
        currentTouchWinY = ev.getRawY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 拖动过程中更新虚拟imageview的位置
                if(isDrag) {
                    windowParams.x = (int) (ev.getRawX() - viewFingerDX);
                    windowParams.y = (int) (ev.getRawY() - viewFingerDY);
                    windowManager.updateViewLayout(virtualImageView, windowParams);

                    dragVirtualPosition = pointToPosition((int)ev.getX(), (int)ev.getY());
                    Log.i("ljh",pointToPosition((int)ev.getX(), (int)ev.getY()) + "    "+getFirstVisiblePosition()+ " the dragvirtualposition is " + dragVirtualPosition);
                    // 当位置没有发生移动时执行，当position超出边界时为-1
                    if(dragVirtualPosition != dragLastPosition && dragVirtualPosition != -1) {
                       /* // 判断是往前移动还是往后移动
                        if (dragVirtualPosition > dragCurrentPosition) {
                            for (int i = dragCurrentPosition + 1; i <= dragVirtualPosition; i++) {
                                startItemMoveAnimation(getChildAt(i), i, i - 1);
                            }
                        } else {
                            for (int i = dragCurrentPosition - 1; i >= dragVirtualPosition; i--) {
                                startItemMoveAnimation(getChildAt(i), i, i + 1);
                            }
                        }*/
                        if(animation != null) {
                            animation.cancel();
                        }
                        Log.i("ljh", (dragVirtualPosition) + "  the getchildat is "+getChildAt(dragVirtualPosition - getFirstVisiblePosition()) + "  the lastposition is " + dragLastPosition);
                        View view = getChildAt(dragVirtualPosition - getFirstVisiblePosition());
                        if(view != null){
                            startItemMoveAnimation(view, dragVirtualPosition, dragCurrentPosition);
                        }
                        dragLastPosition = dragVirtualPosition;
                    }

                    // 根据触摸点的位置改变trash的图标
                    if(ev.getX() < 0){
                        trash.changeDrawable(true);
                    }
                    else {
                        trash.changeDrawable(false);
                    }

                }


                break;

            case MotionEvent.ACTION_UP:
                if(isDrag) {
                    if(animation != null) {
                        animation.cancel();
                    }
                    Log.i("ljh", getFirstVisiblePosition() + " the dragcurrentposition is " + dragCurrentPosition);
                    getChildAt(dragCurrentPosition - getFirstVisiblePosition()).setVisibility(VISIBLE);

                    dragVirtualPosition = pointToPosition((int)ev.getX(), (int)ev.getY());
                    if(dragVirtualPosition != -1) {
                        ((AppsAdapter) getAdapter()).exchangeItem(dragCurrentPosition, dragVirtualPosition);
                    }


                    // 如果有虚拟的imageview则删除它
                    if(virtualImageView != null) {
                        try {
                            windowManager.removeView(virtualImageView);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(ev.getX() < 0) {
                        isDrag = false;
                        deletePosition = dragCurrentPosition;
                    }
                    trash.closeTrash();
                    isDrag = false;
                    dragCurrentPosition = -1;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       /* if(isDrag) {
            return true;
        }
        else {
            return super.onInterceptTouchEvent(ev);
        }*/
        return true;
    }




    public int getDragCurrentPosition() {
        return dragCurrentPosition;
    }

    public int getDeletePosition() {
        return deletePosition;
    }

    public void setDeletePosition(int deletePosition) {
        this.deletePosition = deletePosition;
    }
}
