package com.iphoto.plus.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class RadioPxButton extends androidx.appcompat.widget.AppCompatRadioButton {
    public RadioPxButton(Context context) {
        super(context);
    }

    public RadioPxButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioPxButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        changeButtonsImages();
    }


    private void changeButtonsImages(){

    }


}
