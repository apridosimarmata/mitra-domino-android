package id.infinion.models

import com.google.gson.annotations.SerializedName

data class AuthenticationResponse(
    @field:SerializedName("code")
    val code : Int,

    @field:SerializedName("token")
    val token : String,

    @field:SerializedName("refresh_token")
    val refreshToken : String
)