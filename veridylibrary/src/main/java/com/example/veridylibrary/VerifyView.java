package com.example.veridylibrary;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO: document your custom view class.
 */
public class VerifyView extends View {
    private String textBefore, textAfter; // TODO: use a default from R.string...
    private int firstColor = Color.RED; // TODO: use a default from R.color...
    private int secColor;
    private float radius = 0; // TODO: use a default from R.dimen...
    private int textColor = Color.WHITE;
    private float textSize = 14;

    private int intervalSec;
    private int cutDown;


    private TextPaint mTextPaint;
    private Paint mPaint;
    private float mTextWidth;
    private float mTextHeight;

    private int left,right,top,bottom;

    //当前要显示的文字
    private String textToShow;
    //当前要填充的颜色
    private int solidColor;



    public VerifyView(Context context) {
        super(context);
        init(null, 0);
    }

    public VerifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VerifyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.VerifyView, defStyle, 0);

        textBefore = a.getString(
                R.styleable.VerifyView_textBefore);
        textAfter = a.getString(R.styleable.VerifyView_textAfter);
        firstColor = a.getColor(
                R.styleable.VerifyView_firstColor,Color.RED);
        //默认灰色
        secColor = a.getColor(R.styleable.VerifyView_secColor,Color.GRAY);
        radius = a.getDimension(
                R.styleable.VerifyView_radius, 0);
        textColor = a.getColor(R.styleable.VerifyView_textColor,Color.WHITE);
        textSize = a.getDimension(R.styleable.VerifyView_textSize,textSize);
        intervalSec = a.getInt(R.styleable.VerifyView_interval,60);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes


        textToShow = textBefore;
        solidColor = firstColor;
        cutDown = intervalSec;
        invalidateTextPaintAndMeasurements();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnabled(false);
                    solidColor = secColor;
                    timer.schedule(task,0,1000);


            }
        });

    }
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if(cutDown > 0){
                cutDown--;
                textToShow = textAfter+ " "+cutDown+"s";

            }else {
                textToShow = textBefore;
                solidColor = firstColor;
                timer.cancel();
                setEnabled(true);
            }
            invalidateTextPaintAndMeasurements();
            postInvalidate();
        }
    };

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        mTextWidth = mTextPaint.measureText(textToShow);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //防止出现椭圆，圆角半径不能超过那啥
        int maxRadius = Math.min(widthSize/2,heightSize/2);
        if(radius >maxRadius){
            radius = maxRadius;
        }
        setMeasuredDimension(widthSize,heightSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.

        drawPath(canvas,getWidth()/2,getHeight()/2);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(textToShow,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawPath(Canvas canvas,int cx,int cy) {
        if(mPaint == null){
            mPaint = new Paint();
        }
        mPaint.setAntiAlias(true);
        mPaint.setColor(solidColor);
        mPaint.setStyle(Paint.Style.FILL);

        left = top = 0;
        right = getWidth();
        bottom = getHeight();
        canvas.drawRoundRect(left,top,right,bottom,radius,radius,mPaint);





    }
}
