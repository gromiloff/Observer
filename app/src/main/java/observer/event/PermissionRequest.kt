@file:Suppress("unused")

package observer.event

import observer.ActivityObserverImpl

class PermissionRequest(val permissions: Array<String>) : ActivityObserverImpl() {
    constructor(permission: String) : this(arrayOf(permission))
}