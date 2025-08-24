package com.example.sms_reader

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class SmsReaderPlugin : FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

    private lateinit var channel: MethodChannel
    private lateinit var context: android.content.Context
    private var activity: Activity? = null
    private var pendingResult: MethodChannel.Result? = null
    private val SMS_PERMISSION_REQUEST = 1001

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        channel = MethodChannel(binding.binaryMessenger, "sms_reader_channel")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
            if (requestCode == SMS_PERMISSION_REQUEST && pendingResult != null) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pendingResult?.success(getInboxSms())
                } else {
                    pendingResult?.error("PERMISSION_DENIED", "SMS permission denied", null)
                }
                pendingResult = null
                true
            } else {
                false
            }
        }
    }

    override fun onDetachedFromActivityForConfigChanges() { activity = null }
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) { activity = binding.activity }
    override fun onDetachedFromActivity() { activity = null }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "getInboxSms") {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                pendingResult = result
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.READ_SMS),
                    SMS_PERMISSION_REQUEST
                )
            } else {
                result.success(getInboxSms())
            }
        } else {
            result.notImplemented()
        }
    }

    private fun getInboxSms(): List<Map<String, Any?>> {
        val smsList = mutableListOf<Map<String, Any?>>()
        val cursor: Cursor? = context.contentResolver.query(
            Uri.parse("content://sms/"),
            arrayOf(
                "_id", "thread_id", "address", "date", "body",
                "read", "type", "service_center", "subject", "locked"
            ),
            null, null,
            "date DESC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex("_id")
            val threadIndex = it.getColumnIndex("thread_id")
            val addressIndex = it.getColumnIndex("address")
            val dateIndex = it.getColumnIndex("date")
            val bodyIndex = it.getColumnIndex("body")
            val readIndex = it.getColumnIndex("read")
            val typeIndex = it.getColumnIndex("type")
            val scIndex = it.getColumnIndex("service_center")
            val subjectIndex = it.getColumnIndex("subject")
            val lockedIndex = it.getColumnIndex("locked")

            while (it.moveToNext()) {
                smsList.add(
                    mapOf(
                        "id" to it.getString(idIndex),
                        "threadId" to it.getInt(threadIndex),
                        "address" to it.getString(addressIndex),
                        "date" to it.getString(dateIndex),
                        "body" to it.getString(bodyIndex),
                        "read" to it.getInt(readIndex),
                        "type" to it.getInt(typeIndex),
                        "serviceCenter" to it.getString(scIndex),
                        "subject" to it.getString(subjectIndex),
                        "locked" to if (lockedIndex != -1) it.getInt(lockedIndex) else null
                    )
                )
            }
        }
        return smsList
    }
}
