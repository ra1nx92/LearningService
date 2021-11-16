package com.example.learningservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.learningservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnStartService.setOnClickListener{
            startService(MyService.newIntent(this))
            //чтобы запустить сервис ему нужно передать интент в качестве параметра
        }
    }
}