@file:Suppress("unused")

package observer.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    companion object {
        var DEBUG = false
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
        val sb = StringBuilder()
                .append("try start new activity\n")
                .append("Activity class [${clz.canonicalName}]\n")
                .append("Activity for code [$code]\n")
                .append((if(closeCurrent) "Close current activity" else "No close current activity\n"))
                .append("Parsers (count=${this.parsers.size}) [")

        this.parsers.forEach {
            sb.append(it).append("\n")
            bundle = it.save(bundle)
        }

        if(DEBUG) Log.d(this::class.java.name, sb.append("]").toString())

        activity.startActivityForResult(Intent(activity, this.clz).apply { bundle?.let { this.putExtras(it) } }, this.code)
        if (this.closeCurrent) activity.finish()
    }
}