package com.example.learningservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.learningservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBtn()
    }


// начиная с API 26 Android 8 необходимо уведомлять пользователя о фоновой работе сервиса.
// На протяжении всей его работы должно висеть уведомление
//метод для показа уведомления о фоновой работе сервиса
    private fun showNotification() {
    //за отображение уведомления отвечает класс notificationManager
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //начиная с 26 API для уведомления необходимо создать канал
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //создание уведомления
//Можно присвоить класс Notification и передать только контекст, но тогда необходимо добавлять
//проверку версий, т.к канал нужен только с API 26. NotificationCompat делает проверку сам
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_android_black_24dp) //если не поставить иконку, будет краш
            .build()

        notificationManager.notify(1, notification)
    }

    private fun initBtn() {
        binding.btnStartService.setOnClickListener {
            startService(MyService.newIntent(this))
            //чтобы запустить сервис ему нужно передать интент в качестве параметра
            showNotification()
        }
        binding.btnForegroundService.setOnClickListener {
            showNotification()
        }
        binding.btnStopService.setOnClickListener {
            stopService(MyService.newIntent(this))
            Toast.makeText(this,"STOP SERVICE",LENGTH_LONG).show()
        }
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
    }
}