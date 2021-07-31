package com.andriod.movies.data

import com.andriod.movies.entity.*
import com.andriod.movies.statusbar.StatusManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class RetrofitDataProvider(private val service: TheMovieDBService) : DataProvider() {
    private val dataRequestStatusGroup = 1
    private var moreDataPage = 2

    override fun startService() {
        errorMessage = ""

        requestTrending("trending", { message: String -> errorMessage = message })

        requestMovieList("latest", "latest")
        requestMovieList("now_playing", "now playing")
        requestMovieList("popular", "popular")
        requestMovieList("top_rated", "top rated")
        requestMovieList("upcoming", "upcoming")

        requestGenres()
    }

    private fun requestMovieList(
        url: String,
        listName: String,
        exceptionCallback: (String) -> Unit = {},
        page: Int = 1,
    ) {
        val statusId = StatusManager.create("waiting for: $listName data requested, page = $page",
            dataRequestStatusGroup)

        service.getMovieList(url, page).enqueue(
            object : Callback<MovieList> {
                override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                    if (response.isSuccessful) {
                        response.body()?.results?.forEach { movie ->
                            addMovieToData(movie, listName)
                        }

                        updateGenres()
                        notifySubscribers(Companion.SubscriberType.DATA)
                        StatusManager.change(statusId, "$listName data received")
                    } else {
                        exceptionCallback.invoke("Failed to load data for $listName")
                    }

                    StatusManager.close(statusId)
                }

                override fun onFailure(call: Call<MovieList>, t: Throwable) {
                    StatusManager.change(statusId,
                        "Failed to load data for $listName: ${t.message}")
                    t.message?.let { exceptionCallback.invoke(it) }

                    StatusManager.close(statusId)
                }

            }
        )
    }

    protected fun addMovieToData(movie: Movie, listName: String = "") {
        if (data.containsKey(movie.id)) {
            data[movie.id]?.addList(listName)
        } else {
            data[movie.id] = movie.apply {
                _type = Movie.Companion.TYPE.TYPE_MOVIE.value
                addList(listName)
            }
        }
    }

    private fun requestGenres() {
        if (isGenresLoaded) return


        val statusId = StatusManager.create("waiting for: genres data requested")
        service.getGenres().enqueue(
            object : Callback<Genres> {
                override fun onResponse(call: Call<Genres>, response: Response<Genres>) {
                    for (genre in response.body()?.genres!!) {
                        genres[genre.id] = genre
                    }
                    if (genres.isNotEmpty()) {
                        isGenresLoaded = true
                        updateGenres()
                        notifySubscribers(Companion.SubscriberType.GENRES)
                    }
                    StatusManager.close(statusId)
                }

                override fun onFailure(call: Call<Genres>, t: Throwable) {
                    StatusManager.close(statusId)
                }
            })
    }

    private fun requestTrending(
        listName: String,
        exceptionCallback: (String) -> Unit = {},
        page: Int = 1,
    ) {
        val statusId = StatusManager.create("waiting for: $listName data requested, page = $page",
            dataRequestStatusGroup)
        service.getTrending(page).enqueue(
            object : Callback<Trending> {
                override fun onResponse(call: Call<Trending>, response: Response<Trending>) {
                    if (response.isSuccessful) {
                        response.body()?.results?.forEach { movie ->
                            addMovieToData(movie, listName)
                        }

                        updateGenres()
                        notifySubscribers(Companion.SubscriberType.DATA)
                        StatusManager.change(statusId, "$listName data received")
                    } else {
                        exceptionCallback.invoke("Failed to load data for $listName")
                    }

                    StatusManager.close(statusId)
                }

                override fun onFailure(call: Call<Trending>, t: Throwable) {
                    StatusManager.change(statusId,
                        "Failed to load data for $listName: ${t.message}")
                    t.message?.let { exceptionCallback.invoke(it) }

                    StatusManager.close(statusId)
                }

            }
        )
    }

    override fun getMovieDetails(movie: Movie) {
        super.getMovieDetails(movie)

        requestMovieDetails(movie)
    }

    private fun requestMovieDetails(movie: Movie) {

        val statusId = StatusManager.create("waiting for: details for ${movie.title} requested")

        service.getDetails(movie._type, movie.id).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful) {
                    response.body()?.let { movie ->
                        updateData(movie)
                    }
                    StatusManager.close(statusId)
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                StatusManager.close(statusId)
            }

        })
    }


    override fun findMovies(query: String) {
        val statusId = StatusManager.create("waiting for: searching results")

        searchResultsData.clear()

        service.getSearching(query).enqueue(object : Callback<SearchResults> {
            override fun onResponse(call: Call<SearchResults>, response: Response<SearchResults>) {
                if (response.isSuccessful) {
                    response.body()?.results?.forEach { movie ->
                        movie._type = Movie.Companion.TYPE.TYPE_MOVIE.value
                        searchResultsData[movie.id] = data[movie.id] ?: movie
                    }
                    updateGenres(searchResultsData)
                    notifySubscribers(Companion.SubscriberType.SEARCH)
                    StatusManager.close(statusId)
                }
            }

            override fun onFailure(call: Call<SearchResults>, t: Throwable) {
                StatusManager.close(statusId)
            }
        })
    }

    override fun requestMoreData() {
        requestMovieList("top_rated",
            listName = "top rated",
            page = moreDataPage++)
    }
}