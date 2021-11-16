package id.infinion.chipdomino

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.infinion.chipdomino.databinding.OrderItemBinding
import id.infinion.models.Order

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var orders = arrayListOf<Order>()

    inner class OrderViewHolder(private val binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(order : Order){
            binding.tvBuyerId.text = order.buyerId.toString()
            binding.tvCreatedAt.text =  order.createdAt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder =
        OrderViewHolder(OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
}