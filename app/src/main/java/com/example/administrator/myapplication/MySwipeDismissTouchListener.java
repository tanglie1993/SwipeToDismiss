package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/4/11 0011.
 */

public class MySwipeDismissTouchListener implements View.OnTouchListener {

    public interface ViewRemoveListener{
        void onViewRemoved();
    }

    private View view;

    private ViewRemoveListener callback;

    private float initialX = 0;

    public MySwipeDismissTouchListener(View view, ViewRemoveListener callback) {
        this.view = view;
        this.callback = callback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        System.out.println("event.getAction(): " + event.getAction());
        System.out.println("event.getRawX(): " + event.getRawX() + " event.getRawY(): " + event.getRawY());
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            initialX = event.getRawX();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            System.out.println("setTranslationX: " + (event.getRawX() - initialX));
            view.setTranslationX(event.getRawX() - initialX);
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            removeView();
        }
//        view.dispatchGenericMotionEvent(event);

        return false;
    }

    private void removeView() {
        final float startTranslationX = view.getTranslationX();
        final float targetTranslationX = - view.getMeasuredWidth() - view.getLeft();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", startTranslationX, targetTranslationX);
        animator.setDuration(3000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                shrinkView();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void shrinkView() {

        final float initialHeight = view.getMeasuredHeight();
        ValueAnimator mAnimator = ValueAnimator.ofFloat(1, 0);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // TODO Auto-generated method stub
                float animatorValue = (float) animation.getAnimatedValue();
                //3.使用IntEvaluator计算属性值并赋值给ListView的高
                view.getLayoutParams().height = (int) (initialHeight * animatorValue);
                view.requestLayout();
            }
        });
        //4.为ValueAnimator设置LinearInterpolator
        mAnimator.setInterpolator(new LinearInterpolator());
        //5.设置动画的持续时间
        mAnimator.setDuration(3000);
        //6.为ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(view);
        mAnimator.start();



//        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleY", 1f,0f);
//        animator.setDuration(5000);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                callback.onViewRemoved();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        animator.start();
    }
}
