package io.github.kartoffelsup.nuntius.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import io.github.kartoffelsup.nuntius.data.Login
import io.github.kartoffelsup.nuntius.data.Logout
import io.github.kartoffelsup.nuntius.data.Security
import io.github.kartoffelsup.nuntius.data.user.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    private lateinit var appState: AppState
    private val navigationViewModel by viewModels<NavigationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        Security.init(applicationContext)

        appState = AppState(Security.getUser())

        setContent {
            NuntiusApp(appState, navigationViewModel)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    fun onLogin(login: Login) {
        appState.userData = Security.getUser()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    fun onLogout(logout: Logout) {
        appState.userData = Security.getUser()
    }

    override fun onBackPressed() {
        if (!navigationViewModel.onBack()) {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        Security.destroy()
    }
}
