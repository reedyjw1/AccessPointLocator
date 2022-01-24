package edu.udmercy.accesspointlocater.arch

import android.annotation.SuppressLint
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
import edu.udmercy.accesspointlocater.features.session.room.AccessPoint
import kotlinx.android.synthetic.main.fragment_execute_session.*
import kotlin.math.pow
import kotlin.math.sqrt


class CircleView(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    companion object {
        private const val TAG = "CircleView"
    }
    private var strokeWidth = 0
    private val paint = Paint()
    var touchedPoint: PointF? = null
    var completedPointScans: List<AccessPoint> = listOf()
    var threshold = 50f
    var listener: CircleViewPointListener? = null

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        strokeWidth = (density / 60f).toInt()
        setOnTouchListener { subView, motionEvent ->
            subView.performClick()
            return@setOnTouchListener gestureDetector.onTouchEvent(motionEvent)
        }
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

        // Currently Selected Points
        touchedPoint?.let {
            sourceToViewCoord(it)?.let { source ->
                Log.i(TAG, "onDraw: Drawing point...")
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

        // Previously clicked points
        completedPointScans.forEach {
            val tempPoint = PointF(it.currentLocationX.toFloat(), it.currentLocationY.toFloat())
            sourceToViewCoord(tempPoint)?.let { source ->
                paint.isAntiAlias = true
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeCap = Cap.ROUND
                paint.color = Color.BLACK
                canvas.drawCircle(source.x, source.y, strokeRadius, paint)
                paint.strokeCap = Cap.ROUND
                paint.color = Color.GREEN
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
                    val source = sourceToViewCoord(coordinate) ?: return true

                    touchedPoint?.let { it ->
                        val touched = sourceToViewCoord(it) ?: return true
                        if (euclideanDistance(source, touched, threshold).first) {
                            touchedPoint = null
                            invalidate()
                        }
                        listener?.onPointsChanged(null)
                        return true
                    }
                    Log.i(TAG, "onSingleTapConfirmed: touched=$coordinate")
                    touchedPoint = coordinate
                    listener?.onPointsChanged(coordinate)
                    invalidate()



                    // Use again when Touching Completed Points is implemented
                    /*var closestPoint: Pair<PointF, Float> = Pair(PointF(-1f,-1f), Float.MAX_VALUE)
                    touchPoints.forEach {
                        val tempSource = sourceToViewCoord(it) ?: return true
                        val distancePair = euclideanDistance(source, tempSource, threshold)
                        if (distancePair.first && distancePair.second < closestPoint.second && it == touchPoints.last()) {
                            closestPoint = Pair(it, distancePair.second)
                        }
                    }
                    // If there is no close point, add the new point
                    if (closestPoint.second == Float.MAX_VALUE && touchPoints.size < numberOfPoints) {
                        touchPoints.add(coordinate)
                        invalidate()
                    } else if(closestPoint.second != Float.MAX_VALUE){
                        // Remove the closes point to the touch event
                        touchPoints.remove(closestPoint.first)
                        invalidate()
                    }
                    listener?.onPointsChanged(touchPoints)*/
                }
                return true
            }
        })

    private fun euclideanDistance(point1: PointF, point2: PointF, threshold: Float): Pair<Boolean, Float> {
        val x = (point1.x - point2.x).pow(2)
        val y = (point1.y - point2.y).pow(2)
        val dist = sqrt(x + y)
        return Pair(dist <= threshold, dist)
    }
}