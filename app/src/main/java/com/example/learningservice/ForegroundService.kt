package com.example.learningservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForegroundService() : Service() {

    private val scope = CoroutineScope(Dispatchers.Main)
//ForegroundService в отличии от обычного Service продолжает свою работу даже когда приложение полностью закрыто
//ForegroundService показывают уведомление в строке состояния, чтобы пользователи были осведомлены
// о том, что ваше приложение выполняет задачу на переднем плане и потребляет системные ресурсы.
//уведомление нельзя отклонить, пока служба не будет остановлена или удалена с переднего плана.
//уведомление должно быть обязательно, если в течении 5 секунд после старта его не передать, приложение упадет
//для ForegroundService необходимо прописать разрешение в манифесте
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        notificationChannel()// передаем канал
        startForeground(NOTIFY_ID_FOREGROUND,notification()) //стартуем ForegroundService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        scope.launch {
            for (i in 0 until 100) {
                delay(1000)
                log("Timer $i")
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
        //создание канала
    private fun notificationChannel() {
//за отображение уведомления отвечает класс notificationManager
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//начиная с 26 API для уведомления необходимо создать канал
            val notificationChannel = NotificationChannel(
                CHANNEL_ID_FOREGROUND,
                CHANNEL_NAME_FOREGROUND,
                NotificationManager.IMPORTANCE_DEFAULT
            )
           notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    //создание уведомления
//Можно присвоить класс Notification и передать только контекст, но тогда необходимо добавлять
//проверку версий, т.к канал нужен только с API 26. NotificationCompat делает проверку сам
    private fun notification():Notification =
        NotificationCompat.Builder(this, CHANNEL_ID_FOREGROUND)
            .setContentTitle("Foreground Title")
            .setContentText("Foreground Text")
            .setSmallIcon(R.drawable.ic_android_black_24dp) //если не поставить иконку, будет краш
            .build()


    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyForegroundService $message")
    }

    companion object {
        private const val NOTIFY_ID_FOREGROUND = 1
        private const val CHANNEL_ID_FOREGROUND = "channel_id_foreground"
        private const val CHANNEL_NAME_FOREGROUND = "channel_name_foreground"

        //чтобы запустить сервис ему нужно передать интент в качестве параметра
        fun newIntent(context: Context): Intent {
            return Intent(context, ForegroundService::class.java)
        }
    }
}