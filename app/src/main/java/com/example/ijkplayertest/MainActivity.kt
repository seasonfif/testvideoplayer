package com.example.ijkplayertest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        single.setOnClickListener {
            startActivity(Intent(MainActivity@this, SinglePlayerActivity::class.java))
        }

        some.setOnClickListener {
            startActivity(Intent(MainActivity@this, SomePlayerActivity::class.java))
        }

        android_media.setOnClickListener {
            startActivity(Intent(MainActivity@this, AndroidPlayerActivity::class.java))
//            startActivity(Intent(MainActivity@this, DemoPlayerActivity::class.java))
        }
    }
}