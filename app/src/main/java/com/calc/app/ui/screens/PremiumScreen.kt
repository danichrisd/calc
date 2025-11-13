package com.calc.app.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    // Handle purchase
                }
            }
        }
        .enablePendingPurchases()
        .build()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Go Premium!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Remove ads and support the developer.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            val productList = listOf(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId("remove_ads")
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build()
                            )
                            val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
                            billingClient.queryProductDetailsAsync(params) { _, productDetailsList ->
                                for (productDetails in productDetailsList) {
                                    val productDetailsParamsList = listOf(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(productDetails)
                                            .build()
                                    )
                                    val billingFlowParams = BillingFlowParams.newBuilder()
                                        .setProductDetailsParamsList(productDetailsParamsList)
                                        .build()
                                    billingClient.launchBillingFlow(context as Activity, billingFlowParams)
                                }
                            }
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        // Try to restart the connection on the next user gesture.
                    }
                })
            }) {
                Text("Remove Ads")
            }
        }
    }
}
