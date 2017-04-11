package com.example.administrator.myapplication;

import android.view.MotionEvent;
import android.view.View;
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
            System.out.println("setTranslationX: " + (event.getRawX() - initialX
            ));
            view.setTranslationX(event.getRawX() - initialX);
        }
//        view.dispatchGenericMotionEvent(event);

        return false;
    }
}
