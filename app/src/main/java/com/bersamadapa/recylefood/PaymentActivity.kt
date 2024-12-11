package com.bersamadapa.recylefood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bersamadapa.recylefood.utils.MidtransHelper
import com.midtrans.sdk.uikit.api.model.TransactionResult

class PaymentActivity : ComponentActivity() {
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var midtransHelper: MidtransHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Activity Result Launcher
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val transactionResult = result.data?.getParcelableExtra<TransactionResult>(
                    com.midtrans.sdk.uikit.internal.util.UiKitConstants.KEY_TRANSACTION_RESULT
                )
                Toast.makeText(this, "Transaction ID: ${transactionResult?.transactionId}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction was canceled or failed", Toast.LENGTH_LONG).show()
            }

            finish() // Close the activity after handling the result
        }

        // Retrieve the token from the Intent
        val paymentToken = intent.getStringExtra("PAYMENT_TOKEN") ?: return

        // Initialize MidtransHelper
        midtransHelper = MidtransHelper(this)
        midtransHelper.initialize("SB-Mid-client-iyeLW8nt_BsB_Ymr", "https://enormous-mint-tomcat.ngrok-free.app")

        // Start the payment flow with the provided token
        midtransHelper.startPaymentWithSnapToken(this, paymentToken, launcher)
    }

    override fun onBackPressed() {
        // Handle back press to ensure user returns to the previous screen
        Toast.makeText(this, "Payment process was canceled.", Toast.LENGTH_SHORT).show()
        super.onBackPressed()  // Closes the current activity and returns to the previous one
    }
}
