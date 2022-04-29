package edu.udmercy.accesspointlocater

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * Automatically used by Room to save bitmap values in classes to ByteArrays and back
 */
class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap):ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray,0, byteArray.size)
    }
 }