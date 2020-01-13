@file:Suppress("unused")

package observer.event

import observer.ApplicationObserverImpl

class TryException(val e: Throwable) : ApplicationObserverImpl()