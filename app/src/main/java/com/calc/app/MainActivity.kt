package com.calc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calc.app.ui.theme.CalcTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import androidx.compose.ui.viewinterop.AndroidView
import com.calc.app.ui.screens.RootScreen
import com.calc.app.ui.screens.SplashScreen
import androidx.compose.foundation.layout.Spacer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            if (showSplash) {
                SplashScreen(onTimeout = { showSplash = false })
            } else {
                CalcApp()
            }
        }
    }
}

@Composable
fun CalcApp() {
    CalcTheme {
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Calculator content area, takes all available space except for the ad
                Box(modifier = Modifier.weight(1f)) {
                    RootScreen()
                }

                // Spacer to ensure content is not overlapped by the ad
                Spacer(modifier = Modifier.padding(vertical = 4.dp))

                // Dedicated ad area at the bottom
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BannerAd()
                }
            }
        }
    }
}

@Composable
private fun BannerAd() {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.density.toInt()
                )
                setAdSize(adSize)
                // Test banner unit ID
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
