@file:Suppress("unused")

package observer.event

import observer.ActivityAndFragmentObserverImpl

open class OperationFailed(val msg : String? = null) : ActivityAndFragmentObserverImpl()