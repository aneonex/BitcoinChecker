package com.aneonex.bitcoinchecker.tester.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.aneonex.bitcoinchecker.tester.ui.theme.MyAppTheme
import com.aneonex.bitcoinchecker.tester.ui.navigation.MyAppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MyAppNavHost()
            }
        }
    }
}