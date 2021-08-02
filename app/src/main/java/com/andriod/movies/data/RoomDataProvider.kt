package com.andriod.movies.data

import com.andriod.movies.data.dao.MoviesDao
import com.andriod.movies.entity.Genre
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.room.*
import com.andriod.movies.statusbar.StatusManager

class RoomDataProvider(
    service: TheMovieDBService,
    private val dao: MoviesDao,
) : RetrofitDataProvider(service) {

    override fun startService() {
        val statusId = StatusManager.create("DB requested", DataProvider.Companion.StatusGroup.ROOM_DATA_REQUESTED.id)
        dataHandler.post {
            StatusManager.change(statusId, "DB requested... genres")
            dao.getGenres().forEach {
                genres[it.genreId] = it.toGenre()
                if (genres.isNotEmpty()) isGenresLoaded = true
            }

            StatusManager.change(statusId, "DB requested... movies")
            dao.getAllMovies().forEach {
                val movie = it.toMovie()
                dao.getMovieList(movie.id).forEach {
                    movie.addList(it.listId)
                }
                movie._genre = dao.getMovieGenres(movie.id).toMutableList()
                addMovieToData(movie)

                updateGenres(mutableMapOf(Pair(movie.id, movie)))
            }

            notifySubscribers(DataProvider.Companion.SubscriberType.DATA)

            StatusManager.close(statusId, "DB data received")
        }

        super.startService()

        subscribe(DataProvider.Companion.SubscriberType.GENRES) {
            val genresDto = genres.values.toGenreDto().toTypedArray()
            dataHandler.post {
                if (dao.updateGenres(*genresDto) < genres.size)
                    dao.insertGenres(*genresDto)
            }
        }
    }

    override fun updateData(movie: Movie) {
        super.updateData(movie)
        saveDataToDB(movie)
    }

    private fun saveDataToDB(movie: Movie) {
        dataHandler.post {
            val movieDto = movie.toMovieDto()
            if (dao.updateMovie(movieDto) == 0)
                dao.insertMovie(movieDto)

            val listsDto = movie.lists.toListsDto().toTypedArray()
            if (dao.updateList(*listsDto) < movie.lists.size)
                dao.insertList(*listsDto)

            val movieListDto = movie.lists.toMovieListDto(movie.id).toTypedArray()
            if (dao.updateMovieList(*movieListDto) < movie.lists.size)
                dao.insertMovieList(*movieListDto)

            val movieGenreDto = movie._genre.toMovieGenreDto(movie.id).toTypedArray()
            if (dao.updateMovieGenres(*movieGenreDto) < movie.lists.size)
                dao.insertMovieGenres(*movieGenreDto)
        }
    }

    override fun findMovies(query: String) {
        super.findMovies(query)
    }

    override fun getMovieDetails(movie: Movie) {
        super.getMovieDetails(movie)
        updateData(movie)
    }

    override fun dataChanged(movie: Movie) {
        super.dataChanged(movie)
        saveDataToDB(movie)
    }

    private fun Movie.toMovieDto() =
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


    private fun MovieDto.toMovie(): Movie {
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

    private fun MutableList<String>.toMovieGenreDto(movieId: String): List<MovieGenreDto> {
        val result = ArrayList<MovieGenreDto>()
        repeat(this.size) {
            if (this[it].isNotBlank()) {
                result.add(MovieGenreDto(
                    movieId,
                    this[it],
                ))
            }
        }

        return result
    }

    private fun MutableCollection<Genre>.toGenreDto(lang: String = "EN"): List<GenreDto> {
        val result = ArrayList<GenreDto>()
        forEach { genre ->
            result.add(GenreDto(genre.id, lang, genre.name))
        }

        return result
    }

    private fun GenreDto.toGenre() = Genre(genreId, text)

    companion object {
        const val TAG = "@@RoomDataProvider"
    }
}
