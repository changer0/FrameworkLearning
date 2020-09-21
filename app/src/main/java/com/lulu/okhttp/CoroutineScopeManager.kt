package com.lulu.okhttp

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

/**
 * @author zhanglulu on 2020/7/1.
 * for 协程 Scope 管理类，防止内存泄漏 Lifecycle ON_DESTROY 态自动销毁
 */
private const val TAG = "CoroutineScopeManager"
object CoroutineScopeManager {
    private val scopeMap = mutableMapOf<Lifecycle, CoroutineScope>()

    /**
     * 获取 CoroutineScope
     */
    public fun getScope(lifecycle: Lifecycle): CoroutineScope {
        var scope = scopeMap[lifecycle]
        if (scope != null) {
            return scope
        }
        scope = CoroutineScope(Dispatchers.Main)
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                //当前 Activity 销毁了
                scope.cancel()
                scopeMap.remove(lifecycle)
            }
        })
        scopeMap[lifecycle] = scope
        return scope
    }

    /**
     * 获取 CoroutineScope
     */
    public fun getScope(owner: LifecycleOwner): CoroutineScope {
        return getScope(owner.lifecycle)
    }
}