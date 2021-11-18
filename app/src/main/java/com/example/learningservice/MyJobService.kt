package com.example.learningservice

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.os.PersistableBundle
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
//--------------------------------------------------------------------------------------------------
//в этой ветке указан пример работы с очередью сервисов, работает она с 26 АПИ 8 версии Андроид
    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob")
        scope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var workItem = params?.dequeueWork() //из параметров получаем workItem, таким образом из очереди будет взят первый сервис
                while (workItem !== null) { //данный код будет выполняться пока в очереди есть еще какие то обьекты
                    val page = workItem.intent.getIntExtra(PAGE_NUM, 0)//получаем значение страницы из интента
                    for (i in 0..5) {
                        delay(1000)
                        log("Timer $i Page $page")
                    }
                    params?.completeWork(workItem) //завершаем работу элемента из очереди
                    workItem = params?.dequeueWork()//достаем следующий элемент из очереди
                }
            }
//первый параметр прилетает в сам метод, второй отвечает за необходимость выполнения сервиса заново,
// если поставить true сервис запустится заново через какое то время
            jobFinished(params, false) //после того как все обьекты в очереди закончаться, сервис прекратит свою работу
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

    companion object {
        const val ID_JOB_SERVICE = 1
        const val PAGE_NUM = "page"

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE_NUM, page)
            }
        }
    }
}