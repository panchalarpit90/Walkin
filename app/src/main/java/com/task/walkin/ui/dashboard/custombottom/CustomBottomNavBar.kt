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

    // ─── Model ───────────────────────────────────────────────────────────────

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

    // ─── Colors ──────────────────────────────────────────────────────────────

    private val colorBg        = Color.WHITE
    private val colorAccent    = Color.parseColor("#2196F3")
    private val colorInactive  = Color.parseColor("#AAAAAA")
    private val colorFabBg     = Color.parseColor("#2196F3")
    private val colorIconInFab = Color.WHITE
    private val colorDivider   = Color.parseColor("#E0E0E0")

    // ─── Paints (lazy — safe, resources always ready on first draw) ──────────

    private val bgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorBg; style = Paint.Style.FILL
        }
    }
    private val dividerPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorDivider; style = Paint.Style.FILL
        }
    }
    private val fabPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorFabBg; style = Paint.Style.FILL
        }
    }
    private val fabShadowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#33000000")
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(dpToPx(6f), BlurMaskFilter.Blur.NORMAL)
        }
    }
    private val labelPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface  = Typeface.create("sans-serif", Typeface.NORMAL)
        }
    }

    // ─── Bitmaps ─────────────────────────────────────────────────────────────

    private val iconBitmapsLg = arrayOfNulls<Bitmap>(items.size)
    private val iconBitmapsSm = arrayOfNulls<Bitmap>(items.size)
    private var iconsLoaded   = false

    // ─── State ───────────────────────────────────────────────────────────────

    private var selectedIndex  = fabIndex
    private var fabCx          = 0f
    private var isLayoutDone   = false
    private var fabAnimator: ValueAnimator? = null

    var onItemSelected: ((index: Int) -> Unit)? = null

    // ─── Init ────────────────────────────────────────────────────────────────

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    // ─── onSizeChanged ───────────────────────────────────────────────────────

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        if (w == 0 || h == 0) return
        loadIcons(h)
        fabCx = itemCenterX(selectedIndex)
        isLayoutDone = true
        invalidate()
    }

    private fun loadIcons(h: Int) {
        // FAB icon size: fits inside the circle nicely
        val lgSz = fabIconSize(h).coerceAtLeast(4)
        // Inactive icon: fixed 22dp
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

    // ─── Geometry ────────────────────────────────────────────────────────────

    /**
     * FAB radius = 28dp fixed. This keeps the circle a consistent size
     * regardless of bar height, and prevents it from overflowing.
     */
    private fun fabRadius()     = dpToPx(28f)

    /**
     * FAB center Y: positioned so the circle sits ON the bar with a slight
     * upward raise of ~8dp above the bar top edge.
     * fabCy = topOfBar - raise + radius
     *       = 0 - (-8dp) + 28dp ... but we keep it inside the view bounds.
     * Result: circle top is at -8dp (slightly above bar),
     *         circle bottom is at 48dp — well inside 70dp bar.
     */
    private fun fabCenterY()    = dpToPx(8f) + fabRadius()   // = ~36dp from top of view

    private fun fabIconSize(h: Int) = (fabRadius() * 1.0f).toInt()

    private fun itemWidth()         = width.toFloat() / items.size
    private fun itemCenterX(i: Int) = itemWidth() * i + itemWidth() / 2f

    // Inactive icon: vertically centered in upper portion of bar
    private fun inactiveIconTopY(iconH: Int): Float {
        val barMid = height * 0.38f
        return (barMid - iconH / 2f).coerceAtLeast(dpToPx(6f))
    }

    private fun inactiveLabelY(iconBottom: Float) = iconBottom + dpToPx(3f) + dpToPx(10f)

    private fun activeLabelY(): Float {
        val cy = fabCenterY()
        val r  = fabRadius()
        return (cy + r + dpToPx(4f) + dpToPx(10f))
            .coerceAtMost(height.toFloat() - dpToPx(2f))
    }

    // ─── Draw ────────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        if (!isLayoutDone || !iconsLoaded) return

        val w = width.toFloat()
        val h = height.toFloat()

        // 1. White bar background
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // 2. Top divider
        canvas.drawRect(0f, 0f, w, dpToPx(1f), dividerPaint)

        // 3. Inactive tabs
        items.forEachIndexed { i, item ->
            if (i != selectedIndex) drawInactiveTab(canvas, i, item)
        }

        // 4. FAB circle (slides between positions)
        drawFab(canvas)

        // 5. Active label
        drawActiveLabel(canvas)
    }

    private fun drawInactiveTab(canvas: Canvas, index: Int, item: NavItem) {
        val cx   = itemCenterX(index)
        val bmp  = iconBitmapsSm[index] ?: return
        val top  = inactiveIconTopY(bmp.height)
        val left = cx - bmp.width / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = PorterDuffColorFilter(colorInactive, PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(bmp, left, top, paint)

        labelPaint.textSize = dpToPx(10f)
        labelPaint.color    = colorInactive
        labelPaint.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        canvas.drawText(item.label, cx, inactiveLabelY(top + bmp.height), labelPaint)
    }

    private fun drawFab(canvas: Canvas) {
        val cy = fabCenterY()
        val r  = fabRadius()

        // Soft shadow
        canvas.drawCircle(fabCx, cy + dpToPx(3f), r, fabShadowPaint)
        // Blue circle
        canvas.drawCircle(fabCx, cy, r, fabPaint)

        // White icon inside
        val bmp = iconBitmapsLg[selectedIndex] ?: return
        val iconDraw = r * 0.85f
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
        labelPaint.textSize = dpToPx(10f)
        labelPaint.color    = colorAccent
        labelPaint.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        canvas.drawText(items[selectedIndex].label, fabCx, activeLabelY(), labelPaint)
    }

    // ─── Touch ───────────────────────────────────────────────────────────────

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

    // ─── Public API ──────────────────────────────────────────────────────────

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

    // ─── Animation ───────────────────────────────────────────────────────────

    private fun animateFabTo(targetX: Float) {
        fabAnimator?.cancel()
        fabAnimator = ValueAnimator.ofFloat(fabCx, targetX).apply {
            duration     = 320
            interpolator = DecelerateInterpolator(2f)
            addUpdateListener {
                fabCx = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    // ─── Util ────────────────────────────────────────────────────────────────

    private fun dpToPx(dp: Float) = dp * resources.displayMetrics.density
}