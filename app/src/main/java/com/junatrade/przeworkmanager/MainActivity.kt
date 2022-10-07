package com.junatrade.przeworkmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.work.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submitBT = findViewById<Button>(R.id.submitBT)
        val wm: WorkManager = WorkManager.getInstance(applicationContext)
        val chargeCHB = findViewById<CheckBox>(R.id.chargeCHB)
        val netCHB = findViewById<CheckBox>(R.id.netCHB)
        val batteryCHB = findViewById<CheckBox>(R.id.batteryCHB)

        submitBT.setOnClickListener(){
            var isCharging = false
            var isNetwork = false
            var isBatteryNotLow = false

            if (chargeCHB.isChecked) isCharging = true
            if (netCHB.isChecked) isNetwork = true
            if (batteryCHB.isChecked) isBatteryNotLow = true

            val constraints = Constraints.Builder()
                .setRequiresCharging(isCharging)
                .setRequiredNetworkType(
                    if (isNetwork) NetworkType.CONNECTED
                else NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(isBatteryNotLow)
                .build()

            val workRequest =
                OneTimeWorkRequestBuilder<SyncDataWithServer>()
                .setConstraints(constraints)
                .addTag("MyUniqueWorkRequest")
                .build()

//            wm.enqueue(workRequest)

            wm.beginUniqueWork("MyUniqueWorkRequest",
                    ExistingWorkPolicy.KEEP,
                    workRequest)
                .enqueue()
        }
    }
}

class SyncDataWithServer(context: Context, workParameters: WorkerParameters):
    Worker(context, workParameters){
    override fun doWork(): Result {
        val number: Int = Random(100).nextInt()
        return if (number%2==0){
            Log.d("Tag", "Worker wykonał synchronizację z serwerem")
            Result.success()
        } else{
            Log.d("Tag", "Worker NIE wykonał synchronizacji z serwerem")
            Result.failure()
        }
    }
}