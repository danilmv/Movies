package com.andriod.movies.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andriod.movies.R
import com.andriod.movies.databinding.ItemMovieBinding
import com.andriod.movies.entity.Movie

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val TAG: String = "@@ListAdapter"
    var movies: List<Movie> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemMovieBinding =
            ItemMovieBinding.inflate(LayoutInflater.from(itemView.context))
        private val textViewTitle = itemView.findViewById<TextView>(R.id.text_view_title)

        fun bind(movie: Movie) {
            Log.d(TAG, "bind() called with: movie = $movie")
//            binding.textViewTitle.text = movie.title
            textViewTitle.text = movie.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount() called: list.size=${movies.size}")
        return movies.size
    }
}