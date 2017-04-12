package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public abstract class BaseSwipeDismissListener implements View.OnTouchListener {

    protected VelocityTracker velocityTracker = VelocityTracker.obtain();

    protected final int ANIMATION_DURATION = 200;

    protected void resetView(final View view) {
        final float startTranslationX = view.getTranslationX();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                view.setTranslationX(startTranslationX * animatorValue);
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    protected boolean shouldRemove(final View view) {
        velocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
        // 如果按照目前速度，1秒内view将从左侧滑出，则要删除
        float predictedTranslationX = velocityTracker.getXVelocity() + view.getTranslationX();
        System.out.println("predictedTranslationX: " + predictedTranslationX);
        System.out.println("view.getLeft() + view.getMeasuredWidth(): " + view.getLeft() + view.getMeasuredWidth());
        return - predictedTranslationX > view.getLeft() + view.getMeasuredWidth();
    }

    protected void removeView(final View view) {
        final float startTranslationX = view.getTranslationX();
        final float targetTranslationX = - view.getMeasuredWidth() - view.getLeft();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 0.1f);
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
                shrinkView(view);
            }
        });
        animator.start();
    }



    protected void shrinkView(final View view) {
        final float initialHeight = view.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.1f);
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
                onViewRemoved();
            }

        });
        animator.start();
    }

    protected abstract void onViewRemoved();

    protected abstract class AnimatorListener implements Animator.AnimatorListener {
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
