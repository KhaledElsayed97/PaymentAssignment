package com.zeal.paymentassignment.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zeal.paymentassignment.core.BroadcastConstants.FINAL_TRANSACTION_DATA
import com.zeal.paymentassignment.core.BroadcastConstants.FINAL_TRANSACTION_INTENT_ACTION

class ZealBroadcastReceiver : BroadcastReceiver() {

    private val finalTransaction = MutableLiveData<String>()

    val finalTransactionAmount: LiveData<String> get() = finalTransaction

    private fun updateFinalTransaction(newData: String) {
        finalTransaction.value = newData
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            FINAL_TRANSACTION_INTENT_ACTION -> {
                intent?.getStringExtra(FINAL_TRANSACTION_DATA)
                    ?.let { updateFinalTransaction(it) }
            }
        }
    }
}