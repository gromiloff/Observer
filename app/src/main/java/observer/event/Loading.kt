@file:Suppress("unused")

package observer.event

import observer.ActivityObserverImpl

class Loading {
    companion object {
        fun start() {
            LoadingStart().send()
        }
        fun stop() {
            LoadingStop().send()
        }
    }
}

class LoadingStart : ActivityObserverImpl()
class LoadingStop(val asGone : Boolean = false) : ActivityObserverImpl()