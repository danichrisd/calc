package com.calc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.calc.app.ui.theme.CalcTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcApp() {
	CalcTheme {
		Scaffold(
			bottomBar = { BannerAd() }
		) { _ ->
			Box(
				modifier = Modifier
					.fillMaxSize()
					// padding automatically applied via Scaffold content lambda param
			) {
				RootScreen()
			}
		}
	}
}

@Composable
private fun BannerAd() {
	AndroidView(
		factory = { context ->
			AdView(context).apply {
				setAdSize(AdSize.BANNER)
				// Test banner unit ID
				adUnitId = "ca-app-pub-3940256099942544/6300978111"
				loadAd(AdRequest.Builder().build())
			}
		}
	)
}


