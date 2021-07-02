package com.andriod.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andriod.movies.fragment.ListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyViewModel.getData()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, ListFragment())
            .commit()
    }
}