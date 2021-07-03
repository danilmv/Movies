package com.andriod.movies.data

import android.util.Log
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.SearchResults
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Thread.sleep
import java.lang.reflect.Type

class DummyDataProvider : DataProvider() {
    private val movieType: Type = object : TypeToken<Movie>() {}.type
    private val searchResultsType: Type = object : TypeToken<SearchResults>() {}.type
    private val gson = Gson()

    init {
        val movie: Movie = gson.fromJson(QUERY_RESULT, movieType)
        data[movie.id] = movie

        val searchResult: SearchResults = gson.fromJson(SEARCH_RESULT, searchResultsType)
        Thread() {
            for (searchedMovie in searchResult.search) {
                data[searchedMovie.id] = searchedMovie
                Log.d(TAG, "data changed: size=${data.size}")
                notifySubscribers()
                sleep(1000)
            }
        }.start()
    }

    override fun updateData(movie: Movie) {
    }

    override fun findMovies(query: String): List<Movie> {
        val searchResult: SearchResults = gson.fromJson(SEARCH_RESULT, searchResultsType)
        return searchResult.search
    }

    companion object {
        private const val QUERY_RESULT =
            "{\"Title\":\"Die Hard\",\"Year\":\"1988\",\"Rated\":\"R\",\"Released\":\"20 Jul 1988\",\"Runtime\":\"132 min\",\"Genre\":\"Action, Thriller\",\"Director\":\"John McTiernan\",\"Writer\":\"Roderick Thorp (based on the novel by), Jeb Stuart (screenplay by), Steven E. de Souza (screenplay by)\",\"Actors\":\"Bruce Willis, Bonnie Bedelia, Reginald VelJohnson, Paul Gleason\",\"Plot\":\"An NYPD officer tries to save his wife and several others taken hostage by German terrorists during a Christmas party at the Nakatomi Plaza in Los Angeles.\",\"Language\":\"English, German, Italian, Japanese\",\"Country\":\"USA\",\"Awards\":\"Nominated for 4 Oscars. Another 8 wins & 2 nominations.\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BZjRlNDUxZjAtOGQ4OC00OTNlLTgxNmQtYTBmMDgwZmNmNjkxXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.2/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"94%\"},{\"Source\":\"Metacritic\",\"Value\":\"72/100\"}],\"Metascore\":\"72\",\"imdbRating\":\"8.2\",\"imdbVotes\":\"807,073\",\"imdbID\":\"tt0095016\",\"Type\":\"movie\",\"DVD\":\"25 Nov 2015\",\"BoxOffice\":\"\$83,844,093\",\"Production\":\"Silver Pictures, Gordon Company\",\"Website\":\"N/A\",\"Response\":\"True\"}"
        private const val SEARCH_RESULT =
            "{\"Search\":[{\"Title\":\"Spy Hard\",\"Year\":\"1996\",\"imdbID\":\"tt0117723\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BOGFmYWFiZDAtZTc1YS00ZTIxLTkwODctODc0MzUzNGIyNzgwL2ltYWdlXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg\"},{\"Title\":\"Hard Rain\",\"Year\":\"1998\",\"imdbID\":\"tt0120696\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BZWVlYTJjNjEtNzY2Yy00YzA0LWEwMjMtOGFjNjIwYTk1ZDhjXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg\"},{\"Title\":\"Hard Ball\",\"Year\":\"2001\",\"imdbID\":\"tt0180734\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMjY3Nzk0NzkxMF5BMl5BanBnXkFtZTgwMTYxNzYxMTE@._V1_SX300.jpg\"},{\"Title\":\"Hard to Kill\",\"Year\":\"1990\",\"imdbID\":\"tt0099739\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTc4NzE1NTU5N15BMl5BanBnXkFtZTgwNTgwNTg4NjE@._V1_SX300.jpg\"},{\"Title\":\"The Goods: Live Hard, Sell Hard\",\"Year\":\"2009\",\"imdbID\":\"tt1092633\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTMwNDI1OTk3MF5BMl5BanBnXkFtZTcwMzY5MDI2Mg@@._V1_SX300.jpg\"},{\"Title\":\"The Hard Way\",\"Year\":\"1991\",\"imdbID\":\"tt0102004\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMzkyZWRiMTEtNmZhNy00NjI3LWE2ZDktM2UzYjkyNDBiMmE4XkEyXkFqcGdeQXVyNDk3NzU2MTQ@._V1_SX300.jpg\"},{\"Title\":\"A Hard Day\",\"Year\":\"2014\",\"imdbID\":\"tt3697626\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BYThiMzE1ZWMtZDU2MC00ZmQ2LTg0ODEtMTgxNjA4MDk0ZGIzXkEyXkFqcGdeQXVyNDE2NjE1Njc@._V1_SX300.jpg\"},{\"Title\":\"Hard Times\",\"Year\":\"1975\",\"imdbID\":\"tt0073092\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNDQ5MTk5OTQtMWRmYy00ZGVjLThlYjItYmMwOTU3ZjI4MjEzXkEyXkFqcGdeQXVyMTMxMTY0OTQ@._V1_SX300.jpg\"},{\"Title\":\"Hard Kill\",\"Year\":\"2020\",\"imdbID\":\"tt11656172\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMmFhM2U0N2YtZDA3NS00MDcxLThlZTMtNTEwMGNiZWExNmJlXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_SX300.jpg\"},{\"Title\":\"The Hard Corps\",\"Year\":\"2006\",\"imdbID\":\"tt0462329\",\"Type\":\"movie\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BYTQ2ZDJiODAtZGQyMC00NzBkLTgyZjktZmZiMDE5MDJhZmRmXkEyXkFqcGdeQXVyNzMwOTY2NTI@._V1_SX300.jpg\"}],\"totalResults\":\"1218\",\"Response\":\"True\"}"
        private const val TAG = "@@DummyDataProvider"
    }
}