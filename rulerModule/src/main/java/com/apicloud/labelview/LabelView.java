package com.apicloud.labelview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class LabelView extends RelativeLayout {

    View mViewLabel;
    View mViewBorder;
    View mIvClose;
    View mIvZoom;
    EditText mEdtLabel;
    private OnLabelTouchListener mListener;
    private boolean isEditEnable;

    public LabelView(Context context) {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        requestDisallowInterceptTouchEvent(true);
        setClipChildren(false);
        View view = LayoutInflater.from(context).inflate(R.layout.mo_labelview_layout_label_view, this);
        mViewLabel = view.findViewById(R.id.view_label);
        mViewBorder = view.findViewById(R.id.view_border);
        mIvClose = view.findViewById(R.id.iv_close);
        mIvZoom = view.findViewById(R.id.iv_zoom);
        mEdtLabel = view.findViewById(R.id.edt_label);
        mEdtLabel.setEnabled(isEditEnable);
        mIvZoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearEditFocus();
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    setBorderEnable();
                    mListener.onLabelViewAction(LabelView.this);
                    mListener.onLabelFocused(LabelView.this);
                    return true;
                }
                return false;
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCloseClicked(LabelView.this);
                }
            }
        });
        mViewLabel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setBorderEnable();
                if (isEditEnable) {
                    showInput(isEditEnable);
                }
                mListener.onLabelFocused(LabelView.this);
            }
        });
        mEdtLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mListener.onLabelEditText(LabelView.this, s.toString());
            }
        });
        mEdtLabel.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBorderEnable();
                    mListener.onLabelFocused(LabelView.this);
                }
            }
        });
    }

    private void setBorderEnable(){
        if (isEditEnable && mViewBorder.getVisibility() != View.VISIBLE) {
            mViewBorder.setVisibility(VISIBLE);
            mIvClose.setVisibility(VISIBLE);
            mIvZoom.setVisibility(VISIBLE);
        }
    }

    public void setBorderDisable(){
        mViewBorder.setVisibility(INVISIBLE);
        mIvClose.setVisibility(INVISIBLE);
        mIvZoom.setVisibility(INVISIBLE);
    }

    public void setEditEnable(boolean isEnable) {
        isEditEnable = isEnable;
        if (!isEditEnable) {
            clearEditFocus();
        }
        mEdtLabel.setEnabled(isEditEnable);
    }

    public void showInput(boolean isEnable) {
        isEditEnable = isEnable;
        mEdtLabel.setEnabled(isEditEnable);
        requestEditFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEdtLabel, InputMethodManager.SHOW_IMPLICIT);
    }

    public void setContent(String s) {
        mEdtLabel.setText(s);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int newWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.UNSPECIFIED);
        super.onMeasure(newWidthMeasureSpec, heightMeasureSpec);

    }

    public void clearEditFocus(){
        mEdtLabel.setFocusable(false);
        mEdtLabel.setFocusableInTouchMode(false);
    }

    public void requestEditFocus(){
        mEdtLabel.setFocusable(true);
        mEdtLabel.setFocusableInTouchMode(true);
        mEdtLabel.requestFocus();
    }

    public void setOnLabelTouchListener(OnLabelTouchListener listener) {
        mListener = listener;
    }

    interface OnLabelTouchListener {
        void onCloseClicked(LabelView labelView);

        void onLabelViewAction(LabelView labelView);

        void onLabelEditText(LabelView labelView, String content);

        void onLabelFocused(LabelView labelView);
    }
}
