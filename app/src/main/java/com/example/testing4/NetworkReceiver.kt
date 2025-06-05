package com.example.testing4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.testing4.utils.ConstValues.BROADCAST_ACTION
import com.example.testing4.utils.ConstValues.EXTRA_MESSAGE

class NetworkReceiver(private val onReceiveMessage: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == BROADCAST_ACTION){
            val  message1 = intent.getStringExtra(EXTRA_MESSAGE)
            val message = message1.toString()
            onReceiveMessage(message)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}