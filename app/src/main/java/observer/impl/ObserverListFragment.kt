@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package observer.impl

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import gromiloff.observer.R

/**
 * @param ModelData - обьект данных для формирования списка
 * @param ListItem  - обьект спика
 * @param VM        - тип модели для полноценных обвязок
 *
 * */
abstract class ObserverListFragment<Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>, ModelData, ListItem, VM : ViewModel> : ObserverFragment<VM>(), SwipeRefreshLayout.OnRefreshListener {
    protected var list: RecyclerView? = null
    protected var adapter: Adapter? = null
    protected var swipeRefreshLayout: SwipeRefreshLayout? = null
    protected var empty: View? = null

    @Suppress("UNCHECKED_CAST")
    override var observer = androidx.lifecycle.Observer<Any?> { fillAdapter(it as ModelData) }

    override val layoutId = R.layout.fragment_list

    open val listLayoutId = R.id.utils_list
    open val swipeRefreshLayoutId = R.id.utils_frame
    open val emptyLayoutId = android.R.id.empty

    @MainThread
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.list = view.findViewById(this.listLayoutId)

        // проверка на дибила
        val tmp = view.findViewById<View>(this.swipeRefreshLayoutId)
        if(tmp is SwipeRefreshLayout) {
            this.swipeRefreshLayout = tmp
            this.swipeRefreshLayout?.isEnabled = swipeRefreshSupported()
            this.swipeRefreshLayout?.setOnRefreshListener(this)
        }

        this.empty = view.findViewById(this.emptyLayoutId)
        this.empty?.visibility = View.GONE
    }

    @MainThread
    @CallSuper
    override fun onDestroyView() {
        this.adapter?.unregisterAdapterDataObserver(this.sizeListener)
        super.onDestroyView()
    }

    @MainThread
    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.list?.also {
            installAdapter()
        }
    }

    @MainThread
    @CallSuper
    override fun onRefresh() {
        this.swipeRefreshLayout?.isRefreshing = swipeRefreshSupported()
    }

    @MainThread
    @CallSuper
    protected open fun changeEmptyListState(isEmpty: Boolean) {
        if (isEmpty) {
            this.list?.visibility = View.GONE
            this.empty?.visibility = View.VISIBLE
        } else {
            this.list?.visibility = View.VISIBLE
            this.empty?.visibility = View.GONE
        }
    }

    // по умолчанию для списка установлен LinearLayoutManager
    protected open fun getLayoutManager(c: Context): RecyclerView.LayoutManager = LinearLayoutManager(c)

    // по умолчанию SwipeRefreshLayout деактивирован
    protected open fun swipeRefreshSupported() = false

    // метод создания обьекта списка из обьекта данных
    abstract fun createListItemFrom(item: ModelData): Collection<ListItem>?

    @MainThread
    // метод создания адаптера для списка
    abstract fun createAndGetAdapter() : Adapter

    @AnyThread
    // метод заполнения списка используя список обьектов данных
    abstract fun fillAdapter(data: ModelData?)

    @CallSuper
    @MainThread
    fun installAdapter() {
        this.list?.also {
            this.adapter = createAndGetAdapter()
            this.adapter?.registerAdapterDataObserver(this.sizeListener)

            it.adapter = this.adapter
            it.layoutManager = getLayoutManager(context!!)
        }
    }

    private val sizeListener = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            changeEmptyListState(adapter?.itemCount ?: 0 == 0)
        }
    }
}
