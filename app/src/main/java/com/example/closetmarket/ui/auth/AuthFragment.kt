package com.example.closetmarket.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.closetmarket.R

class AuthFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnSignUp = view.findViewById<Button>(R.id.btnSignUp)
        val nameContainer = view.findViewById<LinearLayout>(R.id.nameContainer)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        btnLogin.setOnClickListener {
            isLoginMode = true
            nameContainer.visibility = View.GONE
            btnLogin.setBackgroundResource(R.drawable.bg_button_purple)
            btnLogin.setTextColor(resources.getColor(R.color.white, null))
            btnSignUp.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            btnSignUp.setTextColor(resources.getColor(R.color.gray_600, null))
            btnSubmit.text = getString(R.string.login)
        }

        btnSignUp.setOnClickListener {
            isLoginMode = false
            nameContainer.visibility = View.VISIBLE
            btnSignUp.setBackgroundResource(R.drawable.bg_button_purple)
            btnSignUp.setTextColor(resources.getColor(R.color.white, null))
            btnLogin.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            btnLogin.setTextColor(resources.getColor(R.color.gray_600, null))
            btnSubmit.text = getString(R.string.sign_up)
        }

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val name = etName.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                tvError.text = "Please fill in all fields"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!isLoginMode && name.isEmpty()) {
                tvError.text = "Please enter your name"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            tvError.visibility = View.GONE

            if (isLoginMode) {
                viewModel.login(email, password)
            } else {
                viewModel.register(email, password, name)
            }
        }

        // Observe ViewModel
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSubmit.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                tvError.text = error
                tvError.visibility = View.VISIBLE
            } else {
                tvError.visibility = View.GONE
            }
        }

        viewModel.authSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Navigation.findNavController(view)
                    .navigate(R.id.action_auth_to_feed)
            }
        }
    }
}

