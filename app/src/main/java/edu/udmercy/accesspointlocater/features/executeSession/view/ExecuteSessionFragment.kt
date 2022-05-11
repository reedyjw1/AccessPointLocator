package edu.udmercy.accesspointlocater.features.executeSession.view

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.R
import kotlinx.android.synthetic.main.fragment_execute_session.*
import android.graphics.PointF
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.Toast

import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.arch.CircleViewPointListener
import edu.udmercy.accesspointlocater.features.createSession.room.BuildingImage
import edu.udmercy.accesspointlocater.utils.Event

import edu.udmercy.accesspointlocater.features.executeSession.interfaces.CompletedPointTouchedListener
import edu.udmercy.accesspointlocater.features.executeSession.room.WifiScans
import edu.udmercy.accesspointlocater.features.placeAccessPoints.model.TouchPointListener
import edu.udmercy.accesspointlocater.features.executeSession.roomInputDialog.view.RoomInputDialog
import edu.udmercy.accesspointlocater.utils.sp.ISharedPrefsHelper
import edu.udmercy.accesspointlocater.utils.sp.SharedPrefsKeys
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class ExecuteSessionFragment: BaseFragment(R.layout.fragment_execute_session), CircleViewPointListener, KoinComponent {

    private val viewModel by viewModels<ExecuteSessionViewModel>()

    companion object {
        private const val TAG = "ExecuteSessionFragment"
        private const val CREATE_FILE = 5503
        private const val OPEN_FILE = 4403
    }

    private lateinit var wifiManager: WifiManager

    private val sharedProvider: ISharedPrefsHelper by inject()

    // Function that recieves the wifi scan data from the phone/os
    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
            showProgressBar(false)
        }
    }

    // Observer to automatically update the floor plan displayed if the user
    // clicks the change floor button
    private val imageObserver =
        Observer { bitmap: BuildingImage? ->
            Log.i(TAG, "imageObserver: isRecycled:${bitmap?.image?.isRecycled}")
            if(bitmap != null && !bitmap.image.isRecycled) {
                executeImageView.setImage(ImageSource.bitmap(bitmap.image))
            } else {
                // Causes Bug
                //executeImageView.setImage(ImageSource.bitmap(Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888)))
            }
        }

    private val savedPointsObserver =
        Observer { points: List<WifiScans> ->
            if(points.isNotEmpty()) {
                executeImageView.completedPointScans = points
            } else {
                executeImageView.completedPointScans = emptyList()
            }
            executeImageView.touchedPoint = null
            executeImageView.invalidate()
        }

    private val isScanningObserver =
        Observer { scanning: Event<Boolean> ->
            scanning.getContentIfNotHandledOrReturnNull()?.let {
                if(it) {
                    scanningContainer.visibility = View.VISIBLE
                } else {
                    scanningContainer.visibility = View.GONE
                }
            }
        }

    @SuppressLint("SetTextI18n")
    private val floorObserver =
        Observer { number: Int ->
            floorInvalidBtn.text = "Floor ${number+1}"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Sets everything up and the button on click listeners
        showUpNavigation()
        val uuid = arguments?.getString("uuid") ?: return
        executeImageView.listener = this
        executeImageView.addRemoveListener = pointAddRemoveListener
        executeImageView.completedPointTouched = completedPointsListener
        startScanBtn.setOnClickListener {
            startScan()
        }
        previousFloorBtn.setOnClickListener { viewModel.moveImage(-1, uuid) }
        nextFloorBtn.setOnClickListener { viewModel.moveImage(1, uuid) }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.execute_menu, menu)
    }

    // Top right options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.finished -> {
                if(viewModel._isScanning) {
                    toast("Please wait until scanning is complete.")
                } else {
                    // Starts calculating the estimated locations results and then
                    // Navigates to the next fragment when complete
                    showProgressBar(true, "Saving...")
                    viewModel.calculateResults {
                        showProgressBar(false)
                        val bundle = bundleOf("uuid" to arguments?.getString("uuid"))
                        findNavController().navigate(R.id.action_executeSession_to_viewSession, bundle)

                    }
                }
                return true
            }
            R.id.delete -> {

                return true
            }
            R.id.export -> {
                exportSession()
                return true
            }
            R.id.load -> {
                loadSession()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //inflates room input dialog and will return the value inputted inside of data variable
    private fun inflateRoomDialog(){
        var received = false
        childFragmentManager.setFragmentResultListener("result", viewLifecycleOwner) { requestKey, data ->
            if (requestKey == "result" && !received) {
                val item = data.getString("roomNumber")
                item?.let { room ->
                    Log.d(TAG, "inflateRoomDialog: Result: $room")
                    if (room == "dismiss") {
                        //remove last point if dialog was dismissed
                        Log.d(TAG, "inflateRoomDialog: dismissed")
                        executeImageView.touchedPoint = null
                        executeImageView.invalidate()
                    } else {
                        val scanCount = data.getString("scanCount")?.toInt()
                        Log.i(TAG, "inflateRoomDialog: scanCount = $scanCount")
                        viewModel.roomValue = room
                    }
                    received = true
                }
            }
        }
        val uuid = arguments?.getString("uuid") ?: return
        val bundle = bundleOf("uuid" to uuid, "lastRoom" to viewModel.roomValue, "completed" to false)
        val roomDialog = RoomInputDialog()
        roomDialog.arguments = bundle
        roomDialog.show(childFragmentManager, "result")
    }

    // called when a completed scan point is touched
    private fun pointTouchedInflateRoomDialog(scanUUID:String, roomNumber: String){
        var received = false
        childFragmentManager.setFragmentResultListener("result", viewLifecycleOwner) { requestKey, data ->
            if (requestKey == "result" && !received) {
                val item = data.getString("roomNumber")
                item?.let { room ->
                    Log.d(TAG, "inflateRoomDialog: Result: $room")
                    if (room != "dismiss") {
                        viewModel.updateRoomValue(scanUUID, room)
                        viewModel.roomValue = room
                    }
                    received = true
                }
            }
        }
        val uuid = arguments?.getString("uuid") ?: return
        val bundle = bundleOf("uuid" to uuid, "lastRoom" to roomNumber, "completed" to true)
        val roomDialog = RoomInputDialog()
        roomDialog.arguments = bundle
        roomDialog.show(childFragmentManager, "result")
    }



    private val pointAddRemoveListener = object: TouchPointListener {
        override fun onPointAdded(point: PointF) {
            viewModel.currentScanUUID = UUID.randomUUID().toString()
            inflateRoomDialog()
        }

        override fun onPointRemoved(point: PointF) {
            //point remove is handled in onPointChanged
        }
    }

    //used when  a completed scan point is touched
    private val completedPointsListener = object: CompletedPointTouchedListener {
        override fun onPointTouched(scanUUID: String, roomNumber: String) {
            pointTouchedInflateRoomDialog(scanUUID, roomNumber)
        }
    }

    private fun startScan() {
        showProgressBar(true, "Scanning...")
        // Calls the Android APIs to stat the WifiScans
        wifiManager = requireActivity().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        requireActivity().registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            scanFailure()
        }
    }

    private fun scanSuccess() {
        requireActivity().unregisterReceiver(wifiScanReceiver)
        val results = wifiManager.scanResults

        if (results.isEmpty()) {
            toast("Scan Failed!")
        } else {
            toast("Scan Complete!")
        }
        // Filters wifi data by currently connected wifi name and saves to database
        val wifiName = wifiManager.connectionInfo.ssid.toString()
        val filteredResults = results.filter {"\"" +  it.SSID + "\""== wifiName}
        val uuid = arguments?.getString("uuid") ?: return


        Log.i(TAG, "scanSuccess: $filteredResults")
        viewModel.saveResults(filteredResults)
    }

    private fun scanFailure() {
        toast("Scan Failed!")
    }

    // Saves the session data to the user specified directory
    private fun exportSession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Checks if the directory has already been used
            val uri = sharedProvider.getSharedPrefs(SharedPrefsKeys.DIR_URI)?.toUri()
            if (uri == null || !uriValid(uri)) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                startActivityForResult(intent, CREATE_FILE)
            } else {
                viewModel.saveFile(uri)
                toast("Saved Session!")
            }
        } else {
            viewModel.saveFile(null)
            toast("Saved Session!")
        }

    }

    // Loads the JSON file
    private fun loadSession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Scoped Storage
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                .apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }
            startActivityForResult(intent, OPEN_FILE)
        } else {
            // TODO: 2/13/22 - Load File Old way
        }
    }

    // Checks validity of URI that was previously entered by the user at a different opening time
    private fun uriValid(uri: Uri): Boolean {
        requireActivity().contentResolver.persistedUriPermissions.forEach {
            if (it.uri == uri && it.isReadPermission && it.isWritePermission && it.persistedTime <= Date().time) {
                return true
            }
        }
        return false
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            // The result data contains a URI for directory that
            // the user selected.
            resultData?.data?.also { uri ->
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                //this is the uri user has provided us
                sharedProvider.saveToSharedPrefs(SharedPrefsKeys.DIR_URI, uri.toString())
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    flags
                )
                viewModel.saveFile(uri)
                toast("Saved Session!")
            }
        }
        if (requestCode == OPEN_FILE && resultCode == RESULT_OK) {
            resultData?.data?.also { uri ->
                viewModel.loadFile(uri)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentBitmap.observe(this, imageObserver)
        viewModel.floor.observe(this, floorObserver)
        viewModel.savedPoints.observe(this, savedPointsObserver)
        viewModel.isScanning.observe(this, isScanningObserver)
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar(show: Boolean, text: String = "") {
        viewModel._isScanning = show
        viewModel.isScanning.postValue(Event(show))
        progressBarText.text = text
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
        viewModel.floor.removeObserver(floorObserver)
        viewModel.savedPoints.removeObserver(savedPointsObserver)
        viewModel.isScanning.removeObserver(isScanningObserver)
        viewModel.currentBitmap.postValue(null)
    }

    override fun onPointsChanged(currentPoint: PointF?) {
        viewModel.currentPosition = currentPoint

    }

    override fun onNavigationClick() {
        if(!viewModel._isScanning) {
            super.onNavigationClick()
            viewModel.onPause()
        } else {
            toast("Please wait until scanning is complete.")
        }
    }
}