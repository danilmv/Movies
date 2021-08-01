package com.andriod.movies

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.andriod.movies.databinding.ActivityMainBinding
import com.andriod.movies.entity.Movie
import com.andriod.movies.fragment.MovieFragment
import com.andriod.movies.fragment.MovieListFragment
import com.andriod.movies.fragment.SettingsFragment
import com.andriod.movies.services.MovieDataDownloadService
import com.andriod.movies.statusbar.StatusManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MovieListFragment.MovieListContract,
    SettingsFragment.SettingsContract,
    MovieFragment.MovieContract {
    private var isLandscape = false
    private lateinit var bottomNavigationView: BottomNavigationView
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    private val listFragment = MovieListFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        configureBottomView()
        configureStatusBar()
    }

    private fun configureStatusBar() {
        binding.statusBar.setStatuses(StatusManager.statuses)
        StatusManager.statuses.observe(this) {
            binding.statusBar.isVisible = it.values.isNotEmpty()
        }
    }

    override fun onStart() {
        super.onStart()
        listenForErrors()
        showList(MovieListFragment.Companion.ShowMode.LIST)
    }

    private fun listenForErrors() {
        MyViewModel.errorMessage.observe(this, {
            if (it.isNotBlank()) {
                MyViewModel.errorMessage.value = ""
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error_message_title))
                    .setMessage(it)
                    .setPositiveButton(getString(R.string.retry_message)) { _, _ -> MyViewModel.retryConnection() }
                    .show()
            }
        })
    }

    private fun configureBottomView() {
        bottomNavigationView = findViewById(R.id.bottom_view)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_bottom_item_list -> {
                    showList(MovieListFragment.Companion.ShowMode.LIST)
                }
                R.id.menu_bottom_item_favorites -> {
                    showList(MovieListFragment.Companion.ShowMode.FAVORITES)
                }
                R.id.menu_bottom_item_settings -> {
                    showSettings()
                }
                R.id.menu_bottom_item_search_results -> {
                    showList(MovieListFragment.Companion.ShowMode.SEARCHING)
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
            true
        }
    }

    private fun showSettings() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, settingsFragment)
            .commit()

        setTitle(getString(R.string.title_settings))
    }

    private fun showList(showMode: MovieListFragment.Companion.ShowMode) {
        listFragment.showMode = showMode
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, listFragment)
            .commit()
    }

    override fun changeMovie(movie: Movie) {
        MyViewModel.getMovieDetails(movie)
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, MovieFragment.newInstance(movie))
            .addToBackStack(null)
            .commit()

        setTitle(movie.title)
    }

    override fun onMovieChanged(movie: Movie) {
        MyViewModel.updateData(movie)
    }

    override fun setTitle(title: String) {
        this.title = "${getString(R.string.app_name)}: $title"
    }

    override fun onModeChange(mode: MovieListFragment.Companion.ShowMode) {
        setBottomView(when (mode) {
            MovieListFragment.Companion.ShowMode.LIST -> R.id.menu_bottom_item_list
            MovieListFragment.Companion.ShowMode.FAVORITES -> R.id.menu_bottom_item_favorites
            MovieListFragment.Companion.ShowMode.SEARCHING -> R.id.menu_bottom_item_search_results
        })
    }

    override fun onMassDetailsRequested(movies: List<Movie>) {
        MyViewModel.getMassMovieDetails(movies)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu.findItem(R.id.menu_main_item_search)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                startSearching(query)
                menuItem.collapseActionView()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return true
    }

    override fun onResume() {
        super.onResume()
        setBottomView(R.id.menu_bottom_item_list)
    }

    private fun setBottomView(bottomItemId: Int) {
        bottomNavigationView.menu.findItem(bottomItemId)?.apply {
            isChecked = true
            isVisible = true
        }
    }

    private fun startSearching(query: String) {
        MyViewModel.startSearching(query)
        showList(MovieListFragment.Companion.ShowMode.SEARCHING)
        hideKeyboard()
    }

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    companion object {
        const val TAG = "@@MainActivity"
    }

    override fun onStartService() {
        startService(Intent(this, MovieDataDownloadService::class.java))
    }

    override fun onStartLoading() {
        MyViewModel.retryConnection()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        listFragment.setTitle()
    }
}