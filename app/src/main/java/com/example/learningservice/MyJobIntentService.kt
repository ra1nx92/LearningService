package com.example.learningservice

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.JobIntentService

//JobIntentService под капотом использует 2 вида сервисов, если API ниже 26 то будет запущен
// IntentService, если выше то JobService
//Для запуска необходим метод enqueueWork()
class MyJobIntentService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }


    override fun onHandleWork(intent: Intent) {
        log("onHandleWork")
        val page = intent.getIntExtra(PAGE,0)
        for (i in 1 until 10) {
            Thread.sleep(1000)
            log("Timer $i Page $page")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        Toast.makeText(this, "STOP SERVICE", Toast.LENGTH_SHORT).show()
    }


    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyJobIntentService $message")
    }

    companion object {
        const val JOB_ID = 1
        const val PAGE = "page"

        fun enqueue(context: Context, page: Int) {
            JobIntentService.enqueueWork(
                context,
                MyJobIntentService::class.java,
                JOB_ID,
                intent(context,page)
            )
        }
        private fun intent(context: Context, page: Int): Intent {
            return Intent(context, MyJobIntentService::class.java).apply {
                putExtra(PAGE, page)
            }
        }
    }
}