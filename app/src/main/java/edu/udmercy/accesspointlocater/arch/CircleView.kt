package edu.udmercy.accesspointlocater.arch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.android.synthetic.main.fragment_execute_session.*


class CircleView(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {
    private var strokeWidth = 0
    private val paint = Paint()
    private var touchPoints = mutableListOf<PointF>()

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()
        setOnTouchListener { subView, motionEvent ->
            subView.performClick()
            return@setOnTouchListener gestureDetector.onTouchEvent(motionEvent)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }
        touchPoints.forEach {
            sourceToViewCoord(it)?.let { source ->
                val radius = 25f
                val strokeRadius = 32f
                paint.isAntiAlias = true
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeCap = Cap.ROUND
                paint.color = Color.BLACK
                canvas.drawCircle(source.x, source.y, strokeRadius, paint)
                paint.strokeCap = Cap.ROUND
                paint.color = Color.WHITE
                canvas.drawCircle(source.x, source.y, radius, paint)
            }
        }
    }

    init {
        initialise()
    }

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (isReady) {
                    val coordinate: PointF = viewToSourceCoord(e.x, e.y) ?: return true
                    Log.i("TEST", "onSingleTapConfirmed: ")
                    touchPoints.add(coordinate)
                    invalidate()
                }
                return true
            }
        })
}