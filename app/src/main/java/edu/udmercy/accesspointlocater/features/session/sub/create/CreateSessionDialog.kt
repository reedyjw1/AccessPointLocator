package edu.udmercy.accesspointlocater.features.session.sub.create

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
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
        private const val SELECT_PICTURE = 200
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
            val bitmap = viewModel.buildingImage

            if(sessionLabel != "" && buildingName != "" && bitmap != null) {
                viewModel.addNewSession(sessionLabel, buildingName, bitmap)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid Session Name, Building Name, or Picture!", Toast.LENGTH_LONG).show()
            }
        }

        selectImageBtn.setOnClickListener { selectImage() }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select the Building Image"), SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            val uri = data?.data ?: return
            requireContext().contentResolver.openInputStream(uri)?.readBytes()?.let {
                viewModel.buildingImage = BitmapFactory.decodeByteArray(it,0, it.size)
                selectImageBtn.text = requireContext().getText(R.string.imageSaved)
                selectImageBtn.icon = requireContext().getDrawable(R.drawable.ic_baseline_image_24)
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