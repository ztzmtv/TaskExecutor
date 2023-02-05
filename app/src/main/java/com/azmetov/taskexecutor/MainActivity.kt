package com.azmetov.taskexecutor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val textView by lazy { findViewById<TextView>(R.id.text_vew) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Singleton.executor.subscribe(object : Observer<String> {
            override fun onLoading() {
                Log.d("TTT", "onLoading()")
            }

            override fun onError() {
                Log.d("TTT", "onError()")
            }

            override fun onSuccess(data: String) {
                textView.text = "success! $data"
                Log.d("TTT", "onSuccess() = $data")
            }
        }, "key")

        textView.setOnClickListener {
            Singleton.executor.execute(MyTask(), "key")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Singleton.executor.unsubscribe("key")
    }
}