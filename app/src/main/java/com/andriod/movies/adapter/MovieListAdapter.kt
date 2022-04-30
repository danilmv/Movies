package com.andriod.movies.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.databinding.ItemMovieBinding
import com.andriod.movies.entity.Movie

class MovieListAdapter : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {
    var movies: List<Movie> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMovieBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class ViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var movie: Movie
        private var isBindingInProgress = false

        init {
            itemView.setOnClickListener { listener?.onItemClick(movie) }

            binding.toggleFavorite.setOnCheckedChangeListener { _, isChecked: Boolean ->
                movie.isFavorite = isChecked
                if (!isBindingInProgress) listener?.onFavoriteChanged(movie)
            }
        }

        fun bind(movie: Movie) {
            isBindingInProgress = true
            this.movie = movie
            binding.textViewTitle.text = movie.title
            binding.textViewRating.text = movie.rating
            binding.textViewYear.text = movie.year
            binding.toggleFavorite.isChecked = movie.isFavorite
            isBindingInProgress = false
        }
    }

    companion object {
        private const val TAG: String = "@@ListAdapter"
    }

    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
        fun onFavoriteChanged(movie: Movie)
    }
}