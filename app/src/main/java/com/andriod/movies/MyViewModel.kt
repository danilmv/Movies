package com.andriod.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andriod.movies.data.DataProvider
import com.andriod.movies.data.RoomDataProvider
import com.andriod.movies.data.TheMovieDBService
import com.andriod.movies.data.dao.MovieDatabase
import com.andriod.movies.entity.Movie
import com.andriod.movies.fragment.MovieListFragment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyViewModel {
    private val _movies = MutableLiveData<Map<String, Movie>>()
    val movies: LiveData<Map<String, Movie>> = _movies

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/")
            .build()
    }
    private val service: TheMovieDBService by lazy {
        retrofit.create(TheMovieDBService::class.java)
    }

    var groupBy = MutableLiveData(MovieListFragment.Companion.GroupBy.LISTS)

    private val _searchResults = MutableLiveData<Map<String, Movie>>()
    val searchResults: LiveData<Map<String, Movie>> = _searchResults

    var errorMessage = MutableLiveData<String>()

    private val dataProvider: DataProvider by lazy { RoomDataProvider(service, database) }

    lateinit var database: MovieDatabase

    fun initData(database: MovieDatabase) {
        this.database = database

        if (movies.value?.isEmpty() != false) {
            _movies.value = HashMap()

            dataProvider.startService()

            dataProvider.subscribe(DataProvider.Companion.SubscriberType.DATA) {
                _movies.postValue(dataProvider.data)
            }

            dataProvider.subscribe(DataProvider.Companion.SubscriberType.ERROR) {
                errorMessage.value = dataProvider.errorMessage
            }
        }
    }

    fun updateData(movie: Movie) {
        dataProvider.updateData(movie)
    }

    fun startSearching(query: String) {
        dataProvider.subscribe(DataProvider.Companion.SubscriberType.SEARCH) {
            _searchResults.value = dataProvider.searchResultsData
        }
        dataProvider.findMovies(query)
    }

    fun retryConnection() {
        dataProvider.startService()
    }

    fun getMovieDetails(movie: Movie) {
        if (!movie.isDetailsReceived) dataProvider.getMovieDetails(movie)
    }

    fun getMoreData() {
        dataProvider.requestMoreData()
    }
}