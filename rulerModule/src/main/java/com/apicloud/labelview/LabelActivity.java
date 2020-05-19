package com.apicloud.labelview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apicloud.labelview.UZRulerModule.labelJSONObject;

public class LabelActivity extends Activity {
    private LabelFrameLayout mFrameLayout;
    float actionDownX = 0, actionDownY = 0;
    private boolean isAdd;
    boolean isVisiableForLast = false;
    private TextView mTvTitle;
    private LabelView mLabelView;
    private ImageView mImgRuler;
    private ImageView mImgPen;
    private ImageView mImgCheck;
    private ImageView mIvContent;
    private boolean isRulerSelected = true;
    private boolean isPenSelected;
    private boolean isCheckSelected;
    private List<LabelView> mLabelList = new ArrayList<>();
    private Map<LabelView, LabelBean.Bean> mLabelBeanMap = new HashMap<>();
    private View mDecorView;
    private View mLayoutRoot;
    private LabelView mPreLabelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mo_labelview_activity_label);
        initViews();
        getIntentData();
    }

    private void getIntentData() {
        LabelBean labelBean = JsonUtil.getLabelBean(labelJSONObject);
        if (labelBean != null) {
            mTvTitle.setText(labelBean.title);
            mIvContent.setImageBitmap(BitmapUtils.base64ToBitmap((String) labelBean.picString));
            if (labelBean.labelList != null && labelBean.labelList.size() > 0) {
                for (int i = 0; i < labelBean.labelList.size(); i++) {
                    LabelBean.Bean bean = labelBean.labelList.get(i);
                    addLabel(bean.startX, bean.startY, bean.endX, bean.endY, bean.labelText);
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        mLayoutRoot = findViewById(R.id.layout_root);
        mTvTitle = findViewById(R.id.tv_title);
        mImgRuler = findViewById(R.id.iv_ruler);
        mImgPen = findViewById(R.id.iv_pen);
        mImgCheck = findViewById(R.id.iv_check);
        mIvContent = findViewById(R.id.iv_content);
        mFrameLayout = findViewById(R.id.frame_layout);
        mFrameLayout.requestDisallowInterceptTouchEvent(true);
        mFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isRulerSelected) {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        actionDownX = event.getX();
                        actionDownY = event.getY();
                        mLabelView = addLabel(actionDownX, actionDownY, event.getX(), event.getY(), null);
                        if (mPreLabelView != null && mPreLabelView != mLabelView) {
                            mPreLabelView.setBorderDisable();
                        }
                        mPreLabelView = mLabelView;
                        isAdd = true;
                        return true;
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        if (isAdd && mLabelView != null) {
                            moveLabel(mLabelView, event.getX(), event.getY());
                            return true;
                        }
                    } else if (action == MotionEvent.ACTION_UP) {
                        mLabelView.showInput(true);
                        mLabelView = null;
                        isAdd = false;
                        return true;
                    }
                }
                return false;
            }
        });
        //获取activity的根视图
        mDecorView = getWindow().getDecorView();
        mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        mImgRuler.setSelected(isRulerSelected);
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @SuppressLint("NewApi")
        @Override
        public void onGlobalLayout() {
            //获取当前根视图在屏幕上显示的大小
            Rect rect = new Rect();
            mDecorView.getWindowVisibleDisplayFrame(rect);
            //计算出可见屏幕的高度
            int displayHight = rect.bottom - rect.top;
            //获得屏幕整体的高度
            int hight = mDecorView.getHeight();
            //获得键盘高度
            int keyboardHeight = hight - displayHight;
            boolean visible = (double) displayHight / hight < 0.8;
            if (visible != isVisiableForLast) {
                if (visible && (mFrameLayout.moveY > hight / 2f || actionDownY > hight / 2f) && !mFrameLayout.isTouched) {
                    mLayoutRoot.setTranslationY(-keyboardHeight * 3 / 5f);
                } else {
                    if (!visible) {
                        mLayoutRoot.setTranslationY(0);
                        if(mPreLabelView!=null){
                            mPreLabelView.clearEditFocus();
                        }
                    }
                }
            }
            isVisiableForLast = visible;
        }
    };

    private LabelView addLabel(final float startX, final float startY, float endX, float endY, String content) {
        final LabelView labelView = initLabelView(startX, startY, endX, endY);
        labelView.setOnLabelTouchListener(new LabelView.OnLabelTouchListener() {
            @Override
            public void onCloseClicked(LabelView labelView) {
                if (isRulerSelected) {
                    mFrameLayout.removeView(labelView);
                    mLabelList.remove(labelView);
                    mLabelBeanMap.remove(labelView);
                }
            }

            @Override
            public void onLabelViewAction(LabelView labelView) {
                if (isRulerSelected) {
                    float x = mFrameLayout.moveX;
                    float y = mFrameLayout.moveY;
                    moveLabel(labelView, x, y);
                }
            }

            @Override
            public void onLabelEditText(LabelView labelView, String content) {
                LabelBean.Bean bean = mLabelBeanMap.get(labelView);
                if (bean != null) {
                    bean.labelText = content;
                }
            }

            @Override
            public void onLabelFocused(LabelView view) {
                if (mPreLabelView != null && mPreLabelView != view) {
                    mPreLabelView.setBorderDisable();
                }
                mPreLabelView = view;
            }
        });
        if (!TextUtils.isEmpty(content)) {
            labelView.setContent(content);
        }
        labelView.setEditEnable(isRulerSelected);
        mFrameLayout.addView(labelView);
        mLabelList.add(labelView);
        LabelBean.Bean bean = new LabelBean.Bean(startX, startY, endX, endY, "");
        mLabelBeanMap.put(labelView, bean);
        return labelView;
    }

    @SuppressLint("NewApi")
    private LabelView initLabelView(final float startX, final float startY, float endX, float endY) {
        LabelView labelView = new LabelView(this);
        labelView.setX(startX);
        labelView.setY(startY);
        if (startX != endX && startY != endY)
            moveLabel(labelView, endX, endY);
        return labelView;
    }

    @SuppressLint("NewApi")
    private void moveLabel(LabelView labelView, float x, float y) {
        ViewGroup.LayoutParams layoutParams = labelView.mViewLabel.getLayoutParams();
        int width;
        int labelHalfHeight = dp2px(60 / 2);
        labelView.setPivotY(labelHalfHeight);
        labelView.setPivotX(0);
        if (y == labelHalfHeight) {
            width = (int) x;
        } else {
            width = (int) (Math.hypot(Math.abs(x - labelView.getX()), Math.abs(y - labelView.getY())));
        }
        layoutParams.width = width;
        labelView.mViewLabel.setLayoutParams(layoutParams);
        float angle = getAngle(x - labelView.getX(), y - labelView.getY());
        labelView.setRotation(90 - angle);
        labelView.mEdtLabel.setPivotX(0);
        labelView.mEdtLabel.setPivotY(dp2px(22 / 2));
        labelView.mEdtLabel.setRotation(angle - 90);
        labelView.requestLayout();
        LabelBean.Bean bean = mLabelBeanMap.get(labelView);
        if (bean != null) {
            bean.endX = x;
            bean.endY = y;
        }
    }


    private int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private float getAngle(float x, float y) {
        double tan = Math.atan2(x, y);
        double angleA = 180 * tan / Math.PI;
        return (float) angleA;
    }

    public void onRulerClick(View view) {
        if (isRulerSelected) {
            isRulerSelected = false;
            if (mPreLabelView != null) {
                mPreLabelView.setBorderDisable();
            }
        } else {
            isRulerSelected = true;
            isPenSelected = false;
            isCheckSelected = false;
        }
        if (mPreLabelView != null) {
            mPreLabelView.clearEditFocus();
        }
        setBottom();
        setLabelViewEditEnable(isRulerSelected);
    }

    public void onPenClick(View view) {
        if (isPenSelected) {
            isPenSelected = false;
        } else {
            isRulerSelected = false;
            isPenSelected = true;
            isCheckSelected = false;
        }
        if (mPreLabelView != null) {
            mPreLabelView.clearEditFocus();
            mPreLabelView.setBorderDisable();
        }
        setBottom();
        setLabelViewEditEnable(isPenSelected);
    }

    public void onCheckClick(View view) {
        if (mPreLabelView != null) {
            mPreLabelView.clearEditFocus();
            mPreLabelView.setBorderDisable();
        }
        if (isCheckSelected) {
            isCheckSelected = false;
        } else {
            isRulerSelected = false;
            isPenSelected = false;
            isCheckSelected = true;
        }
        setBottom();
        savePic();
    }

    private void savePic() {
        Bitmap bitmap = viewConversionBitmap(mFrameLayout);
        Object s = BitmapUtils.bitmapToBase64(bitmap);
        Collection<LabelBean.Bean> values = mLabelBeanMap.values();
        LabelBean labelBean = new LabelBean();
        labelBean.picString = s;
        labelBean.labelList = new ArrayList<>(values);
        labelJSONObject = JsonUtil.getJSONObject(labelBean);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * view转bitmap
     */
    public Bitmap viewConversionBitmap(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    private void setBottom() {
        mImgRuler.setSelected(isRulerSelected);
        mImgPen.setSelected(isPenSelected);
        mImgCheck.setSelected(isCheckSelected);
    }

    private void setLabelViewEditEnable(boolean isEnable) {
        for (int i = 0; i < mLabelList.size(); i++) {
            mLabelList.get(i).setEditEnable(isEnable);
        }
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mPreLabelView != null) {
            mPreLabelView.clearEditFocus();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mDecorView.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
        super.onDestroy();
    }
}
