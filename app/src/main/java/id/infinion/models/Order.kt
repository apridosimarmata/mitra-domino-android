package id.infinion.models

import com.google.gson.annotations.SerializedName

data class Order(
    @field:SerializedName("id")
    val id : Int,

    @field:SerializedName("buyerId")
    val buyerId : Int,

    @field:SerializedName("createdAt")
    val createdAt : String
)
