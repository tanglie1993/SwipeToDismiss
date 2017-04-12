package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class MySwipeDismissListViewListener extends BaseSwipeDismissListener {



    public interface ViewRemoveListener{
        void onViewRemoved(int position);
    }

    private ListView listView;

    private ViewRemoveListener callback;

    private float initialX = 0;
    private float initialY = 0;

    private int positionBeingDragged = -1;

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
                    positionBeingDragged = i;
                    break;
                }
            }
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(positionBeingDragged >= 0){
                float deltaX = event.getX() - initialX;
                listView.getChildAt(positionBeingDragged).setTranslationX(deltaX);
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL){
            if(positionBeingDragged >= 0){
                if(shouldRemove(listView.getChildAt(positionBeingDragged))){
                    removeView(listView.getChildAt(positionBeingDragged), positionBeingDragged);
//                    onViewRemoved();
                }else{
                    resetView(listView.getChildAt(positionBeingDragged));
                }
            }
        }
        return false;
    }

    protected void removeView(final View view, final int position) {
        final float startTranslationX = view.getTranslationX();
        final float targetTranslationX = - view.getMeasuredWidth() - view.getLeft();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                view.setTranslationX(startTranslationX + (targetTranslationX - startTranslationX) * animatorValue);
            }
        });
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                shrinkView(view, position);
            }
        });
        animator.start();
    }

    protected void shrinkView(final View view, final int position) {
        final float initialHeight = view.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0f);
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
                onViewRemoved(position, initialHeight);
            }

        });
        animator.start();
    }

    protected void onViewRemoved(int position, float initialHeight) {
        restoreViewState(listView.getChildAt(position), (int) initialHeight);
        callback.onViewRemoved(position);
    }

    private void restoreViewState(View view, int initialHeight) {
        view.setTranslationX(0);
        view.getLayoutParams().height = initialHeight;
        view.requestLayout();
    }
}
