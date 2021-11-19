package com.example.learningservice

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.learningservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var pageNum = 1 //для очереди сервисов

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
            ContextCompat.startForegroundService(this, ForegroundService.newIntent(this))
        }
        binding.btnStopService.setOnClickListener {
            stopService(MyService.newIntent(this)) //остановка сервиса вручную
            Toast.makeText(this, "STOP SERVICE", LENGTH_LONG).show()
        }
        binding.btnIntentService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyIntentService.newIntent(this))
        }
        binding.btnJobService.setOnClickListener {
            //указываем какой именно сервис нам нужен
            val componentName = ComponentName(this, MyJobService::class.java)

            //этот обьект содержит требования для сервиса, передаем ID и компонент
            val jobInfo = JobInfo.Builder(MyJobService.ID_JOB_SERVICE, componentName)
                .setRequiresCharging(true) // сервис будет работать только если устройство заряжается
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)//будет работать только если есть подключение к Wi-Fi
                //.setPersisted(true) //если необходимо чтобы сервис стартовал после того как устройство было выключено и включено заново
                .build()

                //для работы с очередью необходим интент
            val intent = MyJobService.newIntent(pageNum++)
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))//чтобы запустить данный сервис,
            // необходимо вызвать enqueue, куда передать JobWorkItem с интентом содержащим необходимые параметры
            }
//если запустить несколько сервисов методом jobScheduler.schedule(), то работать будет только последний из них, остальные работы отменятся
            //чтобы реализовать очередь сервисов, необходимо вызвать метод jobScheduler.enqueue(),
        // тогда каждый следующий сервис будет ждать пока предыдущий закончит свое выполнение.
// Если система убьет сервис, то при перезапуске он продолжится с выполнения последнего запущеного сервиса который был прерван
        }//ВСЕ ЭТО РАБОТАЕТ ТОЛЬКО С ВЕРСИИ 26
        binding.btnJobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this,pageNum++)
        }
        binding.btnWorkManager.setOnClickListener {
            val workManager = WorkManager.getInstance(applicationContext)
//2 параметром необходимо передать что делать если производится попытка запустить работу, которая
// уже запущена. Их 4 варианта - APPEND\APPEND_OR_REPLACE\KEEP\REPLACE
//REPLACE - старый воркер будет заменен на новый. KEEP - старый продолжит работу, новый игнорируется.
//APPEND - новый воркер будет положен в очередь, если старый завершится с ошибкой, эта ошибка
// распространится на все сервисы в очереди
//APPEND_OR_REPLACE - новый воркер будет положен в очередь, если старый завершится с ошибкой, будет создана новая цепочка
//--------------------------------------------------------------------------------------------------
//3 параментром принимается OneTimeWorkRequest (если выполнение пройдет успешно, его не надо выполнять заново).
// Он принимает все параметры, а так же ограничения на работу сервиса(зарядка, Wi-Fi и т.д).Необходимо создать фарбичный метод который будет возвращать экземпляр Request
            workManager.enqueueUniqueWork(
                MyWorker.NAME_WORKER,
                ExistingWorkPolicy.APPEND,
                MyWorker.makeRequest(pageNum++)

            )
        }
    }
}