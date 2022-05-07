package edu.udmercy.accesspointlocater.features.inputMAC.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.utils.hideSoftInput
import kotlinx.android.synthetic.main.dialog_get_mac.*
import kotlinx.android.synthetic.main.dialog_room_number.*

/**
 * This dialog box appears after the user places a known AP on the image for
 * them to enter its corresponding MAC address
 */
class MACAddressDialog : DialogFragment(R.layout.dialog_get_mac) {
    companion object {
        private const val TAG = "MACAddressDialog"
    }

    private val viewModel by viewModels<MACAddressDialogViewModel>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    private val roomNumberList =
        Observer { list: List<String>->
            arrayAdapter.addAll(list)
            arrayAdapter.notifyDataSetChanged()
        }

    private val lastRoomNumber = Observer { roomNumber: String? ->
        val safeRoomNumber = roomNumber ?: return@Observer
        if (safeRoomNumber != "") {
            roomAutoCompleteKnown.setText(safeRoomNumber)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Sets listeners and saves data when done button is clicked
        setupAutoComplete(view)
        macSaveBtn.setOnClickListener {
            //return what ever is in the text box
            val text = macTextLayout.editText?.text.toString()
            val roomNumber = roomAutoCompleteKnown?.text.toString()
            sendMACAddress(text, roomNumber)
            dismissAllowingStateLoss()
        }

        macDismissBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun sendMACAddress(data: String, roomNumber: String){
        parentFragmentManager.setFragmentResult("macAddress", bundleOf("result" to data, "roomNumber" to roomNumber))
    }

    private fun setupAutoComplete(view: View) {
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_simple_text)
        roomAutoCompleteKnown.threshold = 1
        roomAutoCompleteKnown.setAdapter(arrayAdapter)

        // Hides the Keyboard when clicking the enter button
        roomAutoCompleteKnown.setOnEditorActionListener { _, _, _ ->
            return@setOnEditorActionListener false
        }

        // Clears keyboard when selecting element from drop down
        roomAutoCompleteKnown.setOnItemClickListener { _, _, _, _ ->
            view.hideSoftInput()
        }

        // Shows the drop down when the text box is clicked to enter information
        roomAutoCompleteKnown.setOnFocusChangeListener { _, focus ->
            roomAutoCompleteKnown.showDropDown()
        }
    }


    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        viewModel.roomNumberList.observe(this, roomNumberList)
        viewModel.lastRoomLiveData.observe(this, lastRoomNumber)
    }

    override fun onPause() {
        super.onPause()
        viewModel.roomNumberList.removeObserver(roomNumberList)
        viewModel.lastRoomLiveData.removeObserver(lastRoomNumber)
    }

}