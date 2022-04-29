package edu.udmercy.accesspointlocater.arch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.android.synthetic.main.fragment_execute_session.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This ImageView was specifically used to allow the user to place only 2 points on the image,
 * a line would then be drawn between them. This was used to specify the scale of the floor plans
 * but is no longer used.
 */
class ScaleCircleView(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    companion object {
        private const val TAG = "CircleView"
    }
    private var strokeWidth = 0
    private val paint = Paint()
    var touchPoints: MutableList<PointF> = mutableListOf()
    var maxPoints = 1
    var threshold = 50f
    var listener: CircleViewPointListener? = null

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        setMinimumDpi(50)
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

        paint.strokeCap = Cap.ROUND
        paint.color = Color.GREEN

        // Draws the line between the two points (if they both exist)
        if (touchPoints.size == 2) {
            sourceToViewCoord(touchPoints[0])?.let { point1 ->
                sourceToViewCoord(touchPoints[1])?.let { point2 ->
                    paint.strokeCap = Cap.ROUND
                    paint.color = Color.BLACK
                    paint.strokeWidth = strokeWidth.toFloat()
                    canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint)
                }
            }
        }

        // Previously clicked points
        touchPoints.forEach {
            sourceToViewCoord(it)?.let { source ->
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

                    // Use again when Touching Completed Points is implemented
                    var closestPoint: Pair<PointF, Float> = Pair(PointF(-1f,-1f), Float.MAX_VALUE)
                    touchPoints.forEach {
                        val tempSource = sourceToViewCoord(it) ?: return true
                        val distancePair = euclideanDistance(source, tempSource, threshold)
                        if (distancePair.first && distancePair.second < closestPoint.second && it == touchPoints.last()) {
                            closestPoint = Pair(it, distancePair.second)
                        }
                    }
                    // If there is no close point, add the new point
                    if (closestPoint.second == Float.MAX_VALUE && touchPoints.size <= maxPoints) {
                        touchPoints.add(coordinate)
                        invalidate()
                    } else if(closestPoint.second != Float.MAX_VALUE){
                        // Remove the closes point to the touch event
                        touchPoints.remove(closestPoint.first)
                        invalidate()
                    }
                    //listener?.onPointsChanged(touchPoints)
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