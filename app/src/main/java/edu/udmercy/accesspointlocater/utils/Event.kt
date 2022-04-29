package edu.udmercy.accesspointlocater.utils

/** https://medium.com/android-news/sending-events-from-viewmodel-to-activities-fragments-the-right-way-26bb68502b24 */
/**
 * Checks if in event has been handled, this is used for Toast messages with live data (since the message will
 * show up when the observer is assigned, this checks to make sure it should actually be used)
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandledOrReturnNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}