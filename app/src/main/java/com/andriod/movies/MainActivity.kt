package com.andriod.movies

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andriod.movies.entity.Movie
import com.andriod.movies.fragment.MovieFragment
import com.andriod.movies.fragment.MovieListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), MovieListFragment.MovieListContract {
    private var isLandscape = false
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        configureBottomView()
        showList(false)
    }

    private fun configureBottomView() {
        bottomNavigationView = findViewById(R.id.bottom_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_bottom_item_list -> {
                    showList(false)
                }
                R.id.menu_bottom_item_favorites -> {
                    showList(true)
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
    }

    private fun showList(showFavorites: Boolean) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, MovieListFragment())
            .commit()
    }

    override fun changeMovie(movie: Movie) {
        if (!isLandscape) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, MovieFragment.newInstance(movie))
                .commit()
        }
    }
}