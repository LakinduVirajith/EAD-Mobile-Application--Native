package com.example.ead_mobile_application__native.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.Cart
import com.example.ead_mobile_application__native.service.CartApiService

class CartAdapter(private var items: List<Cart>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>(){
    // API SERVICE INSTANCE
    private val cartApiService = CartApiService()

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.cProductImage)
        val productName: TextView = itemView.findViewById(R.id.cProductName)
        val productPrice: TextView = itemView.findViewById(R.id.cProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.cProductQuantity)

        val plusIcon: ImageView = itemView.findViewById(R.id.cPlusIcon)
        val minusIcon: ImageView = itemView.findViewById(R.id.cMinusIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    fun updateItems(newItems: List<Cart>) {
        // STORE OLD ITEMS
        val oldItems = items
        items = newItems

        // NOTIFY ABOUT CHANGES
        if (oldItems.size < newItems.size) {
            // NOTIFY ITEM RANGE INSERTED
            notifyItemRangeInserted(oldItems.size, newItems.size - oldItems.size)
        } else if (oldItems.size > newItems.size) {
            // NOTIFY ITEM RANGE REMOVED
            notifyItemRangeRemoved(newItems.size, oldItems.size - newItems.size)
        } else {
            // NOTIFY ITEM CHANGES
            for (i in oldItems.indices) {
                if (oldItems[i] != newItems[i]) {
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView.context)
            .load(item.imageResId)
            .into(holder.productImage)
        holder.productName.text = item.name
        holder.productPrice.text = holder.itemView.context.getString(R.string.product_price_format, item.price.toString())
        holder.productQuantity.text = item.quantity.toString()

        // SET CLICK LISTENER TO INGRESS QUANTITY
        holder.plusIcon.setOnClickListener {
            cartApiService.cartProductPlus(item.id) { response ->
                if (response != null) {
                    item.quantity += 1
                    notifyItemChanged(position)
                }
            }
        }
        // SET CLICK LISTENER TO DECREES QUANTITY
        holder.minusIcon.setOnClickListener {
            cartApiService.cartProductMinus(item.id) { response ->
                if (response != null) {
                    item.quantity -= 1
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}