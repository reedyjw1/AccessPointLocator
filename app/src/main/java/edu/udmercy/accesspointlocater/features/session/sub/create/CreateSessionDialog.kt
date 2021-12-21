package edu.udmercy.accesspointlocater.features.session.sub.create

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import edu.udmercy.accesspointlocater.R
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.udmercy.accesspointlocater.utils.Event
import kotlinx.android.synthetic.main.dialog_create_session.*


class CreateSessionDialog: DialogFragment(R.layout.dialog_create_session) {

    private val viewModel by viewModels<CreateSessionViewModel>()

    companion object {
        private const val TAG = "CreateSessionDialog"
    }

    private val databaseSavedObserver =
        Observer { event: Event<Boolean>? ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                if(it) {
                    dismiss()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doneBtn.setOnClickListener {
            val sessionLabel = sessionEditText.editText?.text.toString()
            val buildingName = buildingEditText.editText?.text.toString()

            if(sessionLabel != "" && buildingName != "") {
                Log.i(TAG, "onViewCreated: sessionLabel=$sessionLabel")
                viewModel.addNewSession(sessionLabel, buildingName)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid Session Name or Building Name!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val params: LayoutParams? = dialog?.window?.attributes
        params?.width = LayoutParams.MATCH_PARENT
        params?.height = LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as LayoutParams

        viewModel.saved.observe(this, databaseSavedObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saved.removeObserver(databaseSavedObserver)
    }
}