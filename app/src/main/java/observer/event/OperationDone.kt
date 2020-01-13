@file:Suppress("unused")

package observer.event

import observer.ActivityAndFragmentObserverImpl

open class OperationDone(val obj : Any? = null) : ActivityAndFragmentObserverImpl()