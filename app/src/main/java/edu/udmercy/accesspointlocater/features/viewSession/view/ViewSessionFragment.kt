package edu.udmercy.accesspointlocater.features.viewSession.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.snackbar.Snackbar
import edu.udmercy.accesspointlocater.R
import edu.udmercy.accesspointlocater.arch.BaseFragment
import edu.udmercy.accesspointlocater.features.createSession.room.BuildingImage
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import edu.udmercy.accesspointlocater.utils.sp.ISharedPrefsHelper
import edu.udmercy.accesspointlocater.utils.sp.SharedPrefsKeys
import kotlinx.android.synthetic.main.fragment_view_session.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*


class ViewSessionFragment: BaseFragment(R.layout.fragment_view_session), KoinComponent {

    companion object {
        private const val TAG = "ViewSessionFragment"
        const val CREATE_FILE = 4404
    }

    private val viewModel by viewModels<ViewSessionViewModel>()
    private val sharedProvider: ISharedPrefsHelper by inject()

    // These observers are used so that when the data is updated, the UI can automatically be updated
    private val imageObserver =
        Observer { bitmap: BuildingImage? ->
            if(bitmap != null) {
                accessPointImage.setImage(ImageSource.bitmap(bitmap.image))
            }
        }

    private val apLocationObserever =
        Observer { points: MutableList<Pair<Int, PointF>> ->
            Log.d(TAG, "Points: $points")
            accessPointImage.touchPoints = points
            accessPointImage.invalidate()

        }

    @SuppressLint("SetTextI18n")
    private val floorObserver =
        Observer { number: Int ->
            accessViewerFloor.text = "Floor ${number+1}"
        }

    private val adapter by lazy {
        AccessPointInfoRecycler()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val accessPointInfoListObserver =
        Observer { list: List<AccessPointInfo>? ->
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.view_session_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exportZones -> {
                Log.i(TAG, "onOptionsItemSelected: Starting Export...")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Checks if the directory has already been used
                    val uri = sharedProvider.getSharedPrefs(SharedPrefsKeys.DIR_URI)?.toUri()
                    if (uri == null || !uriValid(uri)) {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        startActivityForResult(intent, CREATE_FILE)
                    } else {
                        viewModel.saveFile(uri) {
                            if (it) toast("Exported Zones!") else toast("Failed to export!")
                        }

                    }
                } else {
                    viewModel.saveFile(null) {
                        if (it) toast("Exported Zones!") else toast("Failed to export!")
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
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
                viewModel.saveFile(uri) {
                    if (it) toast("Exported Zones!") else toast("Failed to export!")
                }
            }
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

    private fun toast(msg: String) {
        val safeView = view ?: return
        Snackbar.make(safeView, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUpNavigation()
        // Sets up recycler view and button callbacks when fragment is first created
        val uuid = arguments?.getString("uuid") ?: return
        viewModel.getCurrentSession(uuid)
        accessPointInformationRecycler.adapter = adapter
        val decor = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        accessPointInformationRecycler.addItemDecoration(decor)
        accessViewerPreviousFloorBtn.setOnClickListener { viewModel.moveImage(-1, uuid) }
        accessViewerNextFloorBtn.setOnClickListener { viewModel.moveImage(1, uuid) }
    }

    override fun onNavigationClick() {
        super.onNavigationClick()
        viewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentBitmap.observe(this, imageObserver)
        viewModel.accessPointInfoList.observe(this, accessPointInfoListObserver)
        viewModel.currentFloor.observe(this, floorObserver)
        viewModel.accessPointLocations.observe(this, apLocationObserever)

    }

    override fun onPause() {
        super.onPause()
        viewModel.currentBitmap.removeObserver(imageObserver)
        viewModel.accessPointInfoList.removeObserver(accessPointInfoListObserver)
        viewModel.currentFloor.removeObserver(floorObserver)
        viewModel.currentBitmap.postValue(null)
        viewModel.accessPointLocations.removeObserver(apLocationObserever)
    }

}