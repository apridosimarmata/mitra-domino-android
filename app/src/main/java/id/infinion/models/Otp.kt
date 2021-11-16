package id.infinion.models

import com.google.gson.annotations.SerializedName

data class Otp(
    @field:SerializedName("otp")
    val otp : String
)