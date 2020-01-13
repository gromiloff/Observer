@file:Suppress("unused")

package observer.event

import observer.ApplicationObserverImpl

open class HttpMetricStart(val url: String) : ApplicationObserverImpl()