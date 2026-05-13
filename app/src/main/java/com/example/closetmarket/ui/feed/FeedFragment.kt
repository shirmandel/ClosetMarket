package com.example.closetmarket.ui.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.closetmarket.R
import com.example.closetmarket.model.ClothingItem

class FeedFragment : Fragment() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var adapter: ClothingItemAdapter
    private var allItems: List<ClothingItem> = listOf()
    private var searchQuery = ""
    private var selectedCategory = "all"
    private var selectedCondition = "all"
    private var sortBy = "newest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerCondition = view.findViewById<Spinner>(R.id.spinnerCondition)
        val spinnerSort = view.findViewById<Spinner>(R.id.spinnerSort)
        val tvResultsCount = view.findViewById<TextView>(R.id.tvResultsCount)
        val emptyState = view.findViewById<LinearLayout>(R.id.emptyState)
        val btnWishlist = view.findViewById<ImageButton>(R.id.btnWishlistScreen)

        adapter = ClothingItemAdapter()
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ClothingItemAdapter.OnItemClickListener {
            override fun onItemClick(item: ClothingItem) {
                val bundle = Bundle().apply { putString("itemId", item.id) }
//                todo: navigate to item details
            }

            override fun onWishlistToggle(itemId: String) {
                viewModel.toggleWishlist(itemId)
            }
        })

        val categoryAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.categories_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCategory.adapter = categoryAdapter

        val conditionAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.conditions_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCondition.adapter = conditionAdapter

        val sortAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.sort_options_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerSort.adapter = sortAdapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
                applyFilters(tvResultsCount, emptyState, recyclerView)
            }
        })

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                val categories = resources.getStringArray(R.array.categories_array)
                selectedCategory = if (position == 0) "all" else categories[position].lowercase()
                applyFilters(tvResultsCount, emptyState, recyclerView)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerCondition.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                val conditions = resources.getStringArray(R.array.conditions_array)
                selectedCondition = if (position == 0) "all" else conditions[position].lowercase().replace(" ", "-")
                applyFilters(tvResultsCount, emptyState, recyclerView)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                sortBy = when (position) {
                    0 -> "newest"
                    1 -> "price-low"
                    2 -> "price-high"
                    else -> "newest"
                }
                applyFilters(tvResultsCount, emptyState, recyclerView)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            allItems = items
            applyFilters(tvResultsCount, emptyState, recyclerView)
        }

        // Wishlist button
//      todo: navigate to wishlist

        viewModel.refreshData()
    }

    private fun applyFilters(tvCount: TextView, emptyState: LinearLayout, recyclerView: RecyclerView) {
        var filtered = allItems
            .filter { item ->
                val matchesSearch = searchQuery.isEmpty() ||
                        item.title.lowercase().contains(searchQuery.lowercase()) ||
                        item.description.lowercase().contains(searchQuery.lowercase())
                val matchesCategory = selectedCategory == "all" || item.category == selectedCategory
                val matchesCondition = selectedCondition == "all" || item.condition == selectedCondition
                matchesSearch && matchesCategory && matchesCondition
            }

        filtered = when (sortBy) {
            "price-low" -> filtered.sortedBy {
                if (it.price == "free") 0.0 else it.price.toDoubleOrNull() ?: 0.0
            }
            "price-high" -> filtered.sortedByDescending {
                if (it.price == "free") 0.0 else it.price.toDoubleOrNull() ?: 0.0
            }
            else -> filtered
        }

        adapter.setItems(filtered)
        tvCount.text = getString(R.string.showing_items, filtered.size)

        if (filtered.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
