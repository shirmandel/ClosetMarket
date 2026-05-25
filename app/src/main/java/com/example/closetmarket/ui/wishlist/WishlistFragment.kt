package com.example.closetmarket.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetmarket.R
import com.example.closetmarket.model.ClothingItem
import com.example.closetmarket.ui.feed.ClothingItemAdapter

class WishlistFragment : Fragment() {

    private lateinit var viewModel: WishlistViewModel
    private lateinit var adapter: ClothingItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WishlistViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val tvCount = view.findViewById<TextView>(R.id.tvWishlistCount)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewWishlist)
        val emptyState = view.findViewById<LinearLayout>(R.id.emptyState)

        adapter = ClothingItemAdapter()
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ClothingItemAdapter.OnItemClickListener {
            override fun onItemClick(item: ClothingItem) {
                val bundle = Bundle().apply { putString("itemId", item.id) }
                Navigation.findNavController(view).navigate(R.id.action_wishlist_to_details, bundle)
            }

            override fun onWishlistToggle(itemId: String) {
                viewModel.toggleWishlist(itemId)
            }
        })

        btnBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        viewModel.wishlistedItems.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
            tvCount.text = getString(R.string.items_saved, items.size)

            if (items.isEmpty()) {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}
