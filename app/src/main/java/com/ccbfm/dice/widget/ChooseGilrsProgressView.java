package com.ccbfm.dice.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ccbfm.dice.R;

public class ChooseGilrsProgressView extends View {

    private Paint mBroaderPaint;//边框画笔
    private Paint mBackgroundPaint;//边框内背景画笔
    private Paint mFillPaint;//填充画笔

    //外边的宽度
    private static final int DEFAULT_BORDER_WIDTH = 2;
    private int BorderWidth = DEFAULT_BORDER_WIDTH;
    //假设是45°
    double degrees;
    //画笔Paint
    private Paint mTextPaint;
    //宽和高
    private int width;
    private int height;
    //路径
    float r;
    //边框线条 内部背景 内部进度
    private Path pathFrame, pathInside, pathFill;
    //当前进度
    private float progress;
    //最大进度
    private float maxProgress;

    public ChooseGilrsProgressView(Context context) {
        this(context, null);
        initView();
    }

    public ChooseGilrsProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initView();
    }

    public ChooseGilrsProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChooseGilrsProgressView, defStyleAttr, 0);
        try {
            BorderWidth = ta.getDimensionPixelSize(R.styleable.ChooseGilrsProgressView_pborderWidth, DEFAULT_BORDER_WIDTH);
            degrees = ta.getFloat(R.styleable.ChooseGilrsProgressView_pdegrees, 45);
        } finally {
            ta.recycle();
        }
        initView();
    }


    private void initView() {
        //边框 抗锯齿
        mBroaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBroaderPaint.setColor(0xffffffff);
        mBroaderPaint.setStrokeWidth(BorderWidth);//线宽度

        mBroaderPaint.setStyle(Paint.Style.STROKE);
        mBroaderPaint.setStrokeJoin(Paint.Join.MITER);
        mBroaderPaint.setAntiAlias(true);

        //背景抗锯齿
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(0xff686B7E);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundPaint.setStrokeJoin(Paint.Join.MITER);

        //进度
        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(0xffFE88A7);
        mFillPaint.setStrokeJoin(Paint.Join.MITER);

        //字体显示进度
        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(24f);
        mTextPaint.setTextAlign(Paint.Align.LEFT);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取view的宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //固定死的
        r = height / (float) Math.tan(Math.toRadians(degrees));

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //  initView();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        pathFrame = new Path();
        pathInside = new Path();
        pathFill = new Path();
        //画边框
        drawSide(canvas);
        //画背景
        drawBackground(canvas);
        //画进度
        drawProgress(canvas);
        //画文字
        drawProgressText(canvas);
    }

    //绘制背景边框
    private void drawSide(Canvas canvas) {

        pathFrame.moveTo(r, 0);
        pathFrame.lineTo(width, 0);
        pathFrame.lineTo(width - r, height);
        pathFrame.lineTo(0, height);
        pathFrame.close();
        canvas.drawPath(pathFrame, mBroaderPaint);
    }

    //绘制背景内部
    private void drawBackground(Canvas canvas) {
        pathInside.moveTo(r, BorderWidth);
        pathInside.lineTo(width - BorderWidth - BorderWidth, BorderWidth);
        pathInside.lineTo(width - r, height - BorderWidth);
        pathInside.lineTo(2 * BorderWidth, height - BorderWidth);
        pathInside.close();
        canvas.drawPath(pathInside, mBackgroundPaint);
    }


    //绘制进度
    private void drawProgress(Canvas canvas) {
        float right;
        //如果当前进度大于最大进度 就是最大的进度
        if (progress > maxProgress) {
            right = width;
        } else {//否则就是按比例计算
            right = (progress / maxProgress) * width;
        }
        // 第一种情况 三角形 因为默认是45度 所以直角三角形 宽 高相等
        if (right <= r) {
            pathFill.moveTo(right + 2 * BorderWidth, height - right - BorderWidth);
            pathFill.lineTo(right + 2 * BorderWidth, height - BorderWidth);
            pathFill.lineTo(2 * BorderWidth, height - BorderWidth);
            pathFill.close();
            canvas.drawPath(pathFill, mFillPaint);
        } else if (right > r && right <= width - r) {
            pathFill.moveTo(r, BorderWidth);
            pathFill.lineTo(right, BorderWidth);
            pathFill.lineTo(right, height - BorderWidth);
            pathFill.lineTo(2 * BorderWidth, height - BorderWidth);
            pathFill.close();
            canvas.drawPath(pathFill, mFillPaint);
        } else if (right > width - r && right < width) {
            pathFill.moveTo(r, BorderWidth);
            pathFill.lineTo(right, BorderWidth);
            pathFill.lineTo(right, width - right - BorderWidth);
            pathFill.lineTo(width - r, height - BorderWidth);
            pathFill.lineTo(2 * BorderWidth, height - BorderWidth);
            pathFill.close();
            canvas.drawPath(pathFill, mFillPaint);
        } else if (right == width) {
            pathFill.moveTo(r + BorderWidth / 2, BorderWidth);
            pathFill.lineTo(width - 2 * BorderWidth, BorderWidth);
            pathFill.lineTo(width - r - BorderWidth / 2, height - BorderWidth);
            pathFill.lineTo(2 * BorderWidth, height - BorderWidth);
            pathFill.close();
            canvas.drawPath(pathFill, mFillPaint);
        }
    }

    //绘制文本
    private void drawProgressText(Canvas canvas) {
        float textWidth = mTextPaint.measureText((int) progress + "/" + (int) maxProgress + "");
        //文字的y轴坐标
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float y = height / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        //文字内容 描绘x轴和y轴
        //canvas.drawText((int) progress + "/" + (int) maxProgress + "", width / 2 - textWidth / 2, y, mTextPaint);
        canvas.drawText((int)(progress * 100 / maxProgress) + "%", width / 2 - textWidth / 2, y, mTextPaint);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    //设置最大进度
    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }
}
