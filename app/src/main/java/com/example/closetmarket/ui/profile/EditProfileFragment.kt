package com.example.closetmarket.ui.profile

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.closetmarket.R
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var selectedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            if (bitmap != null) {
                selectedImageBitmap = bitmap
                view?.findViewById<ImageView>(R.id.ivProfileImage)?.setImageBitmap(bitmap)
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                view?.findViewById<ImageView>(R.id.ivProfileImage)?.let { iv ->
                    Picasso.get().load(uri).into(iv)
                }
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
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivProfileImage = view.findViewById<ImageView>(R.id.ivProfileImage)
        val btnChangePhoto = view.findViewById<Button>(R.id.btnChangePhoto)
        val etName = view.findViewById<EditText>(R.id.etName)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // Load current user data
        val user = viewModel.getUser()
        if (user != null) {
            etName.setText(user.displayName)
            if (user.profileImageUrl.isNotEmpty()) {
                Picasso.get().load(user.profileImageUrl).into(ivProfileImage)
            }
        }

        btnChangePhoto.setOnClickListener {
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

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveProfile(name, selectedImageBitmap)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSave.isEnabled = !isLoading
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view).popBackStack()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

