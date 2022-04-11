package edu.udmercy.accesspointlocater.features.inputMAC.view

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import edu.udmercy.accesspointlocater.R
import kotlinx.android.synthetic.main.dialog_get_mac.*

class MACAddressDialog : DialogFragment(R.layout.dialog_get_mac) {
    companion object {
        private const val TAG = "MACAddressDialog"
    }

    private val viewModel by viewModels<MACAddressDialogViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        macSaveBtn.setOnClickListener {
            //return what ever is in the text box
            val text = macTextLayout.editText?.text.toString()
            sendMACAddress(text)
            dismissAllowingStateLoss()
        }

        macDismissBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun sendMACAddress(data: String){
        parentFragmentManager.setFragmentResult("macAddress", bundleOf("result" to data))
    }



    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

}