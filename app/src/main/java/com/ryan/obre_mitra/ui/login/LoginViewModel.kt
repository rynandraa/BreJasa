package com.ryan.obre_mitra.ui.login

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val sharedPreferences = application.getSharedPreferences("myPref", Context.MODE_PRIVATE)

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _loginState.value = false
    }

    fun loginUser(email: String, password: String) {
        _isLoading.value = true // Menampilkan loading indicator

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            _loginState.value = true
                            saveLoginStatus(true)
                            Toast.makeText(getApplication(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                        } else {
                            _loginState.value = false
                            Toast.makeText(getApplication(), "Email belum diverifikasi. Silakan periksa email Anda untuk melakukan verifikasi.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        _loginState.value = false
                        Toast.makeText(getApplication(), "Gagal mendapatkan data pengguna. Silakan coba lagi.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    _loginState.value = false
                    task.exception?.let {
                        val errorMessage = when (it) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                "Email atau kata sandi salah. Silakan coba lagi."
                            }
                            is FirebaseAuthInvalidUserException -> {
                                "Pengguna tidak ditemukan. Silakan periksa email Anda atau daftar akun baru."
                            }
                            is FirebaseNetworkException -> {
                                "Koneksi jaringan bermasalah. Silakan coba lagi nanti."
                            }
                            else -> {
                                "Terjadi kesalahan. Silakan coba lagi."
                            }
                        }
                        Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_LONG).show()
                    } ?: run {
                        Toast.makeText(getApplication(), "Terjadi kesalahan yang tidak diketahui. Silakan coba lagi.", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun logoutUser() {
        auth.signOut() // Logout from Firebase Authentication
        clearLoginStatus() // Clear the login status from shared preferences
    }

    private fun clearLoginStatus() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
    }
}