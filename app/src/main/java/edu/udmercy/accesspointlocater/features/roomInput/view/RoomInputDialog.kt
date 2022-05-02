package edu.udmercy.accesspointlocater.features.roomInput.view


import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import edu.udmercy.accesspointlocater.R
import kotlinx.android.synthetic.main.dialog_room_number.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        roomSave.setOnClickListener {
            val roomNumber = roomAutoComplete?.text.toString()
            sendRoomNumber(roomNumber)
            dismissAllowingStateLoss()
        }
        roomDismissBtn.setOnClickListener {
            //return negative one
            sendRoomNumber("dismiss")
            dismissAllowingStateLoss()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun sendRoomNumber(data: String){
        parentFragmentManager.setFragmentResult("roomNumber", bundleOf("result" to data))
    }
}