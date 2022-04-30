package com.andriod.movies.entity

import org.json.JSONObject

data class Genre(
    val id: String,
    val name: String,
) {
    var isSavedToDB = false

    companion object {
        fun jsonToList(raw: String): List<Genre> {
            val result = mutableListOf<Genre>()
            val jsonTrendingObj = JSONObject(raw)
            val results = jsonTrendingObj.getJSONArray("genres")
            for (i in 0 until results.length()) {
                result.add(jsonToObject(results.getString(i)))
            }

            return result
        }

        private fun jsonToObject(raw: String): Genre {
            val jsonObj = JSONObject(raw)
            return Genre(
                jsonObj.getString("id"),
                jsonObj.getString("name")
            )
        }
    }
}

