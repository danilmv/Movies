package com.andriod.movies.data

import com.andriod.movies.data.dao.MoviesDao
import com.andriod.movies.entity.Genre
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.Video
import com.andriod.movies.entity.room.*
import com.andriod.movies.statusbar.StatusManager

class RoomDataProvider(
    service: TheMovieDBService,
    private val dao: MoviesDao,
) : RetrofitDataProvider(service) {

    override fun startService() {
        val statusId = StatusManager.create("DB requested",
            DataProvider.Companion.StatusGroup.ROOM_DATA_REQUESTED.id)
        dataHandler.post {
            StatusManager.change(statusId, "DB requested... genres")
            dao.getGenres().forEach {
                genres[it.genreId] = it.toGenre().also { genre -> genre.isSavedToDB = true }
            }
//            if (genres.isNotEmpty()) isGenresLoaded = true

            StatusManager.change(statusId, "DB requested... movies")
            dao.getAllMovies().forEach {
                val movie = it.toMovie()
                dao.getMovieList(movie.id).forEach {
                    movie.addList(it.listId)
                }
                movie._genre = dao.getMovieGenres(movie.id).toMutableList()

                dao.getMovieVideos(movie.id).forEach{ videoId->
                    val video = dao.getVideo(videoId).toVideo()
                    video.isSavedToDB = true
                    movie.videos[video.key] = video
                }

                addMovieToData(movie)

                updateGenres(mutableMapOf(Pair(movie.id, movie)))

                movie.isSavedToDB = true
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
            if (!movie.isSavedToDB) {
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

            if (movie.videos.isNotEmpty()) {
                val videosDto = movie.videos.values.toVideosDto().toTypedArray()
                if (videosDto.isNotEmpty()) {
                    dao.insertVideos(*videosDto)

                    val movieVideosDto =
                        movie.videos.values.toMovieVideosDto(movie.id).toTypedArray()
                    dao.insertMovieVideos(*movieVideosDto)
                }
            }
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


    private fun VideoDto.toVideo() = Video(name, videoId, site, type)

    private fun MutableCollection<Video>.toMovieVideosDto(
        movieId: String,
        lang: String = "EN",
    ): List<MovieVideoDto> {
        val result = ArrayList<MovieVideoDto>()
        forEach { video ->
            if (!video.isSavedToDB) {
                result.add(MovieVideoDto(movieId, video.key, lang))
            }
        }
        return result
    }

    private fun MutableCollection<Video>.toVideosDto(): List<VideoDto> {
        val result = ArrayList<VideoDto>()
        forEach { video ->
            if (!video.isSavedToDB) {
                result.add(VideoDto(video.key, video.name, video.site, video.type))
            }
        }

        return result
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
            if (isDetailsReceived) 1 else 0,
            _background)


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
        movie._background = background

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
