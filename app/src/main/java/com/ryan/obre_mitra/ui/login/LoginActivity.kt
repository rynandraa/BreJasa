package com.ryan.obre_mitra.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ActivityLoginBinding
import com.ryan.obre_mitra.databinding.CustomDialogLayoutBinding
import com.ryan.obre_mitra.ui.MainActivity
import com.ryan.obre_mitra.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(LoginViewModel::class.java)

        if (loginViewModel.isLoggedIn()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        loginViewModel.loginState.observe(this, { loginState ->
            if (loginState) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Toast.makeText(this, "Login gagal. Mohon Periksa kembali email dan password", Toast.LENGTH_SHORT).show()
            }
        })

        val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

        if (isFirstTime) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupAction()
        setupInput()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            loginViewModel.loginUser(email, password)
        }

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.txtLupaPassword.setOnClickListener {
            showResetPasswordDialog()
        }
    }

    private fun setupInput() {
        val loginTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmailEmpty = binding.inputEmail.text.toString().trim().isEmpty()
                val isPasswordEmpty = binding.inputPassword.text.toString().trim().isEmpty()
                val isPasswordValid = binding.inputPassword.text.toString().length >= 8
                binding.loginButton.isEnabled = !isEmailEmpty && !isPasswordEmpty && isPasswordValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEmail.addTextChangedListener(loginTextWatcher)
        binding.inputPassword.addTextChangedListener(loginTextWatcher)
    }

    private fun showResetPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogBinding = CustomDialogLayoutBinding.inflate(inflater)
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSubmit.setOnClickListener {
            val email = dialogBinding.emailEditText.text.toString()
            if (email.isNotEmpty()) {
                resetPassword(email)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun resetPassword(email: String) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email reset password telah dikirim", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}