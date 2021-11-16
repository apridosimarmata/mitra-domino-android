package id.infinion.chipdomino

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import id.infinion.Auth
import id.infinion.chipdomino.databinding.ActivitySplashBinding
import id.infinion.models.AuthenticationResponse
import id.infinion.remote.AppApiService
import id.infinion.remote.ServiceBuilderApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.lang.Exception

class SplashActivity : AppCompatActivity() {

    private lateinit var apiService: AppApiService
    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        apiService = ServiceBuilderApp.buildService(AppApiService::class.java)
        authorize()
        setContentView(binding.root)
    }

    private fun authorize(){
        if(Auth.getToken(this) != null){
            lifecycleScope.launch(Dispatchers.IO){
                if(Auth.authUser(this@SplashActivity)){
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }else{
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }

    }

}