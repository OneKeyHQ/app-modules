package cn.jpush.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
    private float width;
    private float height;
    private int leftTopRadius = 45;
    private int rightTopRadius = 45;
    private int rightBottomRadius = 45;
    private int leftBottomRadius = 45;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRadius(int leftTop, int rightTop, int rightBottom, int leftBottom) {
        this.leftTopRadius = leftTop;
        this.rightTopRadius = rightTop;
        this.rightBottomRadius = rightBottom;
        this.leftBottomRadius = leftBottom;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.width = getWidth();
        this.height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int horizontalNeed = Math.max(leftTopRadius, leftBottomRadius) + Math.max(rightTopRadius, rightBottomRadius);
        int verticalNeed = Math.max(leftTopRadius, rightTopRadius) + Math.max(leftBottomRadius, rightBottomRadius);
        if (width >= horizontalNeed && height > verticalNeed) {
            Path path = new Path();
            path.moveTo(leftTopRadius, 0.0f);
            path.lineTo(width - rightTopRadius, 0.0f);
            path.quadTo(width, 0.0f, width, rightTopRadius);
            path.lineTo(width, height - rightBottomRadius);
            path.quadTo(width, height, width - rightBottomRadius, height);
            path.lineTo(leftBottomRadius, height);
            path.quadTo(0.0f, height, 0.0f, height - leftBottomRadius);
            path.lineTo(0.0f, leftTopRadius);
            path.quadTo(0.0f, 0.0f, leftTopRadius, 0.0f);
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
