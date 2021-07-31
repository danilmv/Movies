package com.andriod.movies.data

import android.util.Log
import com.andriod.movies.data.dao.MoviesDao
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.room.MovieDto

class RoomDataProvider(
    service: TheMovieDBService,
    private val dao: MoviesDao,
) : RetrofitDataProvider(service) {

    override fun startService() {
        dataHandler.post {
            dao.getAll().forEach {
                addMovieToData(it.fromDto())
            }
            notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
            Log.d(TAG, "startService() data=${data.size}")
        }

//        super.startService()
    }

    override fun updateData(movie: Movie) {
        super.updateData(movie)
        dataHandler.post {
            val dto = movie.toDto()
            if (dao.updateMovie(dto) == 0)
                dao.insertMovie(movie.toDto())
        }
    }

    override fun findMovies(query: String) {
        super.findMovies(query)
    }

    private fun Movie.toDto() =
        MovieDto(id,
            title,
            originalTitle,
            runtime,
            released,
            year,
            plot,
            poster,
            rating,
            votes,
            revenue,
            budget,
            _type,
            if (isFavorite) 1 else 0,
            if (isDetailsReceived) 1 else 0)


    private fun MovieDto.fromDto(): Movie {
        val movie = Movie()

        movie.id = id
        movie._title = title
        movie.name = title
        movie.originalTitle = originalTitle
        movie.runtime = runtime
        movie.released = released
        movie.plot = plot
        movie._poster = if (poster.isNotBlank()) poster else null
        movie.rating = rating
        movie.votes = votes
        movie._revenue = if (revenue == "?") null else revenue.toInt()
        movie.budget = budget
        movie._type = type
        movie.isFavorite = isFavorite == 1
        movie.isDetailsReceived = isDetailed == 1

        return movie
    }

    companion object {
        const val TAG = "@@RoomDataProvider"
    }
}
