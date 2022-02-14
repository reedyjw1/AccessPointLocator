package edu.udmercy.accesspointlocater.features.create.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.InputType
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
import kotlin.math.floor


class CreateSessionDialog: DialogFragment(R.layout.dialog_create_session) {

    private val viewModel by viewModels<CreateSessionViewModel>()
    var floorHeights = mutableMapOf<Int,Float>()

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

    private val spinnerItemSelectListener = object: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
            //index is the position in the combo box array of item that was selected
            //post floor height for selected floor number
            val height =  floorHeights[index]
            height?.let { h->
                val selection = Pair(index, h)
                viewModel.selectedFloorHeight.postValue(selection)
            }


        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            //do nothing
        }

    }

    private val heightTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, charCount: Int) {
            var floorHeight = newText.toString()
            val selectedFloor = viewModel.selectedFloorHeight.value?.first
            if (selectedFloor != null && floorHeight.isNotEmpty()){
                if(floorHeight.first() == '.')
                {
                    //checks if period is first character, and if it is append a 0 to the beginning
                    floorHeight = "0$floorHeight"
                }

                floorHeights[selectedFloor] = floorHeight.toFloat()
                Log.d(TAG, "FloorHeightsMap: $floorHeights")
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    private val floorChanged =
        Observer { height: Pair<Int,Float> ->
            floorHeightEditText.setText(height.second.toString())
        }


    private val floorNumberObserver =
        Observer { size: Int ->
            Log.d(TAG, "FloorNumberObserver: Size = $size")
            heightSpinner.isEnabled = true
            floorHeightEditText.isEnabled = true
            floorHeightEditText.addTextChangedListener(heightTextWatcher)
            floorHeightEditText.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            for (i in 0 until size){
                floorHeights[i] = 0f
            }
            val spinner: Spinner = heightSpinner
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, floorHeights.keys.map { it+1 }.toMutableList()).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.onItemSelectedListener = spinnerItemSelectListener
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



        doneBtn.setOnClickListener {
            val sessionLabel = sessionEditText.editText?.text.toString()
            val buildingName = buildingEditText.editText?.text.toString()
            val scaleValue = scaleTextView.editText?.text.toString()
            val scaleUnit = scaleSpinner.selectedItem.toString()

            val bitmaps = viewModel.buildingImages

            if(sessionLabel != "" && buildingName != "" && bitmaps.isNotEmpty() && scaleImageView.touchPoints.size == 2 && scaleValue.isNotEmpty() && validateFloorHeights(floorHeights)) {
                val point1 = scaleImageView.touchPoints[0]
                val point2 = scaleImageView.touchPoints[1]
                val pixelDistance = MathUtils.euclideanDistance(point1, point2).toDouble()

                viewModel.addNewSession(sessionLabel, buildingName, bitmaps, scaleValue.toDouble(), scaleUnit, pixelDistance, floorHeights.values.toList())
            } else {
                if(!validateFloorHeights(floorHeights)){
                    Toast.makeText(requireContext(), "Please fill in a height value for each floor.", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(requireContext(), "Please fill in every field.", Toast.LENGTH_LONG).show()
                }


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

    private fun validateFloorHeights(map: MutableMap<Int, Float>): Boolean{
        for(floor in map){
            if(floor.value == 0f){
                return false
            }
        }
        return true
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
                Log.d(TAG, "onActivityResult: Image Count: $count")
                viewModel.numberOfFloors.postValue(count)
                selectImageBtn.text = requireContext().getText(R.string.imageSaved)
                selectImageBtn.icon = requireContext().getDrawable(R.drawable.ic_baseline_image_24)
            } else {
                viewModel.numberOfFloors.postValue(1)
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
        viewModel.selectedFloorHeight.observe(this, floorChanged)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saved.removeObserver(databaseSavedObserver)
        viewModel.presentedBitmap.removeObserver(presentedBitmapObserver)
        viewModel.numberOfFloors.removeObserver(floorNumberObserver)
        viewModel.selectedFloorHeight.removeObserver(floorChanged)
    }
}