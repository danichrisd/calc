plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
}

android {
	namespace = "com.calc.app"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.calc.app"
		minSdk = 22
		targetSdk = 35
		versionCode = 1
		versionName = "1.0.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
		debug {
			isMinifyEnabled = false
		}
	}

	buildFeatures {
		compose = true
		buildConfig = true
	}

	composeOptions {
		// Compose Compiler mapped from Kotlin plugin; keep explicit for clarity
		kotlinCompilerExtensionVersion = "1.5.15"
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
		freeCompilerArgs = freeCompilerArgs + listOf(
			"-Xjvm-default=all",
			"-Xcontext-receivers"
		)
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	val composeBom = platform("androidx.compose:compose-bom:2024.09.01")
	implementation(composeBom)
	androidTestImplementation(composeBom)

	// Compose
	implementation("androidx.activity:activity-compose:1.9.3")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material-icons-extended")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-tooling-preview")
	debugImplementation("androidx.compose.ui:ui-tooling")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
	implementation("androidx.core:core-ktx:1.13.1")

	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

	// AdMob
	implementation("com.google.android.gms:play-services-ads:23.3.0")

	// Google Play Billing
	implementation("com.android.billingclient:billing-ktx:7.0.0")

	// Testing
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}