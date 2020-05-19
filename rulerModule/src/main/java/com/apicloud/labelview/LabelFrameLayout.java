package com.apicloud.labelview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class LabelFrameLayout extends FrameLayout {

    public boolean isTouched;

    public LabelFrameLayout(Context context) {
        super(context);
    }

    public LabelFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LabelFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float moveX, moveY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        moveX = ev.getX();
        moveY = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isTouched = true;
        } else if (ev.getAction() == MotionEvent.ACTION_CANCEL ||
                ev.getAction() == MotionEvent.ACTION_UP) {
            isTouched = false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
