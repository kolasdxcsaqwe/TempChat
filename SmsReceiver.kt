package com.example.tempchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.widget.Toast


class SmsReceiver : BroadcastReceiver()
{
    var method: ((SmsMessage) -> Unit?)? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        val content = StringBuilder()
        val bundle = intent!!.extras
        val format = intent.getStringExtra("format")
        if (bundle != null) {
            val pdus = bundle["pdus"] as Array<Any>?
            for (obj in pdus!!) {
                val message: SmsMessage =
                    SmsMessage.createFromPdu(obj as ByteArray, format)
                val sender = message.getOriginatingAddress()
                content.append(message.getMessageBody())
                val millis = message.getTimestampMillis()
                val status = message.getStatus()

                if(method!=null)
                {
                    method?.invoke(message)
                }
                Toast.makeText(context,content.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }
}
