package com.example.tempchat

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(checkPermission(true))
        {
            readSMS();
        }

        val smsFilter = IntentFilter()
        smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        smsFilter.addAction("android.provider.Telephony.SMS_DELIVER")
        val smsReceiver = SmsReceiver()
        smsReceiver.method={
            if(it!=null)
            {
                val text=TextView(this)
                text.layoutParams=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                text.setTextColor(Color.BLACK)
                text.setPadding(0,50,0,0)
                text.text = "${it.originatingAddress}  ${it.messageBody}  "
                llMain.addView(text)
            }
            else
            {
                readSMS()
            }
        }
        registerReceiver(SmsReceiver(), smsFilter)
    }

    fun checkPermission(request:Boolean):Boolean
    {
        val hasReadSMS=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED
        val hasReceivedSMS=ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)== PackageManager.PERMISSION_GRANTED
        if(!hasReadSMS && request)
        {
            //reject
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_SMS
                ),
                999
            )
        }

        if(!hasReceivedSMS && request)
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECEIVE_SMS
                ),
                999
            )
        }

        return hasReadSMS && hasReceivedSMS
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==999)
        {
            if(checkPermission(false))
            {
                readSMS()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun readSMS()
    {
        llMain.removeAllViews()
        val SMS_INBOX: Uri = Uri.parse("content://sms/inbox")
        val cr = contentResolver
        val projection =
            arrayOf("_id", "address", "person", "body", "date", "type")
        val cur: Cursor? = cr.query(SMS_INBOX, projection, null, null, "date desc")
        if (null != cur) {
            if(cur.moveToFirst())
            {
                while (cur.moveToNext()) {
                    val number: String = cur.getString(cur.getColumnIndex("address")) //手机号
//                val name: String = cur.getString(cur.getColumnIndex("person")) //联系人姓名列表
                    val body: String = cur.getString(cur.getColumnIndex("body")) //短信内容

                    val text=TextView(this)
                    text.layoutParams=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    text.setTextColor(Color.BLACK)
                    text.setPadding(0,50,0,0)
                    text.text = "$number  $body"
                    llMain.addView(text)
                }
            }
        }

    }
}
