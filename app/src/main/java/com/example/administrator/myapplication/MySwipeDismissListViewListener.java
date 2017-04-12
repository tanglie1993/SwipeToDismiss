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

        }

        return false;
    }
}
