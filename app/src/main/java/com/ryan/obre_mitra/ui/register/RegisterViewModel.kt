package com.ryan.obre_mitra.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterViewModel: ViewModel() {

    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(name: String, email: String, password: String, phoneNumber: String) {
        _isLoading.value = true // Menampilkan loading indicator
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false // Menyembunyikan loading indicator
                if (task.isSuccessful) {
                    // Pengguna berhasil didaftarkan, tambahkan informasi tambahan ke Firestore
                    val userId = auth.currentUser
                    userId?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            val user = hashMapOf(
                                "fullname" to name,
                                "email" to email,
                                "createdAt" to Calendar.getInstance().time, // Menambahkan waktu pembuatan
                                "phoneNumber" to phoneNumber, // Menambahkan nomor telepon
                                "namaUsaha" to null,
                                "deskripsiSingkat" to null,
                                "latitude" to null,
                                "longitude" to null
                            )

                            // Menambahkan data pengguna ke Firestore
                            userId.uid.let { userId->
                                firestore.collection("UserMitra")
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener {
                                        // Tampilkan pesan bahwa pengguna berhasil didaftarkan dan data ditambahkan ke Firestore
                                        _registrationState.value = RegistrationState.Success
                                    }
                                    .addOnFailureListener { e ->
                                        // Jika gagal menambahkan data ke Firestore, tampilkan pesan kesalahan
                                        _registrationState.value = RegistrationState.Error("Gagal menambahkan data pengguna ke Firestore")
                                    }
                            }

                        }
                    }

                } else {
                    // Jika gagal mendaftarkan pengguna dengan email dan password, tampilkan pesan kesalahan
                    _registrationState.value = RegistrationState.Error("Gagal mendaftarkan pengguna")
                }
            }
    }

    sealed class RegistrationState {
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }
}
