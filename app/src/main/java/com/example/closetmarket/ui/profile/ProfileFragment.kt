package com.example.closetmarket.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.closetmarket.R
import com.example.closetmarket.ui.feed.ClothingItemAdapter
import com.squareup.picasso.Picasso
import com.example.closetmarket.model.ClothingItem

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var adapter: ClothingItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val ivProfileImage = view.findViewById<ImageView>(R.id.ivProfileImage)
        val tvItemsCount = view.findViewById<TextView>(R.id.tvItemsCount)
        val btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMyItems)
        val emptyState = view.findViewById<LinearLayout>(R.id.emptyState)

        // RecyclerView setup
        adapter = ClothingItemAdapter()
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ClothingItemAdapter.OnItemClickListener {
            override fun onItemClick(item: ClothingItem) {
                val bundle = Bundle().apply { putString("itemId", item.id) }
                Navigation.findNavController(view).navigate(R.id.action_profile_to_details, bundle)
            }

            override fun onWishlistToggle(itemId: String) {
                // Toggle handled by repository
                com.example.closetmarket.repository.ClothingItemRepository.toggleWishlist(itemId)
            }
        })

        // Load user
        viewModel.loadUser()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                tvUserName.text = user.displayName
                tvUserEmail.text = user.email
                if (user.profileImageUrl.isNotEmpty()) {
                    ivProfileImage.setPadding(0, 0, 0, 0)
                    ivProfileImage.imageTintList = null
                    ivProfileImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivProfileImage.clipToOutline = true
                    Picasso.get()
                        .load(user.profileImageUrl)
                        .placeholder(R.drawable.bg_circle_purple)
                        .into(ivProfileImage)
                }
            }
        }

        // User items
        viewModel.getUserItems().observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
            tvItemsCount.text = items.size.toString()
            if (items.isEmpty()) {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        btnEditProfile.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_profile_to_editProfile)
        }

        btnLogout.setOnClickListener {
            viewModel.logout()
            Navigation.findNavController(view)
                .navigate(R.id.action_profile_to_auth)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUser()
    }
}
