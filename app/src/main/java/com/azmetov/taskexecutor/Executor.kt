package com.azmetov.taskexecutor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import kotlin.random.Random

object Singleton {
    val executor = Executor()
}

class MyTask : Task<String> {
    override fun executeTask(): String {
        Thread.sleep(5_000)
        return "${Random(100)}"
    }
}

interface Task<DATA> {
    fun executeTask(): DATA
}

interface Observer<DATA> {
    fun onLoading()
    fun onSuccess(data: DATA)
    fun onError()
}

class Executor {

    private val tasks = HashMap<String, Task<*>>()
    private val observers = HashMap<String, Observer<*>>()
    private val handler = Handler(Looper.getMainLooper())
    private val pool = Executors.newFixedThreadPool(5)

    fun <DATA> subscribe(o: Observer<DATA>, key: String) {
        observers[key] = o
    }

    fun unsubscribe(key: String) {
        observers.remove(key)
    }

    fun <DATA> execute(task: Task<DATA>, key: String) {
        val existedTask = tasks[key]
        if (existedTask != null) {
            return
        }
        tasks[key] = task
        val observer = observers[key] as? Observer<DATA>
        handler.post { observer?.onLoading() }
        pool.execute {
            try {
                val result = task.executeTask()
                val currentObserver = observers[key] as? Observer<DATA>
                handler.post { currentObserver?.onSuccess(result) }
            } catch (e: Exception) {
                val currentObserver = observers[key] as? Observer<DATA>
                handler.post { currentObserver?.onError() }
            }
        }
    }
}