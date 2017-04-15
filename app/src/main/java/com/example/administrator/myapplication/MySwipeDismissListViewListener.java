package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
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

    private Map<View, Animator> viewAnimationMap = new HashMap<>();

    private int firstVisibleItem = 0;
    private int lastVisibleItem = 0;
    private View firstVisibleView;
    private View lastVisibleView;


    public MySwipeDismissListViewListener(final ListView listView, ViewRemoveListener callback) {
        this.listView = listView;
        this.callback = callback;
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleView == null){
                    firstVisibleView = listView.getChildAt(0);
                }
                int lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
                if(lastVisibleItem >= totalItemCount){
                    lastVisibleItem = totalItemCount - 1;
                }
                if(lastVisibleView == null){
                    lastVisibleView = listView.getChildAt(lastVisibleItem);
                }
                if(firstVisibleItem != MySwipeDismissListViewListener.this.firstVisibleItem){
                    System.out.println("firstVisibleItem: " + firstVisibleItem);
                    System.out.println("MySwipeDismissListViewListener.this.firstVisibleItem: "
                            + MySwipeDismissListViewListener.this.firstVisibleItem);
                }

                if(firstVisibleItem != MySwipeDismissListViewListener.this.firstVisibleItem){
                    cancelViewAnimation(firstVisibleView);
                    firstVisibleView = listView.getChildAt(firstVisibleItem);
                }
                if(MySwipeDismissListViewListener.this.lastVisibleItem != lastVisibleItem){
                    cancelViewAnimation(lastVisibleView);
                    lastVisibleView = listView.getChildAt(lastVisibleItem);
                }
                MySwipeDismissListViewListener.this.firstVisibleItem = firstVisibleItem;
                MySwipeDismissListViewListener.this.lastVisibleItem = lastVisibleItem;
            }
        });

//
//        new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View v) {
//
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View v) {
//                v.setTranslationX(0);
//                if(viewAnimationMap.get(v) != null){
//                    viewAnimationMap.get(v).cancel();
//                }
//            }
//        }
    }

    private void cancelViewAnimation(View view) {
        Animator animator = viewAnimationMap.get(view);
        System.out.println("cancel 1");
        if(animator != null){
            viewAnimationMap.remove(view);
            System.out.println("cancel 2");
            if(animator.isRunning()){
                System.out.println("cancel 3");
                animator.cancel();
            }
        }
        System.out.println("cancelViewAnimation viewAnimationMap.size(): " + viewAnimationMap.size());
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        velocityTracker.addMovement(event);
        if(isDeleting){
            return false;
        }
//        System.out.println("event.getAction(): " + event.getAction());
//        System.out.println("event.getRawX(): " + event.getRawX() + " event.getRawY(): " + event.getRawY());
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
                    isDeleting = true;
                    removeView(positionBeingDragged);
//                    onViewRemoved();
                }else{
                    resetView(listView.getChildAt(positionBeingDragged));
                }
            }
        }
        return false;
    }

    protected void removeView(final int position) {
        final View view = listView.getChildAt(position);
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
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                System.out.println("onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("onAnimationEnd");
                System.out.println("viewAnimationMap.size(): " + viewAnimationMap.size());
                if(viewAnimationMap.containsKey(view)){
                    viewAnimationMap.remove(view);
                    shrinkView(view, position);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        viewAnimationMap.put(view, animator);
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
                viewAnimationMap.remove(view);
                onViewRemoved(position, initialHeight);
            }

        });
        viewAnimationMap.put(view, animator);
        animator.start();
    }

    protected void onViewRemoved(int position, float initialHeight) {
        restoreViewState(listView.getChildAt(position), (int) initialHeight);
        isDeleting = false;
        callback.onViewRemoved(position);
    }

    private void restoreViewState(View view, int initialHeight) {
        view.setTranslationX(0);
        view.getLayoutParams().height = initialHeight;
        view.requestLayout();
    }
}
