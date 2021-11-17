package com.example.learningservice

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyIntentService : IntentService(NAME_INTENT_SERVICE) {
//MyIntentService решет проблемы, работает не на главном потоке, сам завершает работу по окончанию
// не дает запустить один и тот же сервис несколько раз одновреенно, каждый запуск будет работать
// последовательно(5 раз нажал, 5 раз запуститься последовательно)

    override fun onCreate() {
        super.onCreate()
        setIntentRedelivery(true) //метод запустит сервис заново, если он будет убит системой,
        // при этом интент который прилетает в onHandleIntent будет сохранен если указать true
        log("onCreate")
        notificationChannel()// передаем канал
        startForeground(NOTIFY_ID_FOREGROUND, notification())
    }

//Вместо метода OnStartCommand, здесь переопределяется метод onHandleIntent
// код этого метода будет выполняться в другом потоке, и главный поток заблокирован не будет
//после выполнения кода, сервис будет остановлен автоматически
    override fun onHandleIntent(p0: Intent?) {
        log("onHandleIntent")
        for (i in 1 until 10) {
            Thread.sleep(1000)
            log("Timer $i")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        Toast.makeText(this, "STOP SERVICE", Toast.LENGTH_SHORT).show()
    }

    private fun notificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID_FOREGROUND,
                CHANNEL_NAME_FOREGROUND,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun notification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID_FOREGROUND)
            .setContentTitle("Foreground Title")
            .setContentText("Foreground Text")
            .setSmallIcon(R.drawable.ic_android_black_24dp) //если не поставить иконку, будет краш
            .build()

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyIntentService $message")
    }

    companion object {
        private const val NOTIFY_ID_FOREGROUND = 1
        private const val CHANNEL_ID_FOREGROUND = "channel_id_foreground"
        private const val CHANNEL_NAME_FOREGROUND = "channel_name_foreground"
        private const val NAME_INTENT_SERVICE = "name_intent_service"

        fun newIntent(context: Context): Intent {
            return Intent(context,MyIntentService::class.java)
        }
    }

}