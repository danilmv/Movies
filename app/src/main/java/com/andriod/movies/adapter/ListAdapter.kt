package com.andriod.movies.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.databinding.ItemMovieBinding
import com.andriod.movies.entity.Movie

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val TAG: String = "@@ListAdapter"
    var movies: List<Movie> = ArrayList()
        set(value) {
            Log.d(TAG, "movies: size = ${movies.size}")
            field = value
            notifyDataSetChanged()
        }

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

    inner class ViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.textViewTitle.text = movie.title
        }
    }
}