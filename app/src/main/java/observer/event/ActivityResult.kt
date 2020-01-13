@file:Suppress("unused")

package observer.event

import android.content.Intent
import observer.FragmentObserverImpl

open class ActivityResult(val requestCode: Int,
                          val resultCode: Int,
                          val data: Intent?
) : FragmentObserverImpl()