package com.tg.point.gridpasswordview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsafekb.safekeyboard.NKeyBoardTextField;
import com.appsafekb.safekeyboard.interfaces.KeyBoardListener;
import com.appsafekb.safekeyboard.values.ValueFactory;
import com.tg.coloursteward.R;


/**
 * ●
 *
 * @author Jungly
 * jungly.ik@gmail.com
 * 15/3/5 21:30
 */
public class GridPasswordView extends LinearLayout implements PasswordView {
    private static final int DEFAULT_PASSWORDLENGTH = 6;
    private static final int DEFAULT_TEXTSIZE = 16;
    private static final String DEFAULT_TRANSFORMATION = "●";
    private static final int DEFAULT_LINECOLOR = 0x00000000;
    private static final int DEFAULT_GRIDCOLOR = 0x00000000;

    private ColorStateList mTextColor;
    private int mTextSize = DEFAULT_TEXTSIZE;
    private int mLineWidth;
    private int mLineColor;
    private int mGridColor;
    private int lineVisible;
    private Drawable mLineDrawable;
    private Drawable mOuterLineDrawable;
    private int mPasswordLength;
    private String mPasswordTransformation;

    private String[] mPasswordArr;
    private TextView[] mViewArr;

    private NKeyBoardTextField mInputView;
    private OnPasswordChangedListener mListener;
    private PasswordTransformationMethod mTransformationMethod;

    public GridPasswordView(Context context) {
        super(context);
        initViews(context);
        init(context, null, 0);
    }

    public GridPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public GridPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GridPasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initAttrs(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.gridPasswordView, defStyleAttr, 0);

        mTextColor = ta.getColorStateList(R.styleable.gridPasswordView_gpvTextColor);
        if (mTextColor == null)
            mTextColor = ColorStateList.valueOf(getResources().getColor(android.R.color.primary_text_light));
        int textSize = ta.getDimensionPixelSize(R.styleable.gridPasswordView_gpvTextSize, -1);
        if (textSize != -1) {
            this.mTextSize = Util.px2sp(context, textSize);
        }

        mLineWidth = (int) ta.getDimension(R.styleable.gridPasswordView_gpvLineWidth, Util.dp2px(getContext(), 1));
        mLineColor = ta.getColor(R.styleable.gridPasswordView_gpvLineColor, DEFAULT_LINECOLOR);
        mGridColor = ta.getColor(R.styleable.gridPasswordView_gpvGridColor, DEFAULT_GRIDCOLOR);
        lineVisible = ta.getInt(R.styleable.gridPasswordView_gpvVisible, 0);
        mLineDrawable = ta.getDrawable(R.styleable.gridPasswordView_gpvLineColor);
        if (mLineDrawable == null)
            mLineDrawable = new ColorDrawable(mLineColor);
        mOuterLineDrawable = generateBackgroundDrawable();

        mPasswordLength = ta.getInt(R.styleable.gridPasswordView_gpvPasswordLength, DEFAULT_PASSWORDLENGTH);
        mPasswordTransformation = ta.getString(R.styleable.gridPasswordView_gpvPasswordTransformation);
        if (TextUtils.isEmpty(mPasswordTransformation))
            mPasswordTransformation = DEFAULT_TRANSFORMATION;
        ta.recycle();

        mPasswordArr = new String[mPasswordLength];
        mViewArr = new TextView[mPasswordLength];
    }

    private void initViews(Context context) {
        super.setBackgroundDrawable(mOuterLineDrawable);
        setShowDividers(SHOW_DIVIDER_NONE);
        setOrientation(HORIZONTAL);

        mTransformationMethod = new CustomPasswordTransformationMethod(mPasswordTransformation);
        inflaterViews(context);
    }

    private void inflaterViews(Context context) {
        mInputView = new NKeyBoardTextField(context);
        mInputView.setNkeyboardType(3);
        mInputView.setNKeyboardRandom(ValueFactory.buildAllTrue());
        mInputView.setEditClearIcon(false);
        mInputView.setNKeyboardKeyEncryption(false);
        mInputView.clearNkeyboard();
        mInputView.setNKeyboardMaxInputLength(mPasswordLength);
        mInputView.addTextChangedListener(textWatcher);
        mInputView.setKeyBoardListener(new KeyBoardListener() {
            @Override
            public void onKey(int j) {
                if (j == 5) {
                    for (int i = mPasswordArr.length - 1; i >= 0; i--) {
                        if (mPasswordArr[i] != null) {
                            mPasswordArr[i] = null;
                            mViewArr[i].setText(null);
                            notifyTextChanged();
                            break;
                        } else {
                            mViewArr[i].setText(null);
                        }
                    }
                }
            }

            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }

            @Override
            public void onShow(DialogInterface dialogInterface) {

            }

            @Override
            public void onConfigLoadSucc() {

            }

            @Override
            public boolean onCompleteKeyboardKeepShow() {
                return false;
            }
        });
        LayoutInflater inflater = LayoutInflater.from(context);
        int index = 0;
        while (index < mPasswordLength) {
            View dividerView = inflater.inflate(R.layout.divider, null);
            LayoutParams dividerParams = new LayoutParams(mLineWidth, LayoutParams.MATCH_PARENT, 1);
            dividerView.setBackgroundDrawable(mLineDrawable);
            addView(dividerView, dividerParams);
            View view = inflater.inflate(R.layout.textview, null);
            TextView textView = view.findViewById(R.id.gridView_text);
            View line_view = view.findViewById(R.id.line_view);
            if (lineVisible == 1) {
                line_view.setVisibility(View.VISIBLE);
            } else {
                line_view.setVisibility(View.GONE);
            }
            setCustomAttr(textView);
            addView(view);
            mViewArr[index] = textView;
            index++;
        }

        setOnClickListener(mOnClickListener);
    }

    private void setCustomAttr(TextView view) {
        if (mTextColor != null)
            view.setTextColor(mTextColor);
        view.setTextSize(mTextSize);
        view.setTransformationMethod(mTransformationMethod);
    }

    public void showKeyWorld() {
        if (!mInputView.keyboardIsShow()) {
            mInputView.showNKeyboard();
        }
    }

    public void hideKeyWorld() {
        if (mInputView.keyboardIsShow()) {
            mInputView.hideNKeyboard();
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            forceInputViewGetFocus();
        }
    };

    private GradientDrawable generateBackgroundDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(mGridColor);
        drawable.setStroke(mLineWidth, mLineColor);
        return drawable;
    }

    private void forceInputViewGetFocus() {
        mInputView.showNKeyboard();
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newStr = mInputView.getNKeyboardText();
            for (int i = 0; i < newStr.length(); i++) {
                if (mPasswordArr[i] == null) {
                    String newNum = newStr.substring(i, i + 1);
                    mPasswordArr[i] = newNum;
                    mViewArr[i].setText(newNum);
                    notifyTextChanged();
                    break;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void notifyTextChanged() {
        if (mListener == null)
            return;
        String currentPsw = getPassWord();
        mListener.onTextChanged(currentPsw);
        if (currentPsw.length() == mPasswordLength) {
            mListener.onInputFinish(currentPsw);
            hideKeyWorld();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putStringArray("passwordArr", mPasswordArr);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mPasswordArr = bundle.getStringArray("passwordArr");
            state = bundle.getParcelable("instanceState");
            mInputView.removeTextChangedListener(textWatcher);
            setPassword(getPassWord());
            mInputView.addTextChangedListener(textWatcher);
        }
        super.onRestoreInstanceState(state);
    }

    //TODO
    //@Override
    private void setError(String error) {
        mInputView.setError(error);
    }

    /**
     * Return the text the PasswordView is displaying.
     */
    @Override
    public String getPassWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mPasswordArr.length; i++) {
            if (mPasswordArr[i] != null)
                sb.append(mPasswordArr[i]);
        }
        return sb.toString();
    }

    /**
     * Clear the passwrod the PasswordView is displaying.
     */
    @Override
    public void clearPassword() {
        for (int i = 0; i < mPasswordArr.length; i++) {
            mPasswordArr[i] = null;
            mViewArr[i].setText(null);
        }
    }

    /**
     * Sets the string value of the PasswordView.
     */
    @Override
    public void setPassword(String password) {
        clearPassword();

        if (TextUtils.isEmpty(password))
            return;

        char[] pswArr = password.toCharArray();
        for (int i = 0; i < pswArr.length; i++) {
            if (i < mPasswordArr.length) {
                mPasswordArr[i] = pswArr[i] + "";
                mViewArr[i].setText(mPasswordArr[i]);
            }
        }
    }

    /**
     * Set the enabled state of this view.
     */
    @Override
    public void setPasswordVisibility(boolean visible) {
        for (TextView textView : mViewArr) {
            textView.setTransformationMethod(visible ? null : mTransformationMethod);
            if (textView instanceof EditText) {
                EditText et = (EditText) textView;
                et.setSelection(et.getText().length());
            }
        }
    }

    /**
     * Toggle the enabled state of this view.
     */
    @Override
    public void togglePasswordVisibility() {
        boolean currentVisible = getPassWordVisibility();
        setPasswordVisibility(!currentVisible);
    }

    /**
     * Get the visibility of this view.
     */
    private boolean getPassWordVisibility() {
        return mViewArr[0].getTransformationMethod() == null;
    }

    /**
     * Register a callback to be invoked when password changed.
     */
    @Override
    public void setOnPasswordChangedListener(OnPasswordChangedListener listener) {
        this.mListener = listener;
    }

    @Override
    public void setPasswordType(PasswordType passwordType) {
        boolean visible = getPassWordVisibility();
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        for (TextView textView : mViewArr)
            textView.setInputType(inputType);

        setPasswordVisibility(visible);
    }

    @Override
    public void setBackground(Drawable background) {
    }

    @Override
    public void setBackgroundColor(int color) {
    }

    @Override
    public void setBackgroundResource(int resid) {
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
    }


    /**
     * Interface definition for a callback to be invoked when the password changed or is at the maximum length.
     */
    public interface OnPasswordChangedListener {

        /**
         * Invoked when the password changed.
         *
         * @param psw new text
         */
        void onTextChanged(String psw);

        /**
         * Invoked when the password is at the maximum length.
         *
         * @param psw complete text
         */
        void onInputFinish(String psw);

    }
}
