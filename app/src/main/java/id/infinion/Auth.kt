package id.infinion

import android.content.Context
import android.preference.PreferenceManager
import id.infinion.models.AuthenticationResponse
import id.infinion.remote.AppApiService
import id.infinion.remote.ServiceBuilderApp
import kotlinx.coroutines.*

object Auth {

    suspend fun authUser(context: Context) : Boolean{
        val apiService = ServiceBuilderApp.buildService(AppApiService::class.java)
        var code = 400
            val wait = GlobalScope.async {
                try{
                    getToken(context)?.let {
                        code = apiService.authorizeUserByJWT(it).code()
                    }
                }catch (e :Exception){
                    code = 400
                }
            }
            wait.await()
            return when(code){
                200 -> true
                410 -> reauthUser(context)
                else -> false
            }

    }

    suspend fun reauthUser(context: Context) : Boolean{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val apiService = ServiceBuilderApp.buildService(AppApiService::class.java)

        var reauthenticated = AuthenticationResponse(500, "", "")
        val wait = CoroutineScope(Dispatchers.Default).async {
            try {
                getRefreshToken(context)?.let {
                    apiService.userReauthorizeByJWT(it).body()?.let {
                        reauthenticated = it
                    }
                }
            }catch (e : Exception){

            }

        }
        wait.await()
        return when(reauthenticated.code){
            200 -> {
                with(sharedPreferences.edit()) {
                    putString("token", reauthenticated.token)
                    putString("refresh_token", reauthenticated.refreshToken)
                        .commit()
                }
                true
            }
            else -> false
        }
    }

    fun getToken(context : Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("token", null)
    }

    fun getRefreshToken(context: Context) : String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("refresh_token", null)
    }
}