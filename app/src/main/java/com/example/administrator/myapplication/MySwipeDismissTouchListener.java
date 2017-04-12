package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/4/11 0011.
 */

public class MySwipeDismissTouchListener extends BaseSwipeDismissListener {

    public interface ViewRemoveListener{
        void onViewRemoved();
    }

    private View view;

    private ViewRemoveListener callback;

    private float initialX = 0;

    public MySwipeDismissTouchListener(final View view, ViewRemoveListener callback) {
        this.view = view;
        this.callback = callback;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        velocityTracker.addMovement(event);
        System.out.println("event.getAction(): " + event.getAction());
        System.out.println("event.getRawX(): " + event.getRawX() + " event.getRawY(): " + event.getRawY());
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            initialX = event.getRawX();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            view.getParent().requestDisallowInterceptTouchEvent(true);
            view.setTranslationX(event.getRawX() - initialX);
        }else if(event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL){
            if(shouldRemove(view)){
                removeView(view);
            }else{
                resetView(view);
            }
        }

        return false;
    }

    @Override
    protected void onViewRemoved() {
        callback.onViewRemoved();
    }

}
