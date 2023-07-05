package com.artesdesign.data.order

sealed class OrderStatus(val status : String) {

    object Ordered : OrderStatus("Sipariş Verildi")
    object Canceled : OrderStatus("İptal Edildi")
    object Confirmed : OrderStatus("Onaylandı")
    object Shipped : OrderStatus("Kargoya Verildi")
    object Delivered : OrderStatus("Teslim Edildi")
    object Returned : OrderStatus("İade Edildi")

    fun getOrderStatus(status: String) : OrderStatus{
       return when(status){
           "Sipariş Verildi" -> {
                Ordered
            }
           "İptal Edildi" -> {
                Confirmed
            }
            "Teslim Edildi" -> {
                Delivered
            }
            "Kargoya Verildi" -> {
                Shipped
            }

            else -> Returned

        }
    }
}
