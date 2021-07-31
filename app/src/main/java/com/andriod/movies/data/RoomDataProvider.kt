package com.andriod.movies.data

import android.util.Log
import com.andriod.movies.data.dao.MoviesDao
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.room.ListsDto
import com.andriod.movies.entity.room.MovieDto
import com.andriod.movies.entity.room.MovieListDto

class RoomDataProvider(
    service: TheMovieDBService,
    private val dao: MoviesDao,
) : RetrofitDataProvider(service) {

    override fun startService() {
        dataHandler.post {
            dao.getAll().forEach {
                val movie = it.fromDto()
                dao.getMovieList(movie.id).forEach {
                    movie.addList(it.listId)
                }
                addMovieToData(movie)
            }
            notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
            Log.d(TAG, "startService() data=${data.size}")
        }

        super.startService()
    }

    override fun updateData(movie: Movie) {
        super.updateData(movie)
        dataHandler.post {
            val movieDto = movie.toListsDto()
            if (dao.updateMovie(movieDto) == 0)
                dao.insertMovie(movie.toListsDto())

            val listsDto = movie.lists.toListsDto().toTypedArray()
            if (dao.updateList(*listsDto) < movie.lists.size)
                dao.insertList(*listsDto)

            val movieListDto = movie.lists.toMovieListDto(movie.id).toTypedArray()
            if (dao.updateMovieList(*movieListDto) < movie.lists.size)
                dao.insertMovieList(*movieListDto)
        }
    }

    override fun findMovies(query: String) {
        super.findMovies(query)
    }

    override fun getMovieDetails(movie: Movie) {
        super.getMovieDetails(movie)
        updateData(movie)
    }

    private fun Movie.toListsDto() =
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

    private fun MutableList<String>.toListsDto(lang: String = "EN"): List<ListsDto> {
        val result = ArrayList<ListsDto>()
        repeat(this.size) {
            if (this[it].isNotBlank()) {
                result.add(ListsDto(
                    this[it],
                    lang,
                    this[it],
                ))
            }
        }

        return result
    }

    private fun MutableList<String>.toMovieListDto(movieId: String): List<MovieListDto> {
        val result = ArrayList<MovieListDto>()
        repeat(this.size) {
            if (this[it].isNotBlank()) {
                result.add(MovieListDto(
                    movieId,
                    this[it],
                ))
            }
        }

        return result
    }

    companion object {
        const val TAG = "@@RoomDataProvider"
    }
}
