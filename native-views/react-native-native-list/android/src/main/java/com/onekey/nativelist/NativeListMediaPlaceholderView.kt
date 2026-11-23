package com.margelo.nitro.nativelist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.view.animation.PathInterpolator
import kotlin.math.sqrt
import kotlin.math.tan
import org.json.JSONObject

/**
 * Loading sheen and failed hatch drawn over a media tile while it has no picture. Geometry mirrors
 * the Web renderer (`MediaTileRowRenderer.ts`) and iOS (`NativeListMediaPlaceholderView.swift`) so
 * every platform draws the same placeholder.
 */
internal class NativeListMediaPlaceholderView(context: Context) : View(context) {
    enum class State {
        Hidden,
        Loading,
        Failed,
    }

    var state = State.Hidden
        private set

    var cornerRadius = 0f
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    private val density = resources.displayMetrics.density
    private val clip = Path()
    private val bounds = RectF()
    private val hatchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val hatchMaskPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN) }
    private val sheenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glyphPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
    private val picture = Path()
    private val slash = Path()
    private val pictureClip = Path()
    private var shine = Color.argb(191, 255, 255, 255)
    private var laidOutWidth = -1
    private var laidOutHeight = -1
    private var reduceMotion = false

    init {
        visibility = GONE
        setWillNotDraw(false)
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    fun applyTheme(theme: JSONObject?) {
        fun tint(name: String, fallback: String) =
            parseNativeListColor(theme?.optString(name, fallback)?.takeIf(String::isNotEmpty) ?: fallback)
        glyphPaint.color = withAlpha(tint("disabledText", "#8C8C8C"), 0.7f)
        fillPaint.color = tint("strongBackground", "#0000000F")
        borderPaint.color = withAlpha(tint("separator", "#E5E7EB"), 0.6f)
        val primary = tint("primaryText", "#202020")
        hatchPaint.color = Color.argb(11, Color.red(primary), Color.green(primary), Color.blue(primary))
        val row = tint("rowBackground", "#FFFFFF")
        val dark = Color.red(row) * 299 + Color.green(row) * 587 + Color.blue(row) * 114 < 128_000
        val nextShine = Color.argb(if (dark) 18 else 191, 255, 255, 255)
        if (nextShine != shine) {
            shine = nextShine
            laidOutWidth = -1
        }
        invalidate()
    }

    fun setState(next: State) {
        if (next == state) return
        state = next
        if (next == State.Loading) reduceMotion = animatorScale() == 0f
        visibility = if (next == State.Hidden) GONE else VISIBLE
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        laidOutWidth = -1
    }

    private fun layoutShapes() {
        if (laidOutWidth == width && laidOutHeight == height) return
        laidOutWidth = width
        laidOutHeight = height
        val w = width.toFloat()
        val h = height.toFloat()
        val center = 0.5f * sqrt(w * w + h * h)
        hatchMaskPaint.shader =
            RadialGradient(
                w / 2,
                h / 2,
                center.coerceAtLeast(1f),
                intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, Color.BLACK),
                floatArrayOf(0f, 0.22f, 0.7f, 1f),
                Shader.TileMode.CLAMP,
            )
        val transparentShine = shine and 0x00FFFFFF
        sheenPaint.shader =
            LinearGradient(
                0f,
                0f,
                w * 0.6f,
                0f,
                intArrayOf(transparentShine, shine, transparentShine),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP,
            )
        hatchPaint.strokeWidth = density
        // Glyph geometry in a 24-unit box, scaled to 28% of the tile and capped at 44pt.
        val size = minOf(w * 0.28f, 44f * density)
        val scale = size / 24f
        val matrix =
            Matrix().apply {
                setScale(scale, scale)
                postTranslate((w - size) / 2, (h - size) / 2)
            }
        glyphPaint.strokeWidth = 1.5f * scale
        picture.reset()
        picture.addRoundRect(RectF(3f, 3f, 21f, 21f), 4f, 4f, Path.Direction.CW)
        picture.addCircle(9f, 9f, 1.6f, Path.Direction.CW)
        picture.moveTo(5f, 20.5f)
        picture.lineTo(14.5f, 11f)
        picture.lineTo(21f, 17.5f)
        picture.transform(matrix)
        slash.reset()
        slash.moveTo(3f, 3f)
        slash.lineTo(21f, 21f)
        slash.transform(matrix)
        val band =
            Path().apply {
                moveTo(2.6f, -0.6f)
                lineTo(24.6f, 21.4f)
                lineTo(21.4f, 24.6f)
                lineTo(-0.6f, 2.6f)
                close()
                transform(matrix)
            }
        pictureClip.reset()
        pictureClip.addRect(0f, 0f, w, h, Path.Direction.CW)
        pictureClip.op(band, Path.Op.DIFFERENCE)
    }

    override fun onDraw(canvas: Canvas) {
        if (state == State.Hidden || width == 0 || height == 0) return
        layoutShapes()
        val w = width.toFloat()
        val h = height.toFloat()
        bounds.set(0f, 0f, w, h)
        clip.reset()
        clip.addRoundRect(bounds, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.save()
        canvas.clipPath(clip)
        // The placeholder owns the slot fill: the image view underneath is empty while
        // loading and hidden once every probe failed.
        canvas.drawRect(0f, 0f, w, h, fillPaint)
        if (state == State.Failed) drawHatch(canvas, w, h) else drawSheen(canvas, w, h)
        if (state == State.Failed) {
            canvas.save()
            canvas.clipPath(pictureClip)
            canvas.drawPath(picture, glyphPaint)
            canvas.restore()
            canvas.drawPath(slash, glyphPaint)
        } else canvas.drawPath(picture, glyphPaint)
        canvas.restore()
        val inset = borderPaint.strokeWidth / 2
        bounds.inset(inset, inset)
        canvas.drawRoundRect(bounds, cornerRadius - inset, cornerRadius - inset, borderPaint)
    }

    private fun drawHatch(canvas: Canvas, w: Float, h: Float) {
        val layer = canvas.saveLayer(0f, 0f, w, h, null)
        // Lines along x + y = c, 7pt apart measured perpendicular to the stripes.
        val step = 7f * density * sqrt(2f)
        var c = 0f
        while (c <= w + h) {
            canvas.drawLine(c, 0f, 0f, c, hatchPaint)
            c += step
        }
        canvas.drawRect(0f, 0f, w, h, hatchMaskPaint)
        canvas.restoreToCount(layer)
    }

    private fun drawSheen(canvas: Canvas, w: Float, h: Float) {
        // Band left edge travels from -80% to 130% of the tile, then rests for 30% of the period.
        // Every tile derives its phase from the shared uptime clock so the grid sweeps in step.
        val phase = (SystemClock.uptimeMillis() % SWEEP_PERIOD_MS).toFloat() / SWEEP_PERIOD_MS
        val progress = if (phase < 0.7f) SWEEP_EASING.getInterpolation(phase / 0.7f) else 1f
        val left = if (reduceMotion) w * 0.2f else w * (-0.8f + 2.1f * progress)
        sheenPaint.alpha = if (reduceMotion) 153 else 255
        canvas.save()
        canvas.translate(left + w * 0.3f, h / 2)
        canvas.skew(SKEW, 0f)
        canvas.translate(-w * 0.3f, 0f)
        canvas.drawRect(0f, -h, w * 0.6f, h, sheenPaint)
        canvas.restore()
        if (!reduceMotion && isAttachedToWindow) postInvalidateOnAnimation()
    }

    private fun animatorScale() =
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)

    private fun withAlpha(color: Int, factor: Float) =
        Color.argb(
            (Color.alpha(color) * factor).toInt(),
            Color.red(color),
            Color.green(color),
            Color.blue(color),
        )

    private companion object {
        const val SWEEP_PERIOD_MS = 1600L
        // CSS skewX(-12deg).
        val SKEW = tan(Math.toRadians(-12.0)).toFloat()
        val SWEEP_EASING = PathInterpolator(0.4f, 0f, 0.2f, 1f)
    }
}
