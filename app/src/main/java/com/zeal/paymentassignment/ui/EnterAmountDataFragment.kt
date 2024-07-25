package com.zeal.paymentassignment.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.zeal.paymentassignment.R
import com.zeal.paymentassignment.core.BroadcastConstants.FINAL_TRANSACTION_INTENT_ACTION
import com.zeal.paymentassignment.core.BroadcastConstants.TRANSACTION_DATA
import com.zeal.paymentassignment.core.BroadcastConstants.TRANSACTION_INTENT_ACTION
import com.zeal.paymentassignment.core.DialogHelper
import com.zeal.paymentassignment.core.FlowDataObject
import com.zeal.paymentassignment.core.ZealBroadcastReceiver
import com.zeal.paymentassignment.databinding.FragmentEnterAmount2Binding

class EnterAmountDataFragment : Fragment() {
    val binding by lazy {
        FragmentEnterAmount2Binding.inflate(layoutInflater)
    }

    private var zealBroadcastReceiver = ZealBroadcastReceiver()
    private var transactionAmountFilter =
        IntentFilter(FINAL_TRANSACTION_INTENT_ACTION)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ContextCompat.registerReceiver(
            requireContext(), zealBroadcastReceiver, transactionAmountFilter,
            ContextCompat.RECEIVER_EXPORTED
        )

        zealBroadcastReceiver.finalTransactionAmount.observe(viewLifecycleOwner, Observer { finalTransactionAmount ->
            onFinalTransactionReceived(finalTransactionAmount.toFloat())
        })

        binding.btnConfirm.setOnClickListener {
            val amount = binding.tvEnterAmount.text.toString()
            if (amount.isNotBlank()) {
                try {
                    val amountF = amount.toFloat()
                    if (amountF == 0.0f)
                        Toast.makeText(context, "cant be zero", Toast.LENGTH_SHORT).show()
                    else {
                        FlowDataObject.getInstance().amount = amountF;
                        sendTransactionDetails(amountF.toString())
                        DialogHelper.showLoadingDialog(requireActivity(), "Awaiting final transaction amount")
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "please add valid number", Toast.LENGTH_SHORT).show()
                }
            } else
                Toast.makeText(context, "cant be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendTransactionDetails(transactionAmount: String) {
        val intent = Intent(TRANSACTION_INTENT_ACTION)
        intent.putExtra(TRANSACTION_DATA, transactionAmount)
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        activity?.sendBroadcast(intent)
    }

    private fun onFinalTransactionReceived(finalTransactionAmount: Float) {
        activity?.let { DialogHelper.hideLoading(it) }
        FlowDataObject.getInstance().amount = finalTransactionAmount
        DialogHelper.showFinalTransactionDialog(finalTransactionAmount.toString(),requireContext()) {
            if (finalTransactionAmount == 0f)
                findNavController().navigate(R.id.action_enterAmountDataFragment_to_printReceiptFragment)
            else
                findNavController().navigate(R.id.action_enterAmountDataFragment_to_swipeCardFragment)
        }
    }
}