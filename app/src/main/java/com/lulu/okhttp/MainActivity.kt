package com.lulu.okhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            synRequest()
        }
    }
    private val client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()//1
    public fun synRequest() {
        val request = Request.Builder().url("https://www.baidu.com")
                .get().build()//2
        CoroutineScopeManager.getScope(this).launch {
            text.text = withContext(Dispatchers.IO) {
                val call = client.newCall(request)//3
                val response = call.execute()//4
                response.body()?.string()
            }

        }
    }
}