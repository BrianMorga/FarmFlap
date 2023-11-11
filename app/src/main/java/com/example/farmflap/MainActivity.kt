package com.example.farmflap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.farmflap.ui.theme.FarmFlapTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FarmFlapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    FarmFlapApp()
                }
            }
        }
    }
}

@Composable
fun MainScreen(onPlayClick: () -> Unit) {
    //main screen ui
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //play button
    }
}

@Composable
fun GameScreen(onPauseClick: () -> Unit, onRestartClick: () -> Unit) {
    //game screen ui
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //pause button
        //restart button
    }
}

@Composable
fun FarmFlapApp(){
    var isGameScreen by remember { mutableStateOf(false) }

    if(isGameScreen){
        GameScreen(
            onPauseClick = { isGameScreen = false },
            onRestartClick = { /*TODO*/ }
        )
}else {
        MainScreen(
            onPlayClick = { isGameScreen = true })
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(onPlayClick = {})
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview(){
    GameScreen(onPauseClick = {}, onRestartClick = {})
}



