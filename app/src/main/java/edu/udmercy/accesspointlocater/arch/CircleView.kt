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
import edu.udmercy.accesspointlocater.features.executeSession.interfaces.CompletedPointTouchedListener
import edu.udmercy.accesspointlocater.features.executeSession.room.WifiScans
import edu.udmercy.accesspointlocater.features.placeAccessPoints.model.TouchPointListener
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * Class to allow points to be added, or removed, from the image.
 * Used specifically for Wireless Scans (where the user can only click one spot
 * on the map at a time and other points are drawn only when scans are completed)
 */
class CircleView(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {

    companion object {
        private const val TAG = "CircleView"
    }
    private var strokeWidth = 0
    private var radius = 0f
    private val paint = Paint()
    private var density = 0f
    var touchedPoint: PointF? = null
    var completedPointScans: List<WifiScans> = listOf()
    var threshold = 50f
    var listener: CircleViewPointListener? = null
    var addRemoveListener: TouchPointListener? = null
    var completedPointTouched: CompletedPointTouchedListener? = null

    private fun initialise() {
        // Initializes gesture detector for when the user clicks a point on the image
        density = resources.displayMetrics.densityDpi.toFloat()
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
        //radius will always look the same on all devices, 16.8f and 13.125 are values that make the icons look good
        radius = density/16.8f
        val strokeRadius = density/13.125f

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
                    // If the image was clicked, get the SourceCoords (the actual pixel point on the image)
                    // And the ViewCoords, which are the pixel coordinate from the entire phone screen
                    val coordinate: PointF = viewToSourceCoord(e.x, e.y) ?: return true
                    val source = sourceToViewCoord(coordinate) ?: return true

                    // Executes code in block if TouchedPoint is not null
                    touchedPoint?.let { it ->
                        // Gets the PhoneCoordinate and checks if it is
                        // Within a specific threshold, if so, it means a tap was registered very close,
                        // or on top of an existing point. Therefore, the existing point should be removed
                        val touched = sourceToViewCoord(it) ?: return true
                        if (euclideanDistance(source, touched, threshold).first) {
                            touchedPoint = null
                            invalidate()
                        }
                        listener?.onPointsChanged(null)
                        return true
                    }
                    // Use again when Touching Completed Points is implemented
                    Log.i(TAG, "onSingleTapConfirmed: isTouchedPointNull = $touchedPoint")
                    var closestPoint: Triple<PointF, Float, String?> = Triple(PointF(-1f,-1f), Float.MAX_VALUE, null)
                    var closestRoomNumber = ""
                    completedPointScans.forEach {
                        val point = PointF(it.currentLocationX.toFloat(), it.currentLocationY.toFloat())
                        val tempSource = sourceToViewCoord(point) ?: return true
                        val distancePair = euclideanDistance(source, tempSource, threshold)
                        if (distancePair.first && distancePair.second < closestPoint.second) {
                            closestPoint = Triple(point, distancePair.second, it.scanUUID)
                            closestRoomNumber = it.roomNumber
                        }
                    }
                    // Place you just touched is close to an existing point
                    if (closestPoint.second != Float.MAX_VALUE) {
                        closestPoint.third?.let { scanUUID ->
                            completedPointTouched?.onPointTouched(scanUUID, closestRoomNumber)
                            return true
                        }
                    }
                    // Otherwise, set the touched point and enable it to be drawn on the image next frame
                    Log.i(TAG, "onSingleTapConfirmed: touched=$coordinate")
                    touchedPoint = coordinate
                    listener?.onPointsChanged(coordinate)
                    addRemoveListener?.onPointAdded(coordinate)
                    invalidate()

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