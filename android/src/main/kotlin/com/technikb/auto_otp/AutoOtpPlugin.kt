package com.technikb.auto_otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

/** AutoOtpPlugin */
class AutoOtpPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var channelResult: MethodChannel.Result? = null
    private var smsRetrieverBroadcastReceiver: SmsBroadcastReceiver? = null
    private lateinit var context: Context

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.technikb.auto_otp")
        context = flutterPluginBinding.applicationContext
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getSmsCode" -> startSmsRetriever(result)
            "removeSmsListener" -> stopSmsRetriever(result)
            "getAppSignature" -> getSignature(result)
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun getSignature(result: MethodChannel.Result) {
        val signatures = AppSignatureHelper(context).appSignatures
        result.success(signatures.getOrNull(0))
    }

    private fun startSmsRetriever(result: MethodChannel.Result) {
        removeSmsRetrieverListener()
        channelResult = result

        val task = SmsRetriever.getClient(context).startSmsRetriever()
        task.addOnSuccessListener {
            smsRetrieverBroadcastReceiver = SmsBroadcastReceiver()
            val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            ContextCompat.registerReceiver(
                context,
                smsRetrieverBroadcastReceiver,
                intentFilter,
                SmsRetriever.SEND_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )
        }
        task.addOnFailureListener {
            channelResult?.success(null)
        }
    }

    private fun stopSmsRetriever(result: MethodChannel.Result) {
        if (smsRetrieverBroadcastReceiver == null) {
            result.success(false)
        } else {
            removeSmsRetrieverListener()
            result.success(true)
        }
    }

    private fun removeSmsRetrieverListener() {
        unregisterReceiver(smsRetrieverBroadcastReceiver)
        smsRetrieverBroadcastReceiver = null
    }

    private fun unregisterReceiver(receiver: BroadcastReceiver?) {
        try {
            receiver?.let { context.unregisterReceiver(it) }
        } catch (exception: Exception) {
            Log.e(PLUGIN_TAG, "Unregistering receiver failed.", exception)
        }
    }

    private fun ignoreIllegalState(fn: () -> Unit) {
        try {
            fn()
        } catch (e: IllegalStateException) {
            Log.e(PLUGIN_TAG, "ignoring exception: $e")
        }
    }

    /**
     * SMS Retriever API
     * [https://developers.google.com/identity/sms-retriever/overview]
     */
    inner class SmsBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                removeSmsRetrieverListener()
                if (intent.extras != null && intent.extras!!.containsKey(SmsRetriever.EXTRA_STATUS)) {
                    val extras = intent.extras!!
                    val smsRetrieverStatus = extras.get(SmsRetriever.EXTRA_STATUS) as Status

                    when (smsRetrieverStatus.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val smsContent = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                            if (smsContent != null) {
                                ignoreIllegalState { channelResult?.success(smsContent) }
                            } else {
                                Log.e(
                                    PLUGIN_TAG,
                                    "Retrieved SMS is null, check if SMS contains correct app signature"
                                )
                                ignoreIllegalState { channelResult?.success(null) }
                            }
                        }

                        CommonStatusCodes.TIMEOUT -> {
                            Log.e(
                                PLUGIN_TAG,
                                "SMS Retriever API timed out, check if SMS contains correct app signature"
                            )
                            ignoreIllegalState { channelResult?.success(null) }
                        }

                        else -> {
                            Log.e(
                                PLUGIN_TAG,
                                "SMS Retriever API failed with status code: ${smsRetrieverStatus.statusCode}, check if SMS contains correct app signature"
                            )
                            ignoreIllegalState { channelResult?.success(null) }
                        }
                    }
                } else {
                    Log.e(
                        PLUGIN_TAG,
                        "SMS Retriever API failed with no status code, check if SMS contains correct app signature"
                    )
                    ignoreIllegalState { channelResult?.success(null) }
                }
            }
        }
    }

    companion object {
        private const val PLUGIN_TAG = "AutoOtpPlugin"
    }
}
