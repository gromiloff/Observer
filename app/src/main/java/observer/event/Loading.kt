@file:Suppress("unused")

package observer.event

import observer.ActivityAndFragmentObserverImpl

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

class LoadingStart : ActivityAndFragmentObserverImpl()
class LoadingStop(val asGone : Boolean = false) : ActivityAndFragmentObserverImpl()