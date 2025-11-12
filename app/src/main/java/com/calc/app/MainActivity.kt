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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.calc.app.ui.theme.CalcTheme
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import androidx.compose.ui.viewinterop.AndroidView
import com.calc.app.ui.screens.RootScreen

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		MobileAds.initialize(this) {}
		setContent {
			CalcApp()
		}
	}
}

@Composable
fun CalcApp() {
	CalcTheme {
		var isAdLoaded by remember { mutableStateOf(false) }

		Scaffold(
			bottomBar = {
				if (isAdLoaded) {
					BannerAd() // Banner sudah loaded, tampilkan tanpa callback
				}
			}
		) { paddingValues ->
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
			) {
				RootScreen()
			}
		}

		// Load ad initially (hidden until loaded)
		if (!isAdLoaded) {
			BannerAd(onAdLoaded = { loaded -> isAdLoaded = loaded })
		}
	}
}

@Composable
private fun BannerAd(onAdLoaded: (Boolean) -> Unit = {}) {
	AndroidView(
		factory = { context ->
			AdView(context).apply {
				setAdSize(AdSize.BANNER)
				// Test banner unit ID
				adUnitId = "ca-app-pub-3940256099942544/6300978111"

				adListener = object : AdListener() {
					override fun onAdLoaded() {
						super.onAdLoaded()
						onAdLoaded(true)
					}

					override fun onAdFailedToLoad(p0: LoadAdError) {
						super.onAdFailedToLoad(p0)
						onAdLoaded(false)
					}
				}

				loadAd(AdRequest.Builder().build())
			}
		},
		modifier = Modifier.fillMaxWidth()
	)
}


