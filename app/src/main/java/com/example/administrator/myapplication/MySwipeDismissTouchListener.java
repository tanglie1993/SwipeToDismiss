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

    private VelocityTracker velocityTracker = VelocityTracker.obtain();

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
            if(shouldRemove()){
                removeView();
            }else{
                resetView(view);
            }
        }

        return false;
    }

    private boolean shouldRemove() {
        velocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
        // 如果按照目前速度，1秒内view将从左侧滑出，则要删除
        float predictedTranslationX = velocityTracker.getXVelocity() + view.getTranslationX();
        System.out.println("predictedTranslationX: " + predictedTranslationX);
        System.out.println("view.getLeft() + view.getMeasuredWidth(): " + view.getLeft() + view.getMeasuredWidth());
        return - predictedTranslationX > view.getLeft() + view.getMeasuredWidth();
    }

    private void removeView() {
        final float startTranslationX = view.getTranslationX();
        final float targetTranslationX = - view.getMeasuredWidth() - view.getLeft();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                view.setTranslationX(startTranslationX + (targetTranslationX - startTranslationX) * animatorValue);
                view.setAlpha((1 - animatorValue) * (1 - animatorValue));
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                shrinkView();
            }
        });
        animator.start();
    }



    private void shrinkView() {

        final float initialHeight = view.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                view.getLayoutParams().height = (int) (initialHeight * animatorValue);
                view.requestLayout();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(ANIMATION_DURATION);
        animator.setTarget(view);
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onViewRemoved();
            }

        });
        animator.start();
    }

}
