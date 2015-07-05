package com.frankd.wttv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by FrankD on 12-6-2015.
 */
public class DiamondButton extends ImageButton {

    public DiamondButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onDraw(Canvas canvas) {

        int mWidth = canvas.getWidth();
        int mHeight = canvas.getHeight();

        Path mPath = new Path();
        mPath.moveTo(mWidth / 2, 0);
        mPath.lineTo(mWidth , mHeight/2);
        mPath.lineTo(mWidth /2 , mHeight);
        mPath.lineTo(0, mHeight / 2);
        mPath.lineTo(mWidth / 2, 0);

        //setup the paint for fill
        Paint mBorderPaint = new Paint();
        mBorderPaint.setAlpha(255);
        mBorderPaint.setColor(getResources().getColor(R.color.teal));
        mBorderPaint.setStyle(Paint.Style.FILL);

        //draw it
        canvas.drawPath(mPath, mBorderPaint);

        super.onDraw(canvas);

    }

}
