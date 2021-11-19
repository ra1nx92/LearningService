package com.example.learningservice

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.work.*
//В какой то момент работа с сервисами стала не очень удобной, из за различных ограничений на сервисы,
// и из за того что они по разному себя ведут на разных версиях API, был создан класс WorkManager
//для его создания необходимо унаследоваться от класса Worker, и передать ему контекст, и workerParameters
//вся работа выполняется в методе doWork, работа идет в отдельном потоке
class MyWorker(context: Context,private val workerParameters: WorkerParameters):Worker(context,workerParameters) {
//Result принимает одно из 3 значений Result.success() Result.failure() Result.retry()
//Result.success() - работа прошла успешно, сервис завершил свою работу.
//Result.failure() - что то пошло не так, метод завершился с исключением, сервис перезапущен не будет
//Result.retry() - что то пошло не так, метод завершился с исключением, сервис будет перезапущен
    override fun doWork(): Result {
        log("doWork")
//различные обьекты в воркер передаются при помощи workerParameters
        val page = workerParameters.inputData.getInt(PAGE,0)
        for (i in 1 until 10) {
            Thread.sleep(1000)
            log("Timer $i Page $page")
        }
        return Result.success()
    }
    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyWorker $message")
    }

    override fun onStopped() {
        super.onStopped()
        log("onStopped")
    }

    companion object{
        const val PAGE = "page"
        const val NAME_WORKER = "my_worker"

        fun makeRequest(page: Int):OneTimeWorkRequest{
            return OneTimeWorkRequestBuilder<MyWorker>().apply {
                setInputData(workDataOf(PAGE to page))//передаем ключ и значение
                setConstraints(makeConstraints())//передаем ограничения для сервиса
            }
                .build()
        }
        //создаем ограничение для сервиса(работает только на зарядке)
        private fun makeConstraints():Constraints{
            return Constraints.Builder()
                .setRequiresCharging(true)
                .build()
        }
    }
}