package com.andriod.movies

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.andriod.movies.entity.Movie
import com.andriod.movies.fragment.MovieFragment
import com.andriod.movies.fragment.MovieListFragment
import com.andriod.movies.fragment.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MovieListFragment.MovieListContract, MovieFragment.MovieContract {
    private var isLandscape = false
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        configureBottomView()
        showList(MovieListFragment.Companion.ShowMode.LIST)
    }

    private fun configureBottomView() {
        bottomNavigationView = findViewById(R.id.bottom_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
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
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
            true
        }
    }

    private fun showSettings() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, SettingsFragment())
            .commit()
    }

    private fun showList(showMode: MovieListFragment.Companion.ShowMode) {
        val fragment = MovieListFragment()
        fragment.showMode = showMode
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }

    override fun changeMovie(movie: Movie) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, MovieFragment.newInstance(movie))
            .commit()
    }

    override fun onMovieChanged(movie: Movie) {
        MyViewModel.updateData(movie)
    }

    override fun setTitle(title: String) {
        this.title = "${getString(R.string.app_name)}: $title"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = menu.findItem(R.id.menu_main_item_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                startSearching(query)
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
        bottomNavigationView.menu.findItem(bottomItemId)?.isChecked = true
    }

    private fun startSearching(query: String){
        MyViewModel.startSearching(query)
        showList(MovieListFragment.Companion.ShowMode.SEARCHING)
    }
}