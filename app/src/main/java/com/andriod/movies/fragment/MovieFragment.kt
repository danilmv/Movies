package com.andriod.movies.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andriod.movies.databinding.FragmentListBinding
import com.andriod.movies.databinding.FragmentMovieBinding
import com.andriod.movies.entity.Movie

class MovieFragment : Fragment() {

    private var movie: Movie? = null
    private var binding: FragmentMovieBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getParcelable<Movie>(MOVIE_EXTRA_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMovieDetails()
    }

    private fun showMovieDetails() {
        binding?.textViewTitle?.text = movie?.title
    }

    companion object {
        private const val MOVIE_EXTRA_KEY = "MOVIE"

        @JvmStatic
        fun newInstance(movie: Movie) =
            MovieFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MOVIE_EXTRA_KEY, movie)
                }
            }
    }
}