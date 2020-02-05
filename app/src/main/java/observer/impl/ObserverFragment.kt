@file:Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "unused")
package observer.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import observer.FastObserver
import observer.FragmentObserver
import observer.ParserBaseImpl
import observer.ProtectedObserverListener

abstract class ObserverFragment<T : ViewModel> : Fragment(), ProtectedObserverListener {
    private val consumeKey = System.nanoTime().toString()

    abstract val layoutId : Int
    abstract val modelClass: Class<T>

    protected open val observer : androidx.lifecycle.Observer<Any?> = androidx.lifecycle.Observer {}

    override fun onCreate(savedInstanceState: Bundle?) {
        (getModel() as? LifecycleObserver)?.also { this.lifecycle.addObserver(it) }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (getModel() as? ParserBaseImpl<*>)?.load(savedInstanceState, arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(this.layoutId, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (getModel() as? LiveModel<*>)?.getLiveData()?.observe(this, this.observer)
    }
    
    override fun onStart() {
        super.onStart()
        FragmentObserver.addObserver(this)
    }

    override fun onResume() {
        super.onResume()
        FragmentObserver.addObserver(this)
    }

    override fun onStop() {
        super.onStop()
        FragmentObserver.deleteObserver(this)
    }

    override fun onDestroyView() {
        (getModel() as? LiveModel<*>)?.getLiveData()?.removeObserver(this.observer)
        super.onDestroyView()
    }
    
    override fun onDestroy() {
        (getModel() as? LifecycleObserver)?.also { this.lifecycle.removeObserver(it) }
        FragmentObserver.deleteObserver(this)
        super.onDestroy()
    }

    @CallSuper
    override fun update(o: FastObserver, arg: Any?) {
    }

    override fun consumerClass() = this.consumeKey

    @AnyThread
    fun showDialog(dialog: DialogFragment) {
        activity?.runOnUiThread {
            activity?.supportFragmentManager?.let {
                dialog.show(it, dialog.javaClass.simpleName)
            }
        }
    }

    fun getModel() = getCustomModel(this.modelClass)

    fun <Model : ViewModel> getCustomModel(clazz: Class<Model>) : Model = ViewModelProviders.of(this).get(clazz)
}

interface LiveModel<Data> {
    fun getLiveData() : MutableLiveData<Data?>
}