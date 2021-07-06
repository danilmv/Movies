package com.andriod.movies

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andriod.movies.entity.Movie
import com.andriod.movies.fragment.MovieFragment
import com.andriod.movies.fragment.MovieListFragment

class MainActivity : AppCompatActivity(), MovieListFragment.MovieListContract {
    private var isLandscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, MovieListFragment())
            .commit()
    }

    override fun changeMovie(movie: Movie) {
        if (!isLandscape){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, MovieFragment.newInstance(movie))
                .commit()
        }
    }
}