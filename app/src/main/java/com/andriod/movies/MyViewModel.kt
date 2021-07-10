package com.andriod.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriod.movies.data.DummyDataProvider
import com.andriod.movies.entity.Movie
import com.andriod.movies.fragment.MovieListFragment

object MyViewModel {
    private val _movies = MutableLiveData<Map<String, Movie>>()
    val movies: LiveData<Map<String, Movie>> = _movies
    private val dummy = DummyDataProvider()

    var groupBy = MutableLiveData(MovieListFragment.Companion.GroupBy.TYPE)

    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    fun initData() {
        if (movies.value?.isEmpty() != false) {
            _movies.value = HashMap()

            dummy.subscribe {
                _movies.postValue(dummy.data)
            }
        }
    }

    fun updateData(movie: Movie) {
        dummy.updateData(movie)
    }

    fun startSearching(query: String) {
        dummy.subscribe {
            _searchResults.postValue(dummy.searchResultsData)
        }
        dummy.findMovies(query)
    }
}