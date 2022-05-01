package edu.udmercy.accesspointlocater.features.roomInput.view


import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import edu.udmercy.accesspointlocater.R

class RoomInputDialog: DialogFragment(R.layout.dialog_room_number) {

    companion object {
        private const val TAG = "RoomInputDialog"
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

}