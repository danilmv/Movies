package com.andriod.movies.data

import com.andriod.movies.entity.Movie
import com.andriod.movies.statusbar.StatusManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitDataProvider : DataProvider() {
    private val dataRequestStatusGroup = 1

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private var service: TheMovieDBService = retrofit.create(TheMovieDBService::class.java)


    override fun startService() {
        errorMessage = ""

//        requestData("trending/movie/week",
//            "trending",
//            { message: String -> errorMessage = message },
//            { errorMessage = "no data received" }
//        )

        requestData("latest", "latest")
        requestData("now_playing", "now playing")
        requestData("popular", "popular")
        requestData("top_rated", "top rated")
        requestData("upcoming", "upcoming")

//        requestGenres()
    }

    private fun requestData(
        url: String,
        listName: String,
        exceptionCallback: (String) -> Unit = {},
        data: MutableMap<String, Movie> = this.data,
        page: Int = 1,
    ) {
        val statusId =
            StatusManager.create("waiting for: $url data requested, page = $page",
                dataRequestStatusGroup)
        service.getMovie(listName).enqueue(
            object : Callback<List<Movie>> {
                override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                    if (response.isSuccessful) {
                        response.body()?.forEach { movie ->
                            if (data.containsKey(movie.id)) {
                                data[movie.id]?.addList(listName)
                            } else {
                                data[movie.id] = movie.apply {
                                    _type = Movie.Companion.TYPE.TYPE_MOVIE.value
                                    addList(listName)
                                }
                            }
                        }

                        updateGenres()
                        notifySubscribers(Companion.SubscriberType.DATA)
                        StatusManager.change(statusId, "$listName data received")
                    }
                }

                override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                    StatusManager.change(statusId, "Failed to load data for $listName")
                    t.message?.let { exceptionCallback.invoke(it) }
                }

            }
        )

        StatusManager.close(statusId)
    }

    override fun findMovies(query: String) {
        TODO("Not yet implemented")
    }
}