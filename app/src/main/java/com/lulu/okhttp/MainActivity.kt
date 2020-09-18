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
import java.io.File
import java.io.IOException
import java.lang.StringBuilder
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
        obtainFileDir.setOnClickListener {
            obtainCacheFile()
        }
        cacheButton.setOnClickListener {
            cacheRequest()
        }
        clearLog.setOnClickListener {
            responseText.text = "无"
            logText.text = "无"
            stringBuilder.clear()
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
            responseText.text = withContext(Dispatchers.IO) {
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
        call.enqueue(object : Callback {
            //4
            override fun onFailure(call: Call, e: IOException) {
                GlobalScope.launch(Dispatchers.Main) {
                    responseText.text = "失败：${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse Thread: ${Thread.currentThread().name}")
                val result = response.body()?.string()
                GlobalScope.launch(Dispatchers.Main) {
                    responseText.text = result
                }
            }
        })
    }

    private fun cacheRequest() {
        val cacheFile = File(externalCacheDir, "okHttpCacheFile")
        val client = OkHttpClient.Builder()
            .cache(Cache(cacheFile, 1024 * 1024 * 10))//指定缓存目录 缓存最大容量（10M）
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder().url("https://www.baidu.com")
            .cacheControl(
                CacheControl.Builder()
                    //设置max-age为5分钟之后，这5分钟之内不管有没有网, 都读缓存
                    .maxAge(5, TimeUnit.MINUTES)
                    // max-stale设置为5天，意思是，网络未连接的情况下设置缓存时间为5天
                    .maxStale(5, TimeUnit.DAYS)
                    .build()
            )
            .get().build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                GlobalScope.launch(Dispatchers.Main) {
                    responseText.text = "失败：${e.message}"
                }
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse Thread: ${Thread.currentThread().name}")
                val result = response.body()?.string()
                GlobalScope.launch(Dispatchers.Main) {
                    responseText.text = result
                    printLog(response.cacheResponse()?.toString() ?: "cacheResponse 为空")
                }
            }
        })
    }

    private fun obtainCacheFile(file: File? = externalCacheDir, spaceStr: String = "") {
        if (file == null) return
        if (file.isDirectory) {
            printFileName(file, spaceStr)
            for (file in file.listFiles()) {
                obtainCacheFile(file, "$spaceStr   ")
            }
        } else {
            printFileName(file, spaceStr)
        }
    }

    private fun printFileName(file: File, spaceStr: String) {
        printLog("$spaceStr| ${file.name} ${fileSize(file)}")
    }

    private fun fileSize(file: File): String {
        if (file.isFile) {
            val size = file.length()
            return when {
                size < 1024 -> {
                    "$size B"
                }
                size < 1024 * 1024 -> {
                    "${size / 1024} KB"
                }
                size < 1024 * 1024 * 1024 -> {
                    "${size / 1024 / 1024} MB"
                }
                else -> {
                    "${size / 1024 / 1024 / 1024} GB"
                }
            }
        }
        return ""
    }

    /**日志打印**/
    private val stringBuilder = StringBuilder()
    private fun printLog(log: String) {
        stringBuilder.append(log).append("\n")
        logText.text = stringBuilder.toString()
    }


}