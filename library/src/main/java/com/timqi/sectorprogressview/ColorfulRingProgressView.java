package com.timqi.sectorprogressview;

import java.lang.annotation.Native;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ColorfulRingProgressView extends View {

    public static final int CLOCK_WISE = 1;
    public static final int COUNTER_CLOCK_WISE = -1;

    private int mDirection = CLOCK_WISE;
    private float mPercent = 75;
    private float mStrokeWidth;
    private int mBgColor = 0xffe1e1e1;
    private float mStartAngle = 0;
    private int mFgColorStart = 0xffffe400;
    private int mFgColorEnd = 0xffff4800;

    private LinearGradient mShader;
    private Context mContext;
    private RectF mOval;
    private Paint mPaint;

    private ObjectAnimator animator;
    private boolean mShowBgColor = true;

    private AnimatorListenerAdapter mListenerAdapter;

    public ColorfulRingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorfulRingProgressView, 0, 0);

        try {
            mBgColor = a.getColor(R.styleable.ColorfulRingProgressView_bgColor, 0xffe1e1e1);
            mFgColorEnd = a.getColor(R.styleable.ColorfulRingProgressView_fgColorEnd, 0xffff4800);

            mFgColorStart = a.getColor(R.styleable.ColorfulRingProgressView_fgColorStart, 0xffffe400);
            mPercent = a.getFloat(R.styleable.ColorfulRingProgressView_percent, 75);
            mStartAngle = a.getFloat(R.styleable.ColorfulRingProgressView_startAngle, 0) + 270;
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.ColorfulRingProgressView_strokeWidth, dp2px(21));
            mDirection = a.getInt(R.styleable.ColorfulRingProgressView_direction, CLOCK_WISE);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private int dp2px(float dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setShader(null);
        mPaint.setColor(mBgColor);
        if (mShowBgColor) {
            canvas.drawArc(mOval, 0, 360, false, mPaint);
        }

        mPaint.setShader(mShader);
        canvas.drawArc(mOval, mStartAngle, mDirection * mPercent * 3.6f, false, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updateOval();

        mShader = new LinearGradient(mOval.left, mOval.top, mOval.left, mOval.bottom, mFgColorStart, mFgColorEnd, Shader.TileMode.MIRROR);
    }

    public float getPercent() {
        return mPercent;
    }

    public void setPercent(float mPercent) {
        this.mPercent = mPercent;
        refreshTheLayout();
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
        mPaint.setStrokeWidth(mStrokeWidth);
        updateOval();
        refreshTheLayout();
    }

    private void updateOval() {
        int xp = getPaddingLeft() + getPaddingRight();
        int yp = getPaddingBottom() + getPaddingTop();
        mOval = new RectF(getPaddingLeft() + mStrokeWidth, getPaddingTop() + mStrokeWidth, getPaddingLeft() + (getWidth() - xp) - mStrokeWidth,
                getPaddingTop() + (getHeight() - yp) - mStrokeWidth);
    }

    public void setStrokeWidthDp(float dp) {
        this.mStrokeWidth = dp2px(dp);
        mPaint.setStrokeWidth(mStrokeWidth);
        updateOval();
        refreshTheLayout();
    }

    public void refreshTheLayout() {
        invalidate();
        requestLayout();
    }

    public int getFgColorStart() {
        return mFgColorStart;
    }

    public void setFgColorStart(int mFgColorStart) {
        this.mFgColorStart = mFgColorStart;
        mShader = new LinearGradient(mOval.left, mOval.top, mOval.left, mOval.bottom, mFgColorStart, mFgColorEnd, Shader.TileMode.MIRROR);
        refreshTheLayout();
    }

    public int getFgColorEnd() {
        return mFgColorEnd;
    }

    public void setFgColorEnd(int mFgColorEnd) {
        this.mFgColorEnd = mFgColorEnd;
        mShader = new LinearGradient(mOval.left, mOval.top, mOval.left, mOval.bottom, mFgColorStart, mFgColorEnd, Shader.TileMode.MIRROR);
        refreshTheLayout();
    }

    public void setBgColor(int bgColor) {
        mBgColor = bgColor;
    }

    public void showBgColor(boolean showBgColor) {
        mShowBgColor = showBgColor;
    }

    public void setDirection(boolean clockWise) {
        mDirection = clockWise ? CLOCK_WISE : COUNTER_CLOCK_WISE;
    }

    public boolean isClockWiseDirection() {
        return mDirection == CLOCK_WISE;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float mStartAngle) {
        this.mStartAngle = mStartAngle + 270;
        refreshTheLayout();
    }

    public void animateIndeterminate() {
        animateIndeterminate(800, new AccelerateDecelerateInterpolator());
    }

    public void animateIndeterminate(int durationOneCircle, TimeInterpolator interpolator) {
        animator = ObjectAnimator.ofFloat(this, "startAngle", getStartAngle(), getStartAngle() + 360);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animator.setDuration(durationOneCircle);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                if (mListenerAdapter != null) {
                    mListenerAdapter.onAnimationRepeat(animation);
                }
            }
        });
        animator.start();
    }

    public void stopAnimateIndeterminate() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    public void stopAnimateIndeterminate(AnimatorListenerAdapter listenerAdapter) {
        mListenerAdapter = listenerAdapter;
    }

    /**
     * Checks whether or not is progress animating.
     *
     * @return return true if indeterminate progress is in process.
     */
    public boolean isAnimateIndeterminate() {
        return animator != null;
    }
}
