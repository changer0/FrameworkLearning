package com.lulu.glide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.lulu.okhttp.R
import kotlinx.android.synthetic.main.activity_glide.*

private const val TAG = "GlideActivity"
class GlideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide)
        Glide.with(this)
            .load("https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200922145933.png")
            .into(imageView1)
    }

}