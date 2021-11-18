package com.example.learningservice

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import kotlinx.coroutines.*

//на работу JobService можно устанавливать ограничения, к примеру можно установить чтобы сервис
// работал только при наличии Wi-Fi, или же когда телефон стоит на зарядке
class MyJobService : JobService() {
    private val scope = CoroutineScope(Dispatchers.Main)


    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

//как и в обычном сервисе, код выполняется на главном потоке. Вызывается при старте сервиса
//возвращаемый тип Boolean обозначает выполняется работа или нет. При работе с асинхронными
// операциями необходимо возвращать true, таким образом сообщаем системе что сервис еще выполняется,
//и мы сами завершим работу когда это будет необходимо. При выполнении синхронной работы, возвращаем
// false, т.к сервис сам завершит свою работу к тому моменту
    override fun onStartJob(p0: JobParameters?): Boolean {
        log("onStartJob")
        scope.launch {
            for (i in 0 until 100) {
                delay(1000)
                log("Timer $i")
            }
//первый параметр прилетает в сам метод, второй отвечает за необходимость выполнения сервиса заново,
// если поставить true сервис запустится заново через какое то время
            jobFinished(p0,false)
        }
        return true
    }
//метод вызывается если сервис был остановлен, в связи с указанными ограничениями
// (отключили от зарядки, пропал Wi-Fi). Если в onStartJob был вызван метод jobFinished, то
// onStopJob не будет вызван. Он вызывается только если система сама остановила сервис
//Вовзращаемый тип отвечает за то, будет ли возобновлена работа сервиса после того как система его убила
    override fun onStopJob(p0: JobParameters?): Boolean {
    log("onStopJob")
    return true
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        log("onDestroy")
        //stopService(newIntent(this))
    }


    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyJobService $message")
    }

    companion object{
        const val ID_JOB_SERVICE = 1
    }
}