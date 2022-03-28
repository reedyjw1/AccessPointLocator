package edu.udmercy.accesspointlocater.arch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class AccessPointImageView(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    companion object {
        private const val TAG = "CircleView"
    }

    private var strokeWidth = 0
    private val paint = Paint()
    var touchPoints: MutableList<Pair<Int, PointF>> = mutableListOf()
    var maxPoints = 2
    var threshold = 50f
    var listener: CircleViewPointListener? = null

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }
        val radius = 25f
        val strokeRadius = 32f

        // Previously clicked points
        touchPoints.forEach { pair ->
            sourceToViewCoord(pair.second)?.let { source ->
                paint.isAntiAlias = true
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeCap = Paint.Cap.ROUND
                paint.color = Color.BLACK
                canvas.drawCircle(source.x, source.y, strokeRadius, paint)
                paint.strokeCap = Paint.Cap.ROUND
                paint.color = Color.CYAN
                canvas.drawCircle(source.x, source.y, radius, paint)
                paint.color = Color.BLACK
                paint.textSize = 30f
                canvas.drawText(pair.first.toString(), source.x-(radius/4f), source.y+(radius/4f), paint)
            }
        }
    }

    init {
        initialise()
    }
}