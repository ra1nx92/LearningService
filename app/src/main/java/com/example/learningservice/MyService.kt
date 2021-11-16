package com.example.learningservice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
//Сервисы предназначены для выполнения каких либо задач в фоне. Чтобы создать сервис, нужно
// унаследоваться от класса Service. У него 3 метода ЖЦ. По умолчанию сервис работает на главном потоке
//сервис как одна из основных сущностей системы Андроид, должна быть зарегестрирована в манифесте
class MyService():Service(){

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        scope.launch {
            for(i in 0 until 100){
                delay(1000)
                log("Timer $i")
            }
        }
//возвращение START_STICKY запустит работу сервиса заново, если система его уничтожит, но без стартового интента
// START_NOT_STICKY не будет запускать сервис после уничтожения
//START_REDELIVER_INTENT запустит сервис заново, вместе с интентом который был использован при запуске сервиса
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        log("onDestroy")
        //stopService(newIntent(this))
    }
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    private fun log(message: String){
        Log.d("SERVICE_TAG", "MyService $message")
    }
//чтобы запустить сервис ему нужно передать интент в качестве параметра
    companion object{
        fun newIntent(context: Context):Intent{
            return Intent(context,MyService::class.java)
        }
    }
}