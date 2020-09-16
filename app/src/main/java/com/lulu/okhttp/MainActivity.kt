package com.lulu.okhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        syncButton.setOnClickListener {
            synRequest()
        }
        asyncButton.setOnClickListener {
            asyncRequest()
        }
    }


    /**
     * 同步请求
     */
    private fun synRequest() {
        val client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()//1
        val request = Request.Builder().url("https://www.baidu.com")
                .get().build()//2
        GlobalScope.launch(Dispatchers.Main) {
            text.text = withContext(Dispatchers.IO) {
                val call = client.newCall(request)//3
                val response = call.execute()//4
                response.body()?.string()
            }
        }
    }

    /**
     * 异步请求
     */
    private fun asyncRequest() {
        val client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()//1
        val request = Request.Builder().url("https://www.baidu.com")
            .get().build()//2
        val call = client.newCall(request)//3
        call.enqueue(object : Callback {//4
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse Thread: ${Thread.currentThread().name}")
                val result = response.body()?.string()
                GlobalScope.launch(Dispatchers.Main) {
                    text.text = result
                }
            }
        })
    }
}