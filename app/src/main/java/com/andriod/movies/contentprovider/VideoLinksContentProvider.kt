package com.andriod.movies.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.andriod.movies.MovieApplication
import com.andriod.movies.R

class VideoLinksContentProvider : ContentProvider() {
    private lateinit var uriMatcher: UriMatcher

    override fun onCreate(): Boolean {
        val authority = context?.resources?.getString(R.string.authorities)

        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(authority, "$VIDEOS_PATH/#", URI_ID)

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        val dao = (context as MovieApplication).database.moviesDao()

        return when (uriMatcher.match(uri)) {
            URI_ID -> dao.getMovieVideosCursor(uri.lastPathSegment ?: "")
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        return 0
    }

    companion object {
        const val URI_ID = 1
        const val VIDEOS_PATH = "videos"
    }
}