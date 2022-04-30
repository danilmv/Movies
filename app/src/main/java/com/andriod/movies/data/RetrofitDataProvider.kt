package com.andriod.movies.data

import com.andriod.movies.entity.*
import com.andriod.movies.statusbar.StatusManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class RetrofitDataProvider(private val service: TheMovieDBService) : DataProvider() {
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
            Companion.StatusGroup.WEB_DATA_REQUESTED.id)

        service.getMovieList(url, page).enqueue(
            object : Callback<MovieList> {
                override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                    if (response.isSuccessful) {
                        dataHandler.post {
                            response.body()?.results?.forEach { movie ->
                                addMovieToData(movie, listName)
                            }

                            updateGenres()
                            notifySubscribers(Companion.SubscriberType.DATA)
                            StatusManager.close(statusId, "$listName data received")
                        }
                    } else {
                        StatusManager.close(statusId, "Failed to load data for $listName")
                        exceptionCallback.invoke("Failed to load data for $listName")
                    }
                }

                override fun onFailure(call: Call<MovieList>, t: Throwable) {
                    StatusManager.close(statusId,
                        "Failed to load data for $listName: ${t.message}")
                    t.message?.let { exceptionCallback.invoke(it) }
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
        data[movie.id]?.let { dataChanged(it) }
    }

    protected open fun dataChanged(movie: Movie) {}

    private fun requestGenres() {
        if (isGenresLoaded) return


        val statusId = StatusManager.create("waiting for: genres data requested")
        service.getGenres().enqueue(
            object : Callback<Genres> {
                override fun onResponse(call: Call<Genres>, response: Response<Genres>) {
                    dataHandler.post {
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
                }

                override fun onFailure(call: Call<Genres>, t: Throwable) {
                    StatusManager.close(statusId, "Failed to receive Genres")
                }
            })
    }

    private fun requestTrending(
        listName: String,
        exceptionCallback: (String) -> Unit = {},
        page: Int = 1,
    ) {
        val statusId = StatusManager.create("waiting for: $listName data requested, page = $page",
            Companion.StatusGroup.WEB_DATA_REQUESTED.id)
        service.getTrending(page).enqueue(
            object : Callback<Trending> {
                override fun onResponse(call: Call<Trending>, response: Response<Trending>) {
                    if (response.isSuccessful) {
                        dataHandler.post {
                            response.body()?.results?.forEach { movie ->
                                addMovieToData(movie, listName)
                            }

                            updateGenres()
                            notifySubscribers(Companion.SubscriberType.DATA)
                            StatusManager.close(statusId, "$listName data received")
                        }
                    } else {
                        StatusManager.close(statusId, "Failed to load data for $listName")
                        exceptionCallback.invoke("Failed to load data for $listName")
                    }
                }

                override fun onFailure(call: Call<Trending>, t: Throwable) {
                    StatusManager.close(statusId,
                        "Failed to load data for $listName: ${t.message}")
                    t.message?.let { exceptionCallback.invoke(it) }
                }

            }
        )
    }

    override fun getMovieDetails(movie: Movie) {
        super.getMovieDetails(movie)

        requestMovieDetails(listOf(movie))
    }

    override fun getMovieDetails(movies: List<Movie>) {
        requestMovieDetails(movies)
    }

    private fun requestMovieDetails(movies: List<Movie>) {

        movies.forEach { movie ->
            if (!movie.isDetailsReceived) {
                val statusId =
                    StatusManager.create("waiting for: details for ${movie.title} requested",
                        Companion.StatusGroup.WEB_DETAILS_REQUESTED.id)

                service.getDetails(movie._type, movie.id).enqueue(object : Callback<Movie> {
                    override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                        if (response.isSuccessful) {
                            dataHandler.post {
                                response.body()?.let { responseMovie ->
                                    responseMovie.isDetailsReceived = true
                                    updateData(responseMovie)
                                }
                                StatusManager.close(statusId, "details for ${movie.title} received")
                            }
                        } else {
                            StatusManager.close(statusId,
                                "Failed to receive details for ${movie.title}")
                        }
                    }

                    override fun onFailure(call: Call<Movie>, t: Throwable) {
                        StatusManager.close(statusId,
                            "Failed to receive details for ${movie.title}: ${t.message}")
                    }

                })
            }
        }
    }

    override fun getMovieVideos(movie: Movie) {
        super.getMovieVideos(movie)

        requestMovieVideos(movie)
    }

    private fun requestMovieVideos(movie: Movie) {
        val statusId =
            StatusManager.create("waiting for: video links for ${movie.title} requested",
                Companion.StatusGroup.WEB_DETAILS_REQUESTED.id)

        service.getVideos(movie.id).enqueue(object : Callback<MovieVideos> {
            override fun onResponse(call: Call<MovieVideos>, response: Response<MovieVideos>) {
                if (response.isSuccessful) {
                    response.body()?.let { videos ->
                        videos.results.forEach { movie.videos[it.key] = it }
                    }
                    updateData(movie)
                    StatusManager.close(statusId, "videos for ${movie.title} received")
                } else {
                    StatusManager.close(statusId,
                        "Failed to receive videos for ${movie.title}")
                }
            }

            override fun onFailure(call: Call<MovieVideos>, t: Throwable) {
                StatusManager.close(statusId,
                    "Failed to receive videos for ${movie.title}: ${t.message}")
            }
        })
    }

    override fun updateData(movie: Movie) {
        super.updateData(movie)
        dataChanged(movie)
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
                    StatusManager.close(statusId, "search results received")
                }
            }

            override fun onFailure(call: Call<SearchResults>, t: Throwable) {
                StatusManager.close(statusId, "Failed to receive search results")
            }
        })
    }

    override fun requestMoreData() {
        requestMovieList("top_rated",
            listName = "top rated",
            page = moreDataPage++)
    }
}