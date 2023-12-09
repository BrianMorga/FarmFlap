package com.example.farmflap

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)
        startButton = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            gameView.resumeGame()
            startButton.isEnabled = false
            startButton.visibility = Button.INVISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }
}