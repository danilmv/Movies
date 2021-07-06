package com.andriod.movies.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.databinding.ItemMovieBinding
import com.andriod.movies.entity.Movie

class MovieListAdapter : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {
    var movies: List<Movie> = ArrayList()
        set(value) {
            Log.d(TAG, "movies: size = ${movies.size}")
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
        Log.d(TAG, "getItemCount() called: list.size=${movies.size}")
        return movies.size
    }

    inner class ViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var movie: Movie

        init {
            itemView.setOnClickListener { listener?.onItemClick(movie) }
        }

        fun bind(movie: Movie) {
            this.movie = movie
            binding.textViewTitle.text = movie.title
            binding.textViewRating.text = movie.imdbRating
            binding.textViewYear.text = movie.year
        }
    }

    companion object {
        private const val TAG: String = "@@ListAdapter"
    }

    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
    }
}