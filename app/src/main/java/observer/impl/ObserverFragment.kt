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
   
    abstract fun getModelClass(): Class<T>

    fun getModel() = getCustomModel(getModelClass())

    fun <Model : ViewModel> getCustomModel(clazz: Class<Model>) : Model = ViewModelProviders.of(this).get(clazz)

    abstract val layoutId : Int

    protected open val observer : androidx.lifecycle.Observer<Any?> = androidx.lifecycle.Observer {}

    override fun onCreate(savedInstanceState: Bundle?) {
        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.addObserver(model)
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
        val model = getModel()
        if (model is LifecycleObserver) this.lifecycle.removeObserver(model)
        FragmentObserver.deleteObserver(this)
        super.onDestroy()
    }

    @CallSuper
    override fun update(o: FastObserver, arg: Any?) {
    }

    override fun consumerClass() = toString()

    @AnyThread
    fun showDialog(dialog: DialogFragment) {
        activity?.runOnUiThread {
            if (fragmentManager != null) dialog.show(fragmentManager!!, dialog.javaClass.simpleName)
        }
    }
}

interface LiveModel<Data> {
    fun getLiveData() : MutableLiveData<Data?>
}