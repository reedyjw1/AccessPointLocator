package edu.udmercy.accesspointlocater.features.createSession.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import edu.udmercy.accesspointlocater.R
import android.view.WindowManager.LayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import edu.udmercy.accesspointlocater.utils.Event
import kotlinx.android.synthetic.main.dialog_create_session.*
import android.widget.*


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

    private val toggleCheckbox =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            viewModel.apKnownLocationsCheckbox = isChecked
            Log.d(TAG, "toggleCheckbox: $isChecked")
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apLocationCheckbox.setOnCheckedChangeListener(toggleCheckbox)


        doneBtn.setOnClickListener {
            val sessionLabel = sessionEditText.editText?.text.toString()
            val buildingName = buildingEditText.editText?.text.toString()

            val bitmaps = viewModel.buildingImages

            if(sessionLabel != "" && buildingName != "" && bitmaps.isNotEmpty()) {

                viewModel.addNewSession(sessionLabel, buildingName, bitmaps)
            } else {
                Toast.makeText(requireContext(), "Please fill in every field.", Toast.LENGTH_LONG).show()
            }
        }

        selectImageBtn.setOnClickListener { selectImage() }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select the Building Image"), SELECT_PICTURE)
    }

    /**
     * This function uses the Storage Access Framework API to get the floor plan images from where the user
     * saved them. This API is relatively new and must be used to get access to images
     * Therefore, the old way for older phones must still be implemented
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Log.i(TAG, "onActivityResult: ${data?.data}")
            if(data?.clipData != null) {
                val count = data.clipData?.itemCount ?: return
                for (item in 0 until count) {
                    val uri = data.clipData?.getItemAt(item)?.uri ?: return
                    requireContext().contentResolver.openInputStream(uri)?.readBytes()?.let {
                        viewModel.buildingImages.add(BitmapFactory.decodeByteArray(it,0, it.size))
                    }
                }
                val uri = data.clipData?.getItemAt(0)?.uri ?: return

                Log.d(TAG, "onActivityResult: Image Count: $count")
                selectImageBtn.text = requireContext().getText(R.string.imageSaved)
                selectImageBtn?.isEnabled = false
                selectImageBtn.icon = requireContext().getDrawable(R.drawable.ic_baseline_image_24)
            } else {
                val uri = data?.data ?: return
                requireContext().contentResolver.openInputStream(uri)?.readBytes()?.let {
                    viewModel.buildingImages.add(BitmapFactory.decodeByteArray(it,0, it.size))
                    selectImageBtn.text = requireContext().getText(R.string.imageSaved)
                    selectImageBtn?.isEnabled = false
                    selectImageBtn.icon = requireContext().getDrawable(R.drawable.ic_baseline_image_24)
                }
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