package edu.udmercy.accesspointlocater.arch

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import edu.udmercy.accesspointlocater.R

/**
 * Base Fragment that sets up the use for the top navigation bar in every subsequent fragment.
 * Otherwise, this code would be repeated for every fragment
 */
abstract class BaseFragment(layoutResId: Int): Fragment(layoutResId) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        // With the correct context, set the navController and actionBar
        with (requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)

            val navController = NavHostFragment.findNavController(this@BaseFragment)

            setupActionBar(this, toolbar, navController)
        }
    }

    open fun setupActionBar(
        activity: AppCompatActivity,
        toolbar: Toolbar,
        navController: NavController
    ) {
        NavigationUI.setupActionBarWithNavController(activity, navController)
    }

    open fun showUpNavigation() {
        // Presents the Navigation Bars Icons
        view?.findViewById<Toolbar>(R.id.toolbar)?.let {
            it.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            it.setNavigationOnClickListener { onNavigationClick() }
        }
    }

    open fun removeNavigation() {
        view?.findViewById<Toolbar>(R.id.toolbar)?.let {
            it.setNavigationOnClickListener {  }
            it.navigationIcon = null
        }
    }

    open fun onNavigationClick() {
        // Goes to the previous fragment in the backstack if the back button in the
        // NavBar was clicked
        findNavController().navigateUp()
    }
}