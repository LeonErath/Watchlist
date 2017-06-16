package com.example.leon.kotlinapplication.activities

import android.os.Bundle
import android.widget.Button
import com.example.leon.kotlinapplication.R
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView


class YoutubeActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)

        val youtubePlayer = findViewById(R.id.youtubePlayer) as YouTubePlayerView
        val button = findViewById(R.id.buttonStart) as Button

        youtubePlayer.initialize("YOUR API KEY", object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider,
                                                 youTubePlayer: YouTubePlayer, b: Boolean) {

                // do any work here to cue video, play video, etc.
                youTubePlayer.cueVideo("5xVh-7ywKpE")
            }

            override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {

            }
        })
    }
}
