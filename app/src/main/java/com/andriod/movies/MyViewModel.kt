package com.andriod.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriod.movies.data.DataProvider
import com.andriod.movies.data.RetrofitDataProvider
import com.andriod.movies.data.TheMovieDBService
import com.andriod.movies.entity.Movie
import com.andriod.movies.fragment.MovieListFragment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyViewModel {
    private val _movies = MutableLiveData<Map<String, Movie>>()
    val movies: LiveData<Map<String, Movie>> = _movies

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.themoviedb.org/")
        .build()

    private val service: TheMovieDBService = retrofit.create(TheMovieDBService::class.java)

    private val dummy: DataProvider = RetrofitDataProvider(service)

    var groupBy = MutableLiveData(MovieListFragment.Companion.GroupBy.LISTS)

    private val _searchResults = MutableLiveData<Map<String, Movie>>()
    val searchResults: LiveData<Map<String, Movie>> = _searchResults

    var errorMessage = MutableLiveData<String>()

    fun initData() {
        if (movies.value?.isEmpty() != false) {
            _movies.value = HashMap()

            dummy.subscribe(DataProvider.Companion.SubscriberType.DATA) {
                _movies.postValue(dummy.data)
            }

            dummy.subscribe(DataProvider.Companion.SubscriberType.ERROR) {
                errorMessage.value = dummy.errorMessage
            }
        }
    }

    fun updateData(movie: Movie) {
        dummy.updateData(movie)
    }

    fun startSearching(query: String) {
        dummy.subscribe(DataProvider.Companion.SubscriberType.SEARCH) {
            _searchResults.value = dummy.searchResultsData
        }
        dummy.findMovies(query)
    }

    fun retryConnection() {
        dummy.startService()
    }

    fun getMovieDetails(movie: Movie) {
        if (!movie.isDetailsReceived) dummy.getMovieDetails(movie)
    }

    fun getMoreData() {
        dummy.requestMoreData()
    }
}