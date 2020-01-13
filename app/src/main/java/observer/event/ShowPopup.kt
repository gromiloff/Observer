@file:Suppress("unused")

package observer.event

open class ShowPopup(val title : String? = null, message: Any) : ShowToast(message)