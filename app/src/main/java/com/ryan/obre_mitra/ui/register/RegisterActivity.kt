package com.ryan.obre_mitra.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ryan.obre_mitra.databinding.ActivityRegisterBinding
import com.ryan.obre_mitra.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        val loginTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNameEmpty = binding.nameEditText.text.toString().trim().isEmpty()
                val isEmailEmpty = binding.emailEditText.text.toString().trim().isEmpty()
                val isPasswordEmpty = binding.passwordEditText.text.toString().trim().isEmpty()
                val isPasswordValid = binding.passwordEditText.text.toString().length >= 8
                val isPhoneEmpty = binding.phoneEdt.text.toString().trim().isEmpty()
                binding.registerBtn.isEnabled =
                    !isNameEmpty && !isEmailEmpty && !isPasswordEmpty && isPasswordValid && !isPhoneEmpty
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.nameEditText.addTextChangedListener(loginTextWatcher)
        binding.emailEditText.addTextChangedListener(loginTextWatcher)
        binding.passwordEditText.addTextChangedListener(loginTextWatcher)
        binding.phoneEdt.addTextChangedListener(loginTextWatcher)
    }

    private fun setupAction() {
        binding.registerBtn.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val phoneNumber = binding.phoneEdt.text.toString()
            viewModel.registerUser(name, email, password, phoneNumber)

            // Tampilkan popup setelah registrasi
            showPopup()
        }
        binding.txtHaveAccount.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun showPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cek Email Kamu")
        builder.setMessage("Link verifikasi telah dikirim ke email kamu. Segera cek email dan klik tombol verifikasi, sebelum melakukan login")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // Arahkan pengguna ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dialog = builder.create()
        dialog.show()
    }

}
