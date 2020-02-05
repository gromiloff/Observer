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

    @AnyThread
    fun addParser(parser : ParserFullImpl<*>){
        this.parsers.add(parser)
    }

    @MainThread
    fun fill(activity: Activity) {
        var bundle : Bundle? = null
        this.parsers.forEach { bundle = it.save(bundle) }

        val i = Intent(activity, this.clz)
        bundle?.let { i.putExtras(it) }

        activity.startActivityForResult(i, this.code)
        if (this.closeCurrent) activity.finish()
    }
}