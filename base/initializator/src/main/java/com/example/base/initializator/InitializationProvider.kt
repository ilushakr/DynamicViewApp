package com.example.base.initializator

import android.content.ComponentName
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Looper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InitializationProvider : ContentProvider() {

    private val valueInitialization by lazy {
        context!!.getString(R.string.value_initialization)
    }

    override fun onCreate(): Boolean {
        initApp(context!!)
        if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
            "main"
        }
        else{
            "not main"
        }
        return true
    }

    private fun initApp(context: Context) {

        val initializersSet = getInitializersSet(context)

        startKoin {
            androidLogger()
            androidContext(context)

            initializersSet.forEach {
                it.create(context, ::modules)
            }
        }.koin

    }

    private fun getInitializersSet(context: Context): Set<AbstractInitializer> {
        val provider = ComponentName(context.packageName, InitializationProvider::class.java.name)
        val providerInfo =
            context.packageManager.getProviderInfo(provider, PackageManager.GET_META_DATA)
        val metaData = providerInfo.metaData

        val initializersSet = mutableSetOf<AbstractInitializer>()

        fun <T> setClass(className: String) {
            (Class.forName(className) as? Class<out T>)?.let {
                val instance = it.getDeclaredConstructor().newInstance()
                (initializersSet as? MutableSet<T>)?.add(instance)
            }
        }

        metaData.keySet().forEach { className ->
            val value = metaData.getString(className, null)
            when (value) {
                valueInitialization -> {
                    setClass<AbstractInitializer>(className)
                }
            }
        }

        return initializersSet
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        throw IllegalStateException(METHOD_NOT_ALLOWED)
    }

    override fun getType(p0: Uri): String? {
        throw IllegalStateException(METHOD_NOT_ALLOWED)
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        throw IllegalStateException(METHOD_NOT_ALLOWED)
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        throw IllegalStateException(METHOD_NOT_ALLOWED)
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        throw IllegalStateException(METHOD_NOT_ALLOWED)
    }

    companion object {
        private const val METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED"
    }
}