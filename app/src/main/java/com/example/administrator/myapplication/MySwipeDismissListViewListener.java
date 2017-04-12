package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class MySwipeDismissListViewListener implements View.OnTouchListener {

    public interface ViewRemoveListener{
        void onViewRemoved(int position);
    }

    private ListView listView;

    private ViewRemoveListener callback;

    private float initialX = 0;
    private float initialY = 0;

    private VelocityTracker velocityTracker = VelocityTracker.obtain();

    private final int ANIMATION_DURATION = 200;

    private View viewBeingDragged;
    private int positionBeingDragged;

    public MySwipeDismissListViewListener(ListView listView, ViewRemoveListener callback) {
        this.listView = listView;
        this.callback = callback;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        velocityTracker.addMovement(event);
        System.out.println("event.getAction(): " + event.getAction());
        System.out.println("event.getRawX(): " + event.getRawX() + " event.getRawY(): " + event.getRawY());
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            initialX = event.getX();
            initialY = event.getY();
            for(int i = 0; i < listView.getChildCount(); i++){
                View view = listView.getChildAt(i);
                if(event.getY() > view.getTop() && event.getY() < view.getBottom()){
                    viewBeingDragged = view;
                    positionBeingDragged = i;
                    break;
                }
            }
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(viewBeingDragged != null){
                float deltaX = event.getX() - initialX;
                viewBeingDragged.setTranslationX(deltaX);
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL){
            if(viewBeingDragged != null){
                if(shouldRemove()){
                    removeView();
                }else{
                    resetView();
                }
            }
        }
        return false;
    }

    private boolean shouldRemove() {
        velocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
        // 如果按照目前速度，1秒内view将从左侧滑出，则要删除
        float predictedTranslationX = velocityTracker.getXVelocity() + viewBeingDragged.getTranslationX();
        System.out.println("predictedTranslationX: " + predictedTranslationX);
        System.out.println("view.getLeft() + view.getMeasuredWidth(): " + viewBeingDragged.getLeft() + viewBeingDragged.getMeasuredWidth());
        return - predictedTranslationX > viewBeingDragged.getLeft() + viewBeingDragged.getMeasuredWidth();
    }

    private void removeView() {
        final float startTranslationX = viewBeingDragged.getTranslationX();
        final float targetTranslationX = - viewBeingDragged.getMeasuredWidth() - viewBeingDragged.getLeft();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                viewBeingDragged.setTranslationX(startTranslationX + (targetTranslationX - startTranslationX) * animatorValue);
                viewBeingDragged.setAlpha((1 - animatorValue) * (1 - animatorValue));
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

    private void resetView() {
        final float startTranslationX = viewBeingDragged.getTranslationX();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                viewBeingDragged.setTranslationX(startTranslationX * animatorValue);
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void shrinkView() {

        final float initialHeight = viewBeingDragged.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                viewBeingDragged.getLayoutParams().height = (int) (initialHeight * animatorValue);
                viewBeingDragged.requestLayout();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(ANIMATION_DURATION);
        animator.setTarget(viewBeingDragged);
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onViewRemoved(positionBeingDragged);
            }

        });
        animator.start();
    }

    private abstract class AnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
