package org.zyb.clearedittext;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/1/4.
 */

public class ClearEditText extends EditText implements
        View.OnFocusChangeListener ,TextWatcher{


    private Drawable mClearDrawable;
    private boolean hasFocus;

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        //如果在xml文件里设置了右边的图片，那肯定会get到，这样if语句就为false
        mClearDrawable = getCompoundDrawables()[2];
        if(mClearDrawable == null){
            mClearDrawable = getResources().getDrawable(R.drawable.icon_clear);

        }

        //设置clear图标的边界
        mClearDrawable.setBounds(0,0,40,40);
        //默认不显示，只有在有输入内容的时候才显示
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
//        addTextChangedListener(this);
    }



    //设置clear图标的显示与否
    public void setClearIconVisible(boolean flag){
        Drawable rightDrawable;

        if(flag){
            rightDrawable = mClearDrawable;
        }else {
            rightDrawable = null;
        }
        setCompoundDrawables(null,null,rightDrawable,null);
    }


    /**
     * 获得焦点的那一刻执行一次这个方法,且当前内容不为空的时候显示clear图标
     * 但是这样做还不能实现输入内容的时候显示clear图标，因为这个方法只会在获得焦点的时候执行
     * 后续焦点一直在editText上，而这个方法不会再执行了，所以必须要有TextWatcher来监听当前输入的内容
     * 进而在有焦点的时候根据是否有输入内容来选择显示或隐藏clear图标
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if(hasFocus){
            setClearIconVisible(getText().length() > 0);
        }else {
            setClearIconVisible(false);
        }
    }

    /**
     * touch到图标所在范围的时候对EditText置空
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(getCompoundDrawables()[2] != null){
                boolean touchClear = (event.getX()>(getWidth()-getTotalPaddingRight()))
                        && (event.getX()<(getWidth()-getPaddingRight()));
                if(touchClear){
                    setText("");//输入框置空
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if(hasFocus){
            setClearIconVisible(s.length() > 0);
        }
    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
}
