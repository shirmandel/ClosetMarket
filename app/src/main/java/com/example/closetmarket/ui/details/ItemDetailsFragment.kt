package com.example.closetmarket.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.closetmarket.R
import com.example.closetmarket.repository.UserRepository
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemDetailsFragment : Fragment() {

    private lateinit var viewModel: ItemDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ItemDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemId = arguments?.getString("itemId") ?: return

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val ivItemImage = view.findViewById<ImageView>(R.id.ivItemImage)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvPrice = view.findViewById<TextView>(R.id.tvPrice)
        val tvCondition = view.findViewById<TextView>(R.id.tvCondition)
        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val tvCity = view.findViewById<TextView>(R.id.tvCity)
        val tvStreet = view.findViewById<TextView>(R.id.tvStreet)
        val tvUserInitial = view.findViewById<TextView>(R.id.tvUserInitial)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUploadDate = view.findViewById<TextView>(R.id.tvUploadDate)
        val ownerButtons = view.findViewById<LinearLayout>(R.id.ownerButtons)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnContactSeller = view.findViewById<Button>(R.id.btnContactSeller)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        btnBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        viewModel.loadItem(itemId)

        viewModel.item.observe(viewLifecycleOwner) { item ->
            if (item == null) return@observe

            tvTitle.text = item.title
            tvPrice.text = if (item.price == "free") getString(R.string.free_label) else "₪${item.price}"
            tvCondition.text = item.condition.replaceFirstChar { it.uppercase() }
            tvCategory.text = item.category.replaceFirstChar { it.uppercase() }
            tvDescription.text = item.description
            tvCity.text = item.city
            tvStreet.text = item.street
            tvUserName.text = item.userName

            if (item.userName.isNotEmpty()) {
                tvUserInitial.text = item.userName.first().uppercase()
            }

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            tvUploadDate.text = dateFormat.format(Date(item.lastUpdated ?: System.currentTimeMillis()))

            if (item.imageUrl.isNotEmpty()) {
                Picasso.get().load(item.imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivItemImage)
            }

            val currentUser = UserRepository.getCurrentUser()
            if (currentUser != null && currentUser.uid == item.userId) {
                ownerButtons.visibility = View.VISIBLE
                btnContactSeller.visibility = View.GONE
            } else {
                ownerButtons.visibility = View.GONE
                btnContactSeller.visibility = View.VISIBLE
            }
        }

        btnEdit.setOnClickListener {
            val bundle = Bundle().apply { putString("editItemId", itemId) }
           // navigate to edit item screen
        }

        btnDelete.setOnClickListener {
            viewModel.deleteItem(itemId)
        }

        btnContactSeller.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.contact_seller_msg), Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view).popBackStack()
            }
        }
    }
}


