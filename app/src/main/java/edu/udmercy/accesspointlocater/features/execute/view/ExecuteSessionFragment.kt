package edu.udmercy.accesspointlocater.features.execute.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.davemorrissey.labs.subscaleview.ImageSource
import edu.udmercy.accesspointlocater.R
import kotlinx.android.synthetic.main.fragment_execute_session.*
import android.graphics.PointF
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.*

import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.arch.CircleViewPointListener
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.utils.Event

import com.google.android.gms.location.*
import com.google.gson.Gson
import edu.udmercy.accesspointlocater.features.execute.model.SessionExport
import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import kotlinx.android.synthetic.main.dialog_create_session.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

class ExecuteSessionFragment: BaseFragment(R.layout.fragment_execute_session), CircleViewPointListener {

    private val viewModel by viewModels<ExecuteSessionViewModel>()

    companion object {
        private const val TAG = "ExecuteSessionFragment"
        private const val CREATE_FILE = 5503
    }

    private lateinit var wifiManager: WifiManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

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

    private val imageObserver =
        Observer { bitmap: BuildingImage? ->
            if(bitmap != null) {
                executeImageView.setImage(ImageSource.bitmap(bitmap.image))
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupCallbacks()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUpNavigation()
        val uuid = arguments?.getString("uuid") ?: return
        executeImageView.listener = this
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.finished -> {
                if(viewModel._isScanning) {
                    toast("Please wait until scanning is complete.")
                } else {
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startScan() {
        showProgressBar(true, "Scanning...")
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

        val wifiName = wifiManager.connectionInfo.ssid.toString()
        val filteredResults = results.filter {"\"" +  it.SSID + "\""== wifiName}
        val uuid = arguments?.getString("uuid") ?: return
        Log.i(TAG, "scanSuccess: $filteredResults")
        viewModel.saveResults(filteredResults, uuid, viewModel.altitude)
    }

    private fun setupCallbacks() {
        locationRequest = LocationRequest().apply {
            // Sets the desired interval for
            // active location updates.
            // This interval is inexact.
            interval = TimeUnit.SECONDS.toMillis(5)

            // Sets the fastest rate for active location updates.
            // This interval is exact, and your application will never
            // receive updates more frequently than this value
            fastestInterval = TimeUnit.SECONDS.toMillis(5)

            // Sets the maximum time when batched location
            // updates are delivered. Updates may be
            // delivered sooner than this interval
            maxWaitTime = TimeUnit.SECONDS.toMillis(10)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.i(TAG, "onLocationResult: got Location")
                val locations = p0.locations
                locations.sortBy { it.time }
                viewModel.altitude = locations.lastOrNull()?.altitude ?: 0.0

            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Looper.myLooper()?.let {
            Log.i(TAG, "onReceive: getting location")
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                it
            )
        }
    }

    private fun scanFailure() {
        toast("Scan Failed!")
    }

    private fun exportSession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Scoped Storage
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {

            }
            startActivityForResult(intent, CREATE_FILE)
        } else {
            viewModel.saveFile(null)
        }

    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            // The result data contains a URI for directory that
            // the user selected.
            resultData?.data?.also { uri ->
                viewModel.saveFile(uri)
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
        val safeView = view ?: return
        Snackbar.make(safeView, msg, Snackbar.LENGTH_SHORT).show()
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

        val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Location Callback removed.")
            } else {
                Log.d(TAG, "Failed to remove Location Callback.")
            }
        }
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