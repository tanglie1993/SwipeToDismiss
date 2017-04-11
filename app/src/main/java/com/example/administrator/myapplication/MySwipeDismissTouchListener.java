package com.example.administrator.myapplication;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

/**
 * Created by Administrator on 2017/4/11 0011.
 */

public class MySwipeDismissTouchListener implements View.OnTouchListener {

    private View view;

    private float initialX = 0;

    public MySwipeDismissTouchListener(View view) {
        this.view = view;
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
            exitFromLeft();
        }
//        view.dispatchGenericMotionEvent(event);

        return false;
    }

    private void exitFromLeft() {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0,1);
        final float startTranslationX = view.getTranslationX();
        final float targetTranslationX = - view.getMeasuredWidth() - view.getLeft();
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                view.setTranslationX(animatorValue * (targetTranslationX - startTranslationX) + startTranslationX);
            }
        });
        //4.为ValueAnimator设置LinearInterpolator
        mAnimator.setInterpolator(new LinearInterpolator());
        //5.设置动画的持续时间
        mAnimator.setDuration(1000);
        //6.为ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(view);
        mAnimator.start();
    }
}
