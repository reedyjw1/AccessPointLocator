package edu.udmercy.accesspointlocater.features.session.sub.create

import androidx.fragment.app.DialogFragment
import edu.udmercy.accesspointlocater.R
import android.view.WindowManager.LayoutParams


class CreateSessionDialog: DialogFragment(R.layout.dialog_create_session) {

    override fun onResume() {
        super.onResume()
        val params: LayoutParams? = dialog?.window?.attributes
        params?.width = LayoutParams.MATCH_PARENT
        params?.height = LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as LayoutParams
    }
}