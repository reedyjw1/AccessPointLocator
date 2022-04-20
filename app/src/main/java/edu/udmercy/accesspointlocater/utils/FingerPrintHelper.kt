package edu.udmercy.accesspointlocater.utils

import android.util.Log
import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation

class FingerPrintHelper(private val list: List<WifiScans>, private val scanLimit: Int, private val uuid: String) {

    companion object {
        private const val TAG = "FingerPrintHelper"
    }

    private val uniqueScanLocations = list.distinctBy { Triple(it.currentLocationX, it.currentLocationY, it.floor) }.map { Triple(it.currentLocationX, it.currentLocationY, it.floor)  }
    private val scanLocationsCount = uniqueScanLocations.count()
    private val accessPointList = list.distinctBy { it.ssid }.map { it.ssid }
    private val accessPointCount = accessPointList.count()
    private val scanLocationMap = mutableMapOf<Int, Triple<Double, Double, Int>>()
    private val accessPointMap = mutableMapOf<Int, String>()
    private val rssiFingerPrintTable = MutableList(accessPointCount) { MutableList(scanLocationsCount) { 0 } }

    init {
        // Dictionary of Location Index to Actual Location (left column in paper)
        uniqueScanLocations.forEachIndexed { index, triple ->
            scanLocationMap[index] = triple
        }

        // Dictionary of AP Index to SSID (top row in paper)
        accessPointList.forEachIndexed { index, s ->
            accessPointMap[index] = s
        }
    }

    private fun constructFingerPrintTable() {
        for (i in 0 until accessPointCount) {
           for (j in 0 until scanLocationsCount) {
               val ithAPScans = list.filter { it.ssid == accessPointList[i] }
               val jCount = ithAPScans.filter { it.currentLocationX == uniqueScanLocations[j].first && it.currentLocationY == uniqueScanLocations[j].second && it.floor == uniqueScanLocations[j].third }.count()
               rssiFingerPrintTable[i][j] = jCount
           }
        }
    }

    fun estimateApLocations(): List<APLocation> {
        constructFingerPrintTable()
        val apLocationList = mutableListOf<APLocation>()
        rssiFingerPrintTable.forEachIndexed { index, mutableList ->
            val possibleIndexes = mutableList.indexesOf(scanLimit)
            var xPosition = 0.0
            var yPosition = 0.0
            var count = 0
            val suspectedFloor = list.filter {it.ssid == accessPointMap[index]}.groupingBy { it.floor }.eachCount().maxByOrNull { it.value }?.key
            possibleIndexes.forEach {
                xPosition += scanLocationMap[it]?.first ?: 0.0
                yPosition += scanLocationMap[it]?.second ?: 0.0
                count +=1
            }
            accessPointMap[index]?.let { ssid ->
                if (count != 0) {
                    val apLocation = APLocation(
                        id = 0,
                        uuid = uuid,
                        xCoordinate = xPosition / count,
                        yCoordinate = yPosition / count,
                        zCoordinate = 0.0,
                        floor = suspectedFloor ?: -1,
                        ssid = ssid
                    )
                    apLocationList.add(apLocation)
                }
            }
        }
        return apLocationList
    }

    fun logFingerPrintTable() {
        rssiFingerPrintTable.forEachIndexed { apIndex, apList ->
            apList.forEachIndexed { locIndex, count ->
                Log.i(TAG, "logFingerPrintTable: AP-$apIndex Position-$locIndex = $count")
            }
        }
    }

    private fun <E> Iterable<E>.indexesOf(e: E)
            = mapIndexedNotNull{ index, elem -> index.takeIf{ elem == e } }
}