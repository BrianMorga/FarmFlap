package com.example.farmflap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager


class GameView (context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), Runnable {

    private var ourHolder: SurfaceHolder = holder
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()

    private var chicken: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.chicken)
    private var background: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.background6)
    private var topWM: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.windmill2)
    private var bottomWM: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.windmill)

    private var isPlaying: Boolean = false
    private var chickenX: Float = 75f
    private var chickenY: Float = 0f
    private var wMillX: Float = 0f
    private var wMillY: Float = 0f

    private var velocity: Float = 0f
    private var gravity: Float = 2f



    init {
        wMillX = width.toFloat()
        wMillY = (Math.random() * (height - bottomWM.height)).toFloat()
        isPlaying = true
        chicken = Bitmap.createScaledBitmap(chicken, 50, 50, false)
        start()
        background = BitmapFactory.decodeResource(resources, R.drawable.background6)
        val displayMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
            displayMetrics
        )
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Resize the background to match the screen size
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false)
    }




    override fun run() {
        while (isPlaying) {
            update()
            draw()
            control()
        }
    }

    private fun start() {
        Thread(this).start()
    }

    fun update() {
        chickenY += velocity
        velocity += gravity

        if (chickenY > height - chicken.height || chickenY < 0) {
            isPlaying = false
        }

        wMillX -= 5f

        if (chickenX + chicken.width > wMillX && chickenX < wMillX + topWM.width && chickenY + chicken.height > wMillY + topWM.height) {
            isPlaying = false
        }

        if (wMillX < -topWM.width) {
            wMillX = width.toFloat()
            wMillY = (Math.random() * (height - bottomWM.height)).toFloat()
        }
    }

    private fun draw() {
        if (ourHolder.surface.isValid) {
            canvas = ourHolder.lockCanvas()
            canvas.drawBitmap(background, 0f, 0f, paint)
            canvas.drawBitmap(chicken, chickenX, chickenY, paint)
            canvas.drawBitmap(topWM, wMillX, wMillY, paint)
            canvas.drawBitmap(bottomWM, wMillX, wMillY + topWM.height + 200, paint)
            ourHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun control() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun pause() {
        isPlaying = false
        try {
           val thread = Thread.currentThread()
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    fun resumeGame() {
        isPlaying = true
        start()
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                velocity = -30f
            }
        }
        return true
    }


}