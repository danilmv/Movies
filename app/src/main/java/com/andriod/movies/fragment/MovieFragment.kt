package com.andriod.movies.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.andriod.movies.MyViewModel
import com.andriod.movies.R
import com.andriod.movies.databinding.FragmentMovieBinding
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.Video
import com.bumptech.glide.Glide

class MovieFragment : Fragment() {

    private var movie: Movie? = null
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private val contract: MovieContract?
        get() = activity as MovieContract?

    private var videoToPlay: Video? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getParcelable(MOVIE_EXTRA_KEY)
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
        binding.apply {
            textViewTitle.text =
                String.format(getString(R.string.details_title), movie?.title)
//        textViewActors.text = String.format(getString(R.string.details_actors), movie?.actors ?: "?")
            textViewBoxOffice.text =
                String.format(getString(R.string.details_revenue), movie?.revenue)
            textViewPlot.text = String.format(getString(R.string.details_plot), movie?.plot ?: "?")
            textViewRating.text =
                String.format(getString(R.string.details_rating), movie?.rating ?: "?.?")
            textViewVotes.text =
                String.format(getString(R.string.details_votes), movie?.votes ?: "?")
            textViewYear.text = String.format(getString(R.string.details_year), movie?.year)
            textViewType.text = String.format(getString(R.string.details_type), movie?._type)
            toggleFavorite.isChecked = movie?.isFavorite ?: false
            toggleFavorite.setOnCheckedChangeListener { _, isChecked: Boolean ->
                movie?.let {
                    it.isFavorite = isChecked
                    contract?.onMovieChanged(it)
                }
            }
            textViewRuntime.text =
                String.format(getString(R.string.details_runtime), movie?.runtime ?: "?")

            textViewReleased.text =
                String.format(getString(R.string.details_released), movie?.released ?: "??.??.????")

            textViewGenres.text =
                String.format(getString(R.string.details_genres), movie?.genre?.joinToString(", "))

            textViewLists.text =
                String.format(getString(R.string.details_lists), movie?.lists?.joinToString(", "))

            movie?.poster?.let {
                Glide.with(root)
                    .load(it)
                    .placeholder(imageViewPoster.drawable)
                    .centerCrop()
                    .into(imageViewPoster)
            }
        }

        showVideosList()
    }

    private fun showVideosList() {
        val spinnerValues = mutableListOf<String>()
        var visible = true
        movie?.videos?.forEach { spinnerValues.add(it.value.name) }

        if (spinnerValues.isNotEmpty()) {

            val adapter =
                ArrayAdapter(requireContext(),
                    R.layout.item_spinner_videos,
                    spinnerValues)

            binding.spinnerVideos.apply {
                this.adapter = adapter

                onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long,
                        ) {
                            videoToPlay = movie?.videos?.values?.elementAt(position)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
            }
            binding.imageButtonVideo.setOnClickListener {
                videoToPlay?.let {
                    contract?.onPlayVideo(it)
                }
            }

        } else {
            visible = false
        }

        binding.spinnerVideos.isVisible = visible
        binding.imageButtonVideo.isVisible = visible
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
        fun onPlayVideo(video: Video)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(context is MovieContract) { "Activity must implement MovieContract" }
    }
}