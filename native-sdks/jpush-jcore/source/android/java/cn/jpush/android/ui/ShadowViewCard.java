package cn.jpush.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ShadowViewCard extends RelativeLayout {
    private static final int DEFAULT_SHADOW_COLOR = Color.parseColor("#1f253044");
    private final int shadowRound = 52;
    private final int shadowRadius = 11;
    private final int shadowTopHeight = 15;
    private final int shadowLeftHeight = 5;
    private final int shadowRightHeight = 5;
    private final int shadowBottomHeight = 20;
    private final int shadowOffsetX = 0;
    private final int shadowOffsetY = 8;

    public ShadowViewCard(Context context) {
        this(context, null);
    }

    public ShadowViewCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowViewCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(shadowLeftHeight, shadowTopHeight, shadowRightHeight, shadowBottomHeight);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(shadowRadius, shadowOffsetX, shadowOffsetY, DEFAULT_SHADOW_COLOR);
        RectF rect = new RectF(
            shadowLeftHeight,
            shadowTopHeight,
            getWidth() - shadowRightHeight,
            getHeight() - shadowBottomHeight
        );
        canvas.drawRoundRect(rect, shadowRound, shadowRound, paint);
        super.dispatchDraw(canvas);
    }
}
