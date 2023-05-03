package com.example.cliente

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        enviarTokenAServidor(token)
    }
    private fun enviarTokenAServidor(token: String) {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.0.5:3000/registerToken")
            .post("{\"token\":\"$token\"}".toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).execute()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Mensaje recibido de: ${remoteMessage.from}")

        // Crear una notificación para el mensaje recibido
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setSmallIcon(R.drawable.ic_launcher_background)

        // Mostrar la notificación
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
        private const val CHANNEL_ID = "my_channel_id"
    }
}
