package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

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

    private boolean isDeleting = false;

    private AnimatorElement exitingView;

    private class AnimatorElement{
        static final int SWIPE = 1;
        static final int SHRINK = 2;

        AnimatorElement(Animator animator, int itemInitialHeight, int targetTranslationX, int position, View view) {
            this.animator = animator;
            this.itemInitialHeight = itemInitialHeight;
            this.targetTranslationX = targetTranslationX;
            this.initialPosition = position;
            this.view = view;
        }

        Animator animator;
        final View view;
        int stage = SWIPE;
        final int itemInitialHeight;
        final int targetTranslationX;
        final int initialPosition;
    }

    private int firstVisibleItem = 0;
    private int lastVisibleItem = 0;


    public MySwipeDismissListViewListener(final ListView listView, ViewRemoveListener callback) {
        this.listView = listView;
        this.callback = callback;
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int newFirstVisibleItem, int visibleItemCount, int totalItemCount) {
                int newLastVisibleItem = newFirstVisibleItem + visibleItemCount - 1;
                if(newLastVisibleItem >= totalItemCount){
                    newLastVisibleItem = totalItemCount - 1;
                }
                if(exitingView == null){
                    return;
                }

                if(newFirstVisibleItem != MySwipeDismissListViewListener.this.firstVisibleItem){
                    if(newFirstVisibleItem == exitingView.initialPosition){
                        tryResumeAnimation();
                    }else if(newFirstVisibleItem > exitingView.initialPosition){
                        tryPauseAnimation();
                    }

                }
                if(MySwipeDismissListViewListener.this.lastVisibleItem != newLastVisibleItem){
                    if(newLastVisibleItem == exitingView.initialPosition){
                        tryResumeAnimation();
                    }else if(newLastVisibleItem < exitingView.initialPosition){
                        tryPauseAnimation();
                    }
                }
                MySwipeDismissListViewListener.this.firstVisibleItem = newFirstVisibleItem;
                MySwipeDismissListViewListener.this.lastVisibleItem = newLastVisibleItem;
            }
        });
    }

    private void tryPauseAnimation() {
        if(exitingView == null){
            return;
        }
        if(exitingView.animator != null && exitingView.animator.isRunning()){
            exitingView.animator.pause();
            exitingView.view.setTranslationX(0);
            exitingView.view.getLayoutParams().height = exitingView.itemInitialHeight;
            exitingView.view.requestLayout();
        }
    }

    private void tryResumeAnimation() {
        if(exitingView != null){
            if(exitingView.animator.isPaused()){
                if(exitingView.stage == AnimatorElement.SHRINK){
                    exitingView.view.setTranslationX(exitingView.targetTranslationX);
                }
                exitingView.animator.resume();
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        velocityTracker.addMovement(event);
        if(isDeleting){
            return false;
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            initialX = event.getX();
            initialY = event.getY();
            for(int i = 0; i < listView.getChildCount(); i++){
                View view = listView.getChildAt(i);
                if(event.getY() > view.getTop() && event.getY() < view.getBottom()){
                    System.out.println("positionBeingDragged 0: " + (i + listView.getFirstVisiblePosition()));
                    positionBeingDragged = i + listView.getFirstVisiblePosition();
                    break;
                }
            }
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(positionBeingDragged >= 0){
                float deltaX = event.getX() - initialX;
                listView.getChildAt(positionBeingDragged - listView.getFirstVisiblePosition()).setTranslationX(deltaX);
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL){
            if(positionBeingDragged >= 0){
                if(shouldRemove(listView.getChildAt(positionBeingDragged - listView.getFirstVisiblePosition()))){
                    isDeleting = true;
                    removeView(positionBeingDragged);
                }else{
                    resetView(listView.getChildAt(positionBeingDragged - listView.getFirstVisiblePosition()));
                }
            }
            positionBeingDragged = -1;
        }
        return false;
    }

    protected void removeView(final int position) {
        final View view = listView.getChildAt(positionBeingDragged - listView.getFirstVisiblePosition());
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
                if(exitingView != null && !exitingView.animator.isPaused()){
                    if(position == listView.getCount() - 1
                            && firstVisibleItem == 0
                            && lastVisibleItem == listView.getCount()){
                        onViewRemoved(position);
                        exitingView = null;
                        lastVisibleItem--;
                    }else{
                        shrinkView(view, position);
                    }
                }
            }
        });
        exitingView = new AnimatorElement(animator, view.getMeasuredHeight(), (int) targetTranslationX, position, view);
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
                onViewRemoved(position);
                exitingView = null;
            }

        });
        exitingView.animator = animator;
        exitingView.stage = AnimatorElement.SHRINK;
        animator.start();
    }

    protected void onViewRemoved(int position) {
        restoreViewState();
        isDeleting = false;
        callback.onViewRemoved(position);
    }

    private void restoreViewState() {
        exitingView.view.setTranslationX(0);
        exitingView.view.getLayoutParams().height = exitingView.itemInitialHeight;
        exitingView.view.requestLayout();
    }
}
