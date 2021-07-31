package com.andriod.movies.data

import com.andriod.movies.data.dao.MovieDatabase
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.room.MovieDto

class RoomDataProvider(
    private val service: TheMovieDBService,
    private val database: MovieDatabase,
) : RetrofitDataProvider(service) {

    init {
        startService()
    }

    override fun startService() {
        super.startService()
    }

    override fun updateData(movie: Movie) {
        super.updateData(movie)
//        database.moviesDao().updateMovie(movie.toDto())
    }

    override fun findMovies(query: String) {
        TODO("Not yet implemented")
    }

    private fun Movie.toDto() =
        MovieDto(id, title, originalTitle, runtime, released, year,
            plot, poster, rating, votes, revenue, budget, _type,
            if (isFavorite) 1 else 0,
            if (isDetailsReceived) 1 else 0)
}
