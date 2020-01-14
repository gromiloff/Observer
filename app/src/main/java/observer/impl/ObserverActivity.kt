@file:Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "unused")

package observer.impl

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import observer.*
import observer.event.OpenScreenWithModelData
import observer.event.PermissionRequest
import observer.event.PermissionResponse
import observer.event.ShowToast
import java.util.*

abstract class ObserverActivity<T : ViewModel> : AppCompatActivity(), ProtectedObserverListener {
    object Const {
        const val permissions = 999
    }

    companion object {
        fun <T : ViewModel> getActivityModel(fragment: Fragment): T? {
            val a = fragment.activity
            return if (a is ObserverActivity<*>) a.getModel() as T else null
        }
    }

    private val stackEventsByResume = Stack<ObserverImpl>()
    protected var checkTime : Long = Long.MIN_VALUE

    abstract val layoutId : Int

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityObserver.addObserver(this)
        super.onCreate(savedInstanceState)
        val l = this.layoutId
        if (l > 0) {
            setContentView(l)
        }

        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.addObserver(model)
        (getModel() as? ParserBaseImpl<*>)?.load(savedInstanceState, intent?.extras)
    }

    override fun onStart() {
        ActivityObserver.addObserver(this)
        super.onStart()
    }

    override fun onResume() {
        ActivityObserver.addObserver(this)
        this.stackEventsByResume.forEach { it.send() }
        this.stackEventsByResume.clear()
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        (getModel() as? ParserFullImpl<*>)?.save(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        (getModel() as? ParserFullImpl<*>)?.load(savedInstanceState)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        ActivityObserver.deleteObserver(this)
    }

    override fun onDestroy() {
        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.removeObserver(model)
        super.onDestroy()
        ActivityObserver.deleteObserver(this)
    }

    override fun update(o: FastObserver, arg: Any?) {
        when (arg) {
            is OpenScreenWithModelData -> arg.fill(this)
            else -> command(arg as ObserverImpl)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Const.permissions -> putToResumeStack(PermissionResponse(permissions, grantResults))
        }
    }

    override fun consumerClass() = toString()

    fun getModel() = ViewModelProviders.of(this).get(EmptyBaseModel.ME, getModelClass())

    @Suppress("UNCHECKED_CAST")
    open fun getModelClass(): Class<T> = EmptyBaseModel::class.java as Class<T>

    @CallSuper
    open fun command(arg: ObserverImpl) {
        when (arg) {
            is PermissionRequest ->
                try {
                    ActivityCompat.requestPermissions(this, arg.permissions, Const.permissions)
                } catch (e: Exception) { PermissionResponse(arg.permissions, null).send() }
            is ShowToast ->
                runOnUiThread {
                    val t = Toast.makeText(this, arg.string(resources), Toast.LENGTH_LONG)
                    t.setGravity(Gravity.CENTER, 0, 0)
                    t.show()
                }
        }
    }

    fun putToResumeStack(obj : ObserverImpl){
        this.stackEventsByResume.push(obj)
    }
}
