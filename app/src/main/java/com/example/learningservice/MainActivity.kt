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
import androidx.core.content.ContextCompat
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


    private fun initBtn() {
        binding.btnStartService.setOnClickListener {
            startService(MyService.newIntent(this))
            //чтобы запустить сервис ему нужно передать интент в качестве параметра
        }
        binding.btnForegroundService.setOnClickListener {
//startForegroundService доступен с API 26. ContextCompat проверяет уровень API, если он < 26, система стартует startService
            ContextCompat.startForegroundService(this,ForegroundService.newIntent(this))
        }
        binding.btnStopService.setOnClickListener {
            stopService(MyService.newIntent(this)) //остановка сервиса вручную
            Toast.makeText(this,"STOP SERVICE",LENGTH_LONG).show()
        }
        binding.btnIntentService.setOnClickListener {
            ContextCompat.startForegroundService(this,MyIntentService.newIntent(this))
        }
    }
}