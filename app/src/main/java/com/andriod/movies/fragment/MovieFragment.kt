package com.andriod.movies.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andriod.movies.MyViewModel
import com.andriod.movies.R
import com.andriod.movies.databinding.FragmentMovieBinding
import com.andriod.movies.entity.Movie

class MovieFragment : Fragment() {

    private var movie: Movie? = null
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private val contract: MovieContract?
        get() = activity as MovieContract?

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

        MyViewModel.movies.observe(viewLifecycleOwner, { showMovieDetails() })
    }

    private fun showMovieDetails() {
        binding.textViewTitle.text = String.format(getString(R.string.details_title), movie?.title)
//        binding.textViewActors.text =
//            String.format(getString(R.string.details_actors), movie?.actors ?: "?")
        binding.textViewBoxOffice.text =
            String.format(getString(R.string.details_revenue), movie?.revenue)
        binding.textViewPlot.text =
            String.format(getString(R.string.details_plot), movie?.plot ?: "?")
        binding.textViewRating.text =
            String.format(getString(R.string.details_rating), movie?.rating ?: "?.?")
        binding.textViewVotes.text =
            String.format(getString(R.string.details_votes), movie?.votes ?: "?")
        binding.textViewYear.text = String.format(getString(R.string.details_year), movie?.year)
        binding.textViewType.text = String.format(getString(R.string.details_type), movie?.type)
        binding.toggleFavorite.isChecked = movie?.isFavorite ?: false
        binding.toggleFavorite.setOnCheckedChangeListener { _, isChecked: Boolean ->
            movie?.let {
                it.isFavorite = isChecked
                contract?.onMovieChanged(it)
            }
        }
        binding.textViewRuntime.text =
            String.format(getString(R.string.details_runtime), movie?.runtime ?: "?")

        binding.textViewReleased.text =
            String.format(getString(R.string.details_released), movie?.released ?: "??.??.????")

        binding.textViewGenres.text = String.format(getString(R.string.details_genres), movie?.genre?.joinToString(", "))
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

    interface MovieContract {
        fun onMovieChanged(movie: Movie)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is MovieContract) { "Activity must implement MovieContract" }
    }
}