package com.example.umonekeyshare;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UMImageButtonShareItem extends LinearLayout {


    private  ImageView   imageViewbutton;


    private  TextView   textView;


    public UMImageButtonShareItem(Context context,AttributeSet attrs) {
        super(context,attrs);


        imageViewbutton = new ImageView(context, attrs);


        imageViewbutton.setPadding(DpUtils.dip2px(context,16), 0, DpUtils.dip2px(context,16), 0);


        textView =new TextView(context, attrs);
        //ˮƽ����
        textView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);


        textView.setPadding(0, 0, 0, 0);


        textView.setTextSize(13);


        textView.setTextColor(getResources().getColor(R.color.um_share_textcolor));


        setClickable(true);


        setFocusable(true);


        setOrientation(LinearLayout.VERTICAL);


        addView(imageViewbutton);


        addView(textView);
    }


    public ImageView getImageViewbutton() {
        return imageViewbutton;
    }


    public void setImageViewbutton(ImageView imageViewbutton) {
        this.imageViewbutton = imageViewbutton;
    }
}