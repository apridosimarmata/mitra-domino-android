package id.infinion.chipdomino

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import id.infinion.chipdomino.databinding.ActivityLoginBinding
import id.infinion.models.AuthenticationResponse
import id.infinion.models.Otp
import id.infinion.remote.AppApiService
import id.infinion.remote.ServiceBuilderApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var apiService: AppApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        apiService = ServiceBuilderApp.buildService(AppApiService::class.java)
        setupListeners()
        setContentView(binding.root)
    }

    private fun setupListeners(){
        binding.btnLoginRequestOTP.setOnClickListener{
            var code = 400
            val progress = ProgressDialog(this)
            lifecycleScope.launch(Dispatchers.Main){
                progress.setMessage("Mengirim kode ...")
                progress.show()

                val wait = lifecycleScope.async {
                    try{
                        code = apiService.requestUserOTPByPhone(binding.etLoginPhone.text.toString()).code()
                    }catch (e : Exception){
                        showToast(e.toString())
                    }
                }
                wait.await()
                progress.dismiss()

                when(code){
                    in (400 .. 410) -> showToast("Kesalahan klien")
                    in (500 .. 510) -> showToast("Kesalahan server, hubungi admin")
                    200 -> showToast("Kode berhasil dikirim, cek SMS")
                    else -> showToast("Kesalahan tidak diketahui, hubingi admin")
                }
            }
        }

        binding.btnLogin.setOnClickListener{
            var code = 400
            val progress = ProgressDialog(this)
            lifecycleScope.launch(Dispatchers.Main){
                val wait = lifecycleScope.async {
                    try {
                        val res = apiService.checkUserOTPByPhone(
                            binding.etLoginPhone.text.toString(),
                            Otp(binding.etLoginOTP.text.toString())
                        )
                        when(res.code()){
                            in (400 .. 410) -> showToast("Kesalahan klien")
                            in (500 .. 510) -> showToast("Kesalahan server, hubungi admin")
                            200 -> {
                                res.body()?.let {
                                    setTokens(it)
                                }
                            }
                            else -> showToast("Kesalahan tidak diketahui, hubingi admin")
                        }
                    }catch (e : Exception){
                        showToast(e.toString())
                    }
                }
                wait.await()
            }
        }
    }

    private fun showToast(message : String){
        Snackbar.make(binding.imageView, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setTokens(authenticationResponse: AuthenticationResponse){
        var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()){
            putString("token", authenticationResponse.token)
            putString("refresh_token", authenticationResponse.refreshToken)
                .commit()
        }
        startActivity(Intent(this, MainActivity::class.java))
    }


}