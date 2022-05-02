package edu.udmercy.accesspointlocater.features.roomInput.view


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.utils.hideSoftInput
import kotlinx.android.synthetic.main.dialog_room_number.*


class RoomInputDialog: DialogFragment(R.layout.dialog_room_number) {

    companion object {
        private const val TAG = "RoomInputDialog"
    }

    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val viewModel by viewModels<RoomInputViewModel>()

    private val roomNumberList =
        Observer { list: List<String>->
            arrayAdapter.addAll(list)
            arrayAdapter.notifyDataSetChanged()
        }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        viewModel.roomNumberList.observe(this, roomNumberList)
    }

    override fun onPause() {
        super.onPause()
        viewModel.roomNumberList.removeObserver(roomNumberList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAutoComplete(view)
        roomSave.setOnClickListener {
            viewModel.enteredRoomNumber = true
            val roomNumber = roomAutoComplete?.text.toString()
            sendRoomNumber(roomNumber)
            dismissAllowingStateLoss()
        }
        roomDismissBtn.setOnClickListener {
            //return negative one
            dismissAllowingStateLoss()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAutoComplete(view: View) {
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_simple_text)
        roomAutoComplete.threshold = 1
        roomAutoComplete.setAdapter(arrayAdapter)

        // Hides the Keyboard when clikcing the enter button
        roomAutoComplete.setOnEditorActionListener { textView, i, keyEvent ->
            return@setOnEditorActionListener false
        }

        // Clears keyboard when selecting element from drop down
        roomAutoComplete.setOnItemClickListener { _, _, _, _ ->
            view.hideSoftInput()
        }

        // Shows the drop down when the text box is clicked to enter information
        roomAutoComplete.setOnFocusChangeListener { _,_ ->
            roomAutoComplete.showDropDown()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(!viewModel.enteredRoomNumber) {
            sendRoomNumber("dismiss")
        }
    }

    private fun sendRoomNumber(data: String){
        parentFragmentManager.setFragmentResult("roomNumber", bundleOf("result" to data))
    }
}