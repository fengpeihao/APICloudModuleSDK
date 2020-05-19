package com.apicloud.labelview;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class CustomerEditText extends EditText {
    public CustomerEditText(Context context) {
        super(context);
    }

    public CustomerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setFocusable(false);
            setFocusableInTouchMode(false);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        Editable text = getText();
        if (TextUtils.isEmpty(text)) return;
        if (focused) {
            setInputType(InputType.TYPE_CLASS_NUMBER);
            if (text.toString().endsWith("厘米")) {
                setText(text.subSequence(0, text.length() - 2));
                setSelection(getText().length());
            }
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
        } else {
            setInputType(InputType.TYPE_CLASS_TEXT);
            if (!text.toString().endsWith("厘米")) {
                text.append("厘米");
                setText(text);
            }
        }
    }
}
