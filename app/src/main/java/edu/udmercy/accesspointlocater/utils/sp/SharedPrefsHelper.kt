package edu.udmercy.accesspointlocater.utils.sp

import android.content.Context

/**
 * Class is used to save key value pairs to shared data
 * This is specifically used to save the inputted URI (location to export JSON data to)
 * URI is the same until permissions expire, so it should be saved to prevent the user from having to
 * choose a location to save data everytime they export
 */
class SharedPrefsHelper(val context: Context): ISharedPrefsHelper {

    companion object {
        private const val PREFS = "image_labeler_prefs"
    }

    private val sharedPrefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override fun getSharedPrefs(key: SharedPrefsKeys): String? {
        return sharedPrefs.getString(key.value, "")
    }

    override fun saveToSharedPrefs(key: SharedPrefsKeys, value: String) {
        with(sharedPrefs.edit()) {
            putString(key.value, value)
            commit()
        }
    }
}

enum class SharedPrefsKeys(val value: String) {
    DIR_URI("pwd_uri")
}

interface ISharedPrefsHelper {
    fun getSharedPrefs(key: SharedPrefsKeys): String?
    fun saveToSharedPrefs(key: SharedPrefsKeys, value: String)
}