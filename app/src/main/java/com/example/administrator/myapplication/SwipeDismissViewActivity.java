package com.example.administrator.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SwipeDismissViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_dismiss_view);

        String[] items = new String[20];
        for (int i = 0; i < items.length; i++) {
            items[i] = "Item " + (i + 1);
        }

        // Set up normal ViewGroup example
        final ViewGroup dismissableContainer = (ViewGroup) findViewById(R.id.dismissable_container);
        for (int i = 0; i < items.length; i++) {
            final Button dismissableButton = new Button(this);
            dismissableButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dismissableButton.setText("Button " + (i + 1));
            dismissableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(SwipeDismissViewActivity.this,
                            "Clicked " + ((Button) view).getText(),
                            Toast.LENGTH_SHORT).show();
                }
            });

            dismissableButton.setOnTouchListener(new MySwipeDismissTouchListener(dismissableButton
                    , new MySwipeDismissTouchListener.ViewRemoveListener() {
                @Override
                public void onViewRemoved() {
                    dismissableContainer.removeView(dismissableButton);
                }
            }));

            // Create a generic swipe-to-dismiss touch listener.
//            dismissableButton.setOnTouchListener(new SwipeDismissTouchListener(
//                    dismissableButton,
//                    null,
//                    new SwipeDismissTouchListener.DismissCallbacks() {
//                        @Override
//                        public boolean canDismiss(Object token) {
//                            return true;
//                        }
//
//                        @Override
//                        public void onDismiss(View view, Object token) {
//                            dismissableContainer.removeView(dismissableButton);
//                        }
//                    }));




            dismissableContainer.addView(dismissableButton);
        }
    }
}
