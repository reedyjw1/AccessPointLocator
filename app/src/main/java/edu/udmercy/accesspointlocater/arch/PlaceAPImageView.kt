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
import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import edu.udmercy.accesspointlocater.features.placeAccessPoints.model.TouchPointListener
import kotlinx.android.synthetic.main.fragment_execute_session.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This ImageView is specifically used to add the known access points to an image
 */
class PlaceAPImageView(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    companion object {
        private const val TAG = "PlaceAPImageView"
    }
    private var strokeWidth = 0
    private val paint = Paint()
    var touchPoints: MutableList<PointF> = mutableListOf()
    var maxPoints = 1
    var threshold = 50f
    var listener: TouchPointListener? = null

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        setMinimumDpi(50)
        strokeWidth = (density / 60f).toInt()
        // Registers on click listener for notifying clicks on image
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


        // Previously clicked points
        touchPoints.forEach {
            sourceToViewCoord(it)?.let { source ->
                paint.isAntiAlias = true
                paint.style = Paint.Style.FILL_AND_STROKE
                paint.strokeCap = Cap.ROUND
                paint.color = Color.BLACK
                canvas.drawCircle(source.x, source.y, strokeRadius, paint)
                paint.strokeCap = Cap.ROUND
                paint.color = Color.CYAN
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
                    // If the image was clicked, get the SourceCoords (the actual pixel point on the image)
                    // And the ViewCoords, which are the pixel coordinate from the entire phone screen
                    val coordinate: PointF = viewToSourceCoord(e.x, e.y) ?: return true
                    val source = sourceToViewCoord(coordinate) ?: return true

                    // Creates a closest point value adn inits a MAX distance value
                    var closestPoint: Pair<PointF, Float> = Pair(PointF(-1f,-1f), Float.MAX_VALUE)
                    touchPoints.forEach {
                        // Checks how close each point already on the map is to the touched point
                        val tempSource = sourceToViewCoord(it) ?: return true
                        val distancePair = euclideanDistance(source, tempSource, threshold)
                        // IF it is withing a threshold and closer than a previously checked point, update closest point variable
                        if (distancePair.first && distancePair.second < closestPoint.second) {
                            closestPoint = Pair(it, distancePair.second)
                        }
                    }
                    // If there is no close point, add the new point
                    if (closestPoint.second == Float.MAX_VALUE) {
                        //touchPoints.add(coordinate)
                        listener?.onPointAdded(coordinate)
                        invalidate()
                    } else if(closestPoint.second != Float.MAX_VALUE){
                        // Remove the closes point to the touch event
                        touchPoints.remove(closestPoint.first)
                        listener?.onPointRemoved(closestPoint.first)
                        invalidate()
                    }
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