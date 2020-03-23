@file:Suppress("unused")

package observer.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import observer.ActivityObserverImpl
import observer.ParserFullImpl

open class OpenScreenWithModelData(private val clz: Class<*>,
                                   private val closeCurrent: Boolean = false,
                                   private val code: Int = -1,
                                   private val parsers: ArrayList<ParserFullImpl<*>> = ArrayList()
) : ActivityObserverImpl() {
    constructor(clz: Class<*>,
                closeCurrent: Boolean = false,
                code: Int = -1,
                single : ParserFullImpl<*>) : this(clz, closeCurrent, code) {
        addParser(single)
    }

    @AnyThread
    fun addParser(parser : ParserFullImpl<*>){
        this.parsers.add(parser)
    }

    @AnyThread
    fun addParsers(parser : Collection<ParserFullImpl<*>>){
        this.parsers.addAll(parser)
    }

    @MainThread
    fun fill(activity: Activity) {
        var bundle : Bundle? = null
        this.parsers.forEach { bundle = it.save(bundle) }

        activity.startActivityForResult(Intent(activity, this.clz).apply { bundle?.let { this.putExtras(it) } }, this.code)
        if (this.closeCurrent) activity.finish()
    }
}