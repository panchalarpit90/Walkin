package com.task.walkin.ui.dashboard.custombottom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.task.walkin.R

class CustomBottomNavBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class NavItem(
        val iconRes: Int,
        val label: String,
        val isFab: Boolean = false
    )

    private val items = listOf(
        NavItem(R.drawable.vector_smart_object_5,        "My Booking"),
        NavItem(R.drawable.vector_smart_object_8,        "Scan QR"),
        NavItem(R.drawable.vector_smart_object_copy_3_2, "Home", isFab = true),
        NavItem(R.drawable.shape_1,                      "My QR"),
        NavItem(R.drawable.ic_profile,                   "Profile")
    )

    private val fabIndex = items.indexOfFirst { it.isFab }.takeIf { it >= 0 } ?: 2


    private val colorBg        = Color.WHITE
    private val colorAccent    = Color.parseColor("#2196F3")
    private val colorInactive  = Color.parseColor("#AAAAAA")
    private val colorFabBg     = Color.parseColor("#2196F3")
    private val colorIconInFab = Color.WHITE
    private val colorDivider   = Color.parseColor("#F7F7F7")

    private val bgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorBg
            style = Paint.Style.FILL
        }
    }
    private val dividerPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorDivider
            style = Paint.Style.FILL
        }
    }
    private val fabPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorFabBg
            style = Paint.Style.FILL
        }
    }
    private val fabShadowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color      = Color.parseColor("#55000000")
            style      = Paint.Style.FILL
            maskFilter = BlurMaskFilter(dpToPx(10f), BlurMaskFilter.Blur.NORMAL)
        }
    }
    private val labelPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface  = Typeface.create("sans-serif", Typeface.NORMAL)
        }
    }

    private val iconBitmapsLg = arrayOfNulls<Bitmap>(items.size)
    private val iconBitmapsSm = arrayOfNulls<Bitmap>(items.size)
    private var iconsLoaded   = false


    private var selectedIndex = fabIndex
    private var fabCx         = 0f
    private var isLayoutDone  = false
    private var fabAnimator: ValueAnimator? = null

    var onItemSelected: ((index: Int) -> Unit)? = null


    private fun fabProtrude() = dpToPx(30f)
    private fun fabRadius()   = dpToPx(28f)
    private fun barTop()      = fabProtrude()

    private fun fabCenterY()  = fabProtrude()

    private fun fabIconSize() = (fabRadius() * 0.80f).toInt()
    private fun itemWidth()         = width.toFloat() / items.size
    private fun itemCenterX(i: Int) = itemWidth() * i + itemWidth() / 2f

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        if (w == 0 || h == 0) return
        loadIcons()
        fabCx        = itemCenterX(selectedIndex)
        isLayoutDone = true
        invalidate()
    }

    private fun loadIcons() {
        val lgSz = fabIconSize().coerceAtLeast(4)
        val smSz = dpToPx(22f).toInt().coerceAtLeast(4)

        items.forEachIndexed { i, item ->
            val drawable = try {
                ContextCompat.getDrawable(context, item.iconRes)
            } catch (e: Exception) { null } ?: return@forEachIndexed

            drawable.setBounds(0, 0, lgSz, lgSz)
            val bmpLg = Bitmap.createBitmap(lgSz, lgSz, Bitmap.Config.ARGB_8888)
            drawable.draw(Canvas(bmpLg))
            iconBitmapsLg[i] = bmpLg

            drawable.setBounds(0, 0, smSz, smSz)
            val bmpSm = Bitmap.createBitmap(smSz, smSz, Bitmap.Config.ARGB_8888)
            drawable.draw(Canvas(bmpSm))
            iconBitmapsSm[i] = bmpSm
        }
        iconsLoaded = true
    }

    override fun onDraw(canvas: Canvas) {
        if (!isLayoutDone || !iconsLoaded) return

        val w = width.toFloat()
        val h = height.toFloat()
        val bt = barTop()

        canvas.drawRect(0f, bt, w, h, bgPaint)

        canvas.drawRect(0f, bt, w, bt + dpToPx(1f), dividerPaint)

        items.forEachIndexed { i, item ->
            if (i != selectedIndex) drawInactiveTab(canvas, i, item)
        }

        drawFab(canvas)

        drawActiveLabel(canvas)
    }

    private fun drawInactiveTab(canvas: Canvas, index: Int, item: NavItem) {
        val cx      = itemCenterX(index)
        val bmp     = iconBitmapsSm[index] ?: return
        val bt      = barTop()
        val barH    = height - bt
        val iconTop = bt + barH * 0.25f - bmp.height / 2f
        val left    = cx - bmp.width / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = PorterDuffColorFilter(colorInactive, PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(bmp, left, iconTop.coerceAtLeast(bt + dpToPx(4f)), paint)

        val labelY = iconTop + bmp.height + dpToPx(13f)
        labelPaint.textSize = dpToPx(12f)
        labelPaint.color    = colorInactive
        labelPaint.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        canvas.drawText(
            item.label, cx,
            labelY.coerceAtMost(height.toFloat() - dpToPx(2f)),
            labelPaint
        )
    }

    private fun drawFab(canvas: Canvas) {
        val cy = fabCenterY()
        val r  = fabRadius()

        canvas.drawCircle(fabCx, cy + dpToPx(3f), r, fabShadowPaint)
        canvas.drawCircle(fabCx, cy, r, fabPaint)

        val bmp      = iconBitmapsLg[selectedIndex] ?: return
        val iconDraw = r * 0.75f
        val scale    = iconDraw / bmp.width.toFloat()
        val left     = fabCx - bmp.width  * scale / 2f
        val top      = cy    - bmp.height * scale / 2f
        val matrix   = Matrix().apply {
            postScale(scale, scale)
            postTranslate(left, top)
        }
        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = PorterDuffColorFilter(colorIconInFab, PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(bmp, matrix, iconPaint)
    }

    private fun drawActiveLabel(canvas: Canvas) {
        val bt = barTop()
        val barH = height - bt
        val bmp = iconBitmapsSm[selectedIndex]
        val iconTop = bt + barH * 0.25f - (bmp?.height ?: 0) / 2f
        val labelY = (iconTop + (bmp?.height ?: 0) + dpToPx(13f))
            .coerceIn(bt + dpToPx(4f), height.toFloat() - dpToPx(2f))

        labelPaint.textSize = dpToPx(12f)
        labelPaint.color    = colorAccent
        labelPaint.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        canvas.drawText(items[selectedIndex].label, fabCx, labelY, labelPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (!isLayoutDone) return true
            val tapped = (event.x / itemWidth()).toInt().coerceIn(0, items.size - 1)
            if (tapped != selectedIndex) selectItem(tapped)
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean { super.performClick(); return true }

    fun selectItem(index: Int) {
        selectedIndex = index
        if (isLayoutDone) animateFabTo(itemCenterX(index))
        onItemSelected?.invoke(index)
    }

    fun selectItemWithoutCallback(index: Int) {
        if (selectedIndex == index) return
        selectedIndex = index
        if (isLayoutDone) animateFabTo(itemCenterX(index))
        else invalidate()
    }

    fun getSelectedIndex() = selectedIndex

    private fun animateFabTo(targetX: Float) {
        fabAnimator?.cancel()
        fabAnimator = ValueAnimator.ofFloat(fabCx, targetX).apply {
            duration     = 700
            interpolator = DecelerateInterpolator(2f)
            addUpdateListener {
                fabCx = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun dpToPx(dp: Float) = dp * resources.displayMetrics.density
}