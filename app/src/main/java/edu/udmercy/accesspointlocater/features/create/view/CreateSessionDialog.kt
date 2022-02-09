package edu.udmercy.accesspointlocater.features.create.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import edu.udmercy.accesspointlocater.R
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.utils.Event
import kotlinx.android.synthetic.main.dialog_create_session.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import edu.udmercy.accesspointlocater.utils.MathUtils


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

    private val floorHeightObserver =
        Observer { map: MutableMap<Int,Int> ->
            for (item in map){

            }

        }

    private val floorNumberObserver =
        Observer { size: Int ->
            Log.d(TAG, "FloorNumberObserver: Size = $size")
            heightSpinner.isEnabled = true
            floorHeightEditText.isEnabled = true
            val list = mutableListOf<Int>()
            val initMap = mutableMapOf<Int, Int>()
            for (i in 0 until size){
                list.add(i+1)
                initMap[i] = 0
            }
            viewModel.floorToHeight.postValue(initMap)
            val spinner: Spinner = heightSpinner
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }
                // Apply the adapter to the spinner
                spinner.adapter = adapter
                spinner.prompt = "Floor"
            }
        }

    private val presentedBitmapObserver =
        Observer { bitmap: Bitmap? ->
            if(bitmap != null) {
                val params: ViewGroup.LayoutParams = scaleViewContainer.layoutParams
                params.height = LayoutParams.WRAP_CONTENT
                scaleViewContainer.requestLayout()
                scaleImageView.setImage(ImageSource.bitmap(bitmap))
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        heightSpinner.isEnabled = false
        floorHeightEditText.isEnabled = false

        floorHeightEditText.editText?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                TODO()
            }

        })

        doneBtn.setOnClickListener {
            val sessionLabel = sessionEditText.editText?.text.toString()
            val buildingName = buildingEditText.editText?.text.toString()
            val scaleValue = scaleTextView.editText?.text.toString()
            val scaleUnit = scaleSpinner.selectedItem.toString()

            val bitmaps = viewModel.buildingImages

            if(sessionLabel != "" && buildingName != "" && bitmaps.isNotEmpty() && scaleImageView.touchPoints.size == 2 && scaleValue.isNotEmpty()) {
                val point1 = scaleImageView.touchPoints[0]
                val point2 = scaleImageView.touchPoints[1]
                val pixelDistance = MathUtils.euclideanDistance(point1, point2).toDouble()

                viewModel.addNewSession(sessionLabel, buildingName, bitmaps, scaleValue.toDouble(), scaleUnit, pixelDistance)
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
                requireContext().contentResolver.openInputStream(uri)?.readBytes()?.let {
                    viewModel.presentedBitmap.postValue(BitmapFactory.decodeByteArray(it, 0, it.size))
                }
                viewModel.numberOfFloors.postValue(count)
                selectImageBtn.text = requireContext().getText(R.string.imageSaved)
                selectImageBtn.icon = requireContext().getDrawable(R.drawable.ic_baseline_image_24)
            } else {
                val uri = data?.data ?: return
                requireContext().contentResolver.openInputStream(uri)?.readBytes()?.let {
                    viewModel.buildingImages.add(BitmapFactory.decodeByteArray(it,0, it.size))
                    viewModel.presentedBitmap.postValue(BitmapFactory.decodeByteArray(it, 0, it.size))
                    selectImageBtn.text = requireContext().getText(R.string.imageSaved)
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
        viewModel.presentedBitmap.observe(this, presentedBitmapObserver)
        viewModel.numberOfFloors.observe(this, floorNumberObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saved.removeObserver(databaseSavedObserver)
        viewModel.presentedBitmap.removeObserver(presentedBitmapObserver)
        viewModel.numberOfFloors.removeObserver(floorNumberObserver)
    }
}