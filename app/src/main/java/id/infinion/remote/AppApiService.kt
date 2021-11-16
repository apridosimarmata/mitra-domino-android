package id.infinion.remote

import id.infinion.models.AuthenticationResponse
import id.infinion.models.Order
import id.infinion.models.Otp
import retrofit2.Response
import retrofit2.http.*

interface AppApiService {
    @POST("/api/user/OTP/{phone}")
    suspend fun checkUserOTPByPhone(@Path("phone") phone : String, @Body otp : Otp) : Response<AuthenticationResponse>

    @GET("/api/user/OTP/{phone}")
    suspend fun requestUserOTPByPhone(@Path("phone") phone : String) : Response<Any>

    @GET("/api/user/authorize")
    suspend fun authorizeUserByJWT(@Header("Token") token : String) : Response<Any>

    @GET("/api/user/reauthenticate")
    suspend fun userReauthorizeByJWT(@Header("Refresh-Token") refreshToken : String) : Response<AuthenticationResponse>

    @GET("/api/player/{playerId}")
    suspend fun getPlayerById(@Path("playerId") playerId : String) : Response<String>

    @GET("/api/player/send/{playerId}")
    suspend fun sendVoucher(@Header("Token") token : String, @Path("playerId") playerId : String) : Response<Any>

    @GET("/api/player/history")
    suspend fun getOrderHistory(@Header("Token") token : String) : Response<Array<Order>>
}