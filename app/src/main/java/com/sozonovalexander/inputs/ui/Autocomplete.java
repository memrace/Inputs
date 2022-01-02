package com.sozonovalexander.inputs.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.sozonovalexander.inputs.R;

import org.jetbrains.annotations.NotNull;

public class Autocomplete extends LinearLayout {
    private TextInputLayout inputLayout;

    public Autocomplete(Context context) {
        super(context);
        init(context, null);
    }

    public Autocomplete(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Autocomplete(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.autocomplete, this);
        initComponents();
        if (attrs != null)
            setAttrs(attrs);
    }

    private void initComponents() {
        inputLayout = (TextInputLayout) findViewById(R.id.inputTextLayout);
    }

    private void setAttrs(@NotNull AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.Autocomplete);
        ta.recycle();
    }
}
