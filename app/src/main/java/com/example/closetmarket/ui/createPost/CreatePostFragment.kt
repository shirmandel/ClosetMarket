package com.example.closetmarket.ui.createPost

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.closetmarket.R
import com.example.closetmarket.repository.UserRepository
import com.squareup.picasso.Picasso

class CreatePostFragment : Fragment() {

    private lateinit var viewModel: CreatePostViewModel
    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var selectedImageBitmap: Bitmap? = null
    private var existingImageUrl: String? = null
    private var editItemId: String? = null
    private var selectedCondition = "used"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreatePostViewModel::class.java)

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            if (bitmap != null) {
                selectedImageBitmap = bitmap
                view?.findViewById<ImageView>(R.id.ivPreview)?.apply {
                    setImageBitmap(bitmap)
                    visibility = View.VISIBLE
                }
                view?.findViewById<LinearLayout>(R.id.imagePlaceholder)?.visibility = View.GONE
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                view?.findViewById<ImageView>(R.id.ivPreview)?.apply {
                    Picasso.get().load(uri).into(this)
                    visibility = View.VISIBLE
                }
                view?.findViewById<LinearLayout>(R.id.imagePlaceholder)?.visibility = View.GONE
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver, uri
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argEditItemId = arguments?.getString("editItemId", "") ?: ""
        editItemId = argEditItemId.ifEmpty { null }

        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val btnPost = view.findViewById<Button>(R.id.btnPost)
        val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
        val ivPreview = view.findViewById<ImageView>(R.id.ivPreview)
        val imagePlaceholder = view.findViewById<LinearLayout>(R.id.imagePlaceholder)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val btnCondNew = view.findViewById<Button>(R.id.btnCondNew)
        val btnCondLikeNew = view.findViewById<Button>(R.id.btnCondLikeNew)
        val btnCondUsed = view.findViewById<Button>(R.id.btnCondUsed)
        val cbFree = view.findViewById<CheckBox>(R.id.cbFree)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val spinnerCity = view.findViewById<Spinner>(R.id.spinnerCity)
        val etStreet = view.findViewById<EditText>(R.id.etStreet)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // Category spinner
        val categoryAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.categories_values_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCategory.adapter = categoryAdapter

        // City spinner
        val cities = resources.getStringArray(R.array.cities_array)
        val cityList = mutableListOf(getString(R.string.select_city))
        cityList.addAll(cities)
        val cityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            cityList
        )
        spinnerCity.adapter = cityAdapter

        // Condition buttons
        fun updateConditionButtons() {
            btnCondNew.setBackgroundResource(
                if (selectedCondition == "new") R.drawable.bg_button_purple else R.drawable.bg_input
            )
            btnCondNew.setTextColor(resources.getColor(
                if (selectedCondition == "new") R.color.white else R.color.black, null
            ))
            btnCondLikeNew.setBackgroundResource(
                if (selectedCondition == "like-new") R.drawable.bg_button_purple else R.drawable.bg_input
            )
            btnCondLikeNew.setTextColor(resources.getColor(
                if (selectedCondition == "like-new") R.color.white else R.color.black, null
            ))
            btnCondUsed.setBackgroundResource(
                if (selectedCondition == "used") R.drawable.bg_button_purple else R.drawable.bg_input
            )
            btnCondUsed.setTextColor(resources.getColor(
                if (selectedCondition == "used") R.color.white else R.color.black, null
            ))
        }

        btnCondNew.setOnClickListener { selectedCondition = "new"; updateConditionButtons() }
        btnCondLikeNew.setOnClickListener { selectedCondition = "like-new"; updateConditionButtons() }
        btnCondUsed.setOnClickListener { selectedCondition = "used"; updateConditionButtons() }
        updateConditionButtons()

        cbFree.setOnCheckedChangeListener { _, isChecked ->
            etPrice.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        imagePlaceholder.setOnClickListener { showImageChooser() }
        ivPreview.setOnClickListener { showImageChooser() }

        btnClose.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        if (editItemId != null) {
            tvHeader.text = getString(R.string.edit_post)
            btnPost.text = getString(R.string.save)
        }

        // If editing, load existing item
        if (editItemId != null) {
            viewModel.loadItem(editItemId!!)
            viewModel.currentItem.observe(viewLifecycleOwner) { item ->
                if (item != null) {
                    etTitle.setText(item.title)
                    etDescription.setText(item.description)
                    etStreet.setText(item.street)
                    existingImageUrl = item.imageUrl
                    selectedCondition = item.condition
                    updateConditionButtons()

                    if (item.price == "free") {
                        cbFree.isChecked = true
                    } else {
                        etPrice.setText(item.price)
                    }

                    if (item.imageUrl.isNotEmpty()) {
                        Picasso.get().load(item.imageUrl).into(ivPreview)
                        ivPreview.visibility = View.VISIBLE
                        imagePlaceholder.visibility = View.GONE
                    }

                    // Set city spinner
                    val cityIndex = cityList.indexOf(item.city)
                    if (cityIndex >= 0) spinnerCity.setSelection(cityIndex)

                    // Set category spinner
                    val catValues = resources.getStringArray(R.array.categories_values_array)
                    val catIndex = catValues.indexOf(item.category)
                    if (catIndex >= 0) spinnerCategory.setSelection(catIndex)
                }
            }
        }

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val category = spinnerCategory.selectedItem?.toString()?.lowercase() ?: "tops"
            val price = etPrice.text.toString().trim()
            val isFree = cbFree.isChecked
            val city = spinnerCity.selectedItem?.toString() ?: ""
            val street = etStreet.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || city == getString(R.string.select_city) || street.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = UserRepository.getCurrentUser()
            if (user == null) {
                Toast.makeText(requireContext(), "Not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.submitPost(
                title = title,
                description = description,
                category = category,
                condition = selectedCondition,
                price = price,
                isFree = isFree,
                city = city,
                street = street,
                imageBitmap = selectedImageBitmap,
                existingImageUrl = existingImageUrl,
                userId = user.uid,
                userName = user.displayName,
                editItemId = editItemId
            )
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnPost.isEnabled = !isLoading
        }

        viewModel.postSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Item saved!", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view).popBackStack()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageChooser() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.choose_image_source))
            .setItems(arrayOf(getString(R.string.camera), getString(R.string.gallery))) { _, which ->
                when (which) {
                    0 -> cameraLauncher.launch(null)
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }
}
