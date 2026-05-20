package com.example.closetmarket.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.closetmarket.R
import com.squareup.picasso.Picasso
import com.example.closetmarket.model.ClothingItem

class ClothingItemAdapter : RecyclerView.Adapter<ClothingItemAdapter.ItemViewHolder>() {

    private var items: List<ClothingItem> = listOf()
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: ClothingItem)
        fun onWishlistToggle(itemId: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setItems(newItems: List<ClothingItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_clothing_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.itemImage)
        private val titleText: TextView = itemView.findViewById(R.id.itemTitle)
        private val priceText: TextView = itemView.findViewById(R.id.itemPrice)
        private val descriptionText: TextView = itemView.findViewById(R.id.itemDescription)
        private val locationText: TextView = itemView.findViewById(R.id.itemLocation)
        private val userNameText: TextView = itemView.findViewById(R.id.itemUserName)
        private val dateText: TextView = itemView.findViewById(R.id.itemDate)
        private val conditionBadge: TextView = itemView.findViewById(R.id.itemCondition)
        private val wishlistBtn: ImageButton = itemView.findViewById(R.id.btnWishlist)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(items[pos])
                }
            }
            wishlistBtn.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener?.onWishlistToggle(items[pos].id)
                }
            }
        }

        fun bind(item: ClothingItem) {
            titleText.text = item.title
            priceText.text = if (item.price == "free") "Free" else "₪${item.price}"
            descriptionText.text = item.description
            locationText.text = "${item.city}, ${item.street}"
            userNameText.text = "by ${item.userName}"
            dateText.text = item.uploadDate

            // Condition badge
            conditionBadge.text = when (item.condition) {
                "like-new" -> "Like New"
                else -> item.condition.replaceFirstChar { it.uppercase() }
            }
            val conditionBg = when (item.condition) {
                "new" -> R.drawable.badge_green
                "like-new" -> R.drawable.badge_blue
                "used" -> R.drawable.badge_orange
                else -> R.drawable.badge_gray
            }
            conditionBadge.setBackgroundResource(conditionBg)

            // Wishlist icon
            wishlistBtn.setImageResource(
                if (item.isWishlisted) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            // Load image with Picasso
            if (item.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_image_placeholder)
            }
        }
    }
}

