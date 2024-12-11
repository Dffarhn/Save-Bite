package com.bersamadapa.recylefood.data.model

data class OrderRequest (
    val mysteryBox : List<String>
)


data class OrderResponse(
    val id:String,
    val token_midtrans: Midtrans
)

data class Midtrans(
    val token:String,
    val redirect_url: String
)

