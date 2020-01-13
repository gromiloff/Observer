@file:Suppress("unused")

package observer.event

import observer.ActivityAndFragmentObserverImpl

/* если status = null значит это системная ошибка */
class PermissionResponse(val permission: Array<String>, val status: IntArray? = null) : ActivityAndFragmentObserverImpl()