package com.andriod.movies.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andriod.movies.R
import com.andriod.movies.databinding.FragmentListBinding
import com.andriod.movies.databinding.FragmentMovieBinding
import com.andriod.movies.entity.Movie

class MovieFragment : Fragment() {

    private var movie: Movie? = null
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMovieDetails()
    }

    private fun showMovieDetails() {
        binding.textViewTitle.text = String.format(getString(R.string.details_title), movie?.title)
        binding.textViewActors.text =
            String.format(getString(R.string.details_actors), movie?.actors)
        binding.textViewBoxOffice.text =
            String.format(getString(R.string.details_box_office), movie?.boxOffice)
        binding.textViewPlot.text = String.format(getString(R.string.details_plot), movie?.plot)
        binding.textViewRating.text =
            String.format(getString(R.string.details_rating), movie?.imdbRating)
        binding.textViewVotes.text =
            String.format(getString(R.string.details_votes), movie?.imdbVotes)
        binding.textViewYear.text = String.format(getString(R.string.details_year), movie?.year)
        binding.textViewType.text = String.format(getString(R.string.details_type), movie?.type)
        binding.toggleFavorite.isChecked = movie?.isFavorite ?: false
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
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