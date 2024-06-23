package com.ryan.obre_mitra.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainViewModel : ViewModel() {
    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> get() = _userData

    fun fetchUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        userId?.let { uid ->
            FirebaseFirestore.getInstance().collection("UserMitra").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    _userData.value = user
                }
                .addOnFailureListener {
                    _userData.value = null
                }
        }
    }
}