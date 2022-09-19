package com.financemanage.hmoneylover

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.financemanage.hmoneylover.billings.utilies.Prefs
import com.financemanage.hmoneylover.ui.MainActivity

class SplashActivity : AppCompatActivity() {

    private var billingClient: BillingClient? = null
    private var prefs: Prefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        prefs = Prefs(this)
        checkSubscription()
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)
    }

    private fun checkSubscription() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
            .setListener { _: BillingResult?, _: List<Purchase?>? -> }
            .build()
        val finalBillingClient = billingClient
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient?.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build()
                    ) { billingResult1: BillingResult, list: List<Purchase> ->
                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (list.isNotEmpty()) {
                                prefs!!.premium = 1
                                for ((i, purchase) in list.withIndex()) {
                                    Log.d("testOffer", purchase.originalJson)
                                    Log.d("testOffer", " index$i")
                                }
                            } else {
                                prefs!!.premium = 0
                            }
                        }
                    }
                }
            }
        })
    }
}