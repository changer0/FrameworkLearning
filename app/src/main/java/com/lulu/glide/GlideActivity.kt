package com.lulu.glide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lulu.okhttp.R
import kotlinx.android.synthetic.main.activity_glide.*

private const val TAG = "GlideActivity"
class GlideActivity : AppCompatActivity() {
    companion object {
        const val imgUrl = "https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200922145933.png"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide)

        reloadBtn.setOnClickListener {
            reload()
        }
        reload()
    }

    private fun reload() {
        loadImageSample()
        loadImageWithArgument()
    }

    /**至少包含三个配置项**/
    private fun loadImageSample() {
        Glide.with(this)
            .load(imgUrl)
            .into(imageView1)
    }
    /**带有其他参数**/
    private fun loadImageWithArgument() {
        Glide.with(this) //指定 Context
            .load(imgUrl) // 指定图片资源 网络 url、File、二进制流等
            .placeholder(R.color.colorGlidePlaceHolder) // 指定图片未加载成功前显示的 Drawable
            .error(R.color.colorGlideError) // 指定图片加载失败时显示的 Drawable
            .override(500, 500) // 指定图片的尺寸
            .fitCenter() // 图片缩放类型 fitCenter
            .centerCrop() // 图片缩放类型 centerCrop
            .skipMemoryCache(true) // 跳过内存缓存
            .crossFade(1000) // 设置渐变显示时间
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 跳过磁盘缓存
            .diskCacheStrategy(DiskCacheStrategy.SOURCE) // 仅仅只缓存原来的全分辨率的图像
            .diskCacheStrategy(DiskCacheStrategy.RESULT) // 仅仅缓存最终的图像
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存所有版本的图像
            .priority(Priority.HIGH) // 指定优先级 Glide 会尽可能的处理这些请求，但并不能 100% 保证优先展示
            .into(imageView2)
    }

}