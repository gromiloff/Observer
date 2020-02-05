@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package observer.impl

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import observer.ApplicationObserver
import observer.FastObserver
import observer.ProtectedObserverListener
import gromiloff.observer.R
import observer.event.ShowToast

open class ObserverApplication : Application(), ProtectedObserverListener {

    override fun update(o: FastObserver, arg: Any?) {}
    override fun consumerClass() = toString()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ApplicationObserver.addObserver(this)
    }
}

/* получение референса на реализацию класса Application */
object Instance {
    fun <App : ObserverApplication> from(context: Context) : App = context.applicationContext as App

    fun <App : ObserverApplication> from(src: View) : App = from(src.context)

    fun from(src: View): FragmentActivity? {
        var context = src.context
        while (context is ContextWrapper) {
            if (context is FragmentActivity) { return context }
            context = context.baseContext
        }
        return null
    }

    fun <T : ViewModel, GodObject : ObserverActivity<T>> baseFeatureActivityFrom(view: View) = from(view) as GodObject
}

object Open {
    fun dial(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            ShowToast(R.string.no_application_for_call).send()
        } catch (e: Exception) {
            ShowToast(R.string.utils_error).send()
        }
    }

    fun mailto(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            ShowToast(R.string.no_application_for_mail).send()
        } catch (e: Exception) {
            ShowToast(R.string.utils_error).send()
        }
    }


    fun view(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            ShowToast(R.string.no_application_for_view).send()
        } catch (e: Exception) {
            ShowToast(R.string.utils_error).send()
        }
    }

    @Suppress("SpellCheckingInspection")
    fun overrideWebViewRequest(context: Context, url : String) = when {
        url.contains("mailto:") -> {
            mailto(context, url)
            true
        }
        url.contains("tel:") -> {
            dial(context, url)
            true
        }
        url.contains("sms:") || url.contains("smsto:") || url.contains("mmsto:") -> {
            view(context, url)
            true
        }
        else -> false
    }
}