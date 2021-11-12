package com.example.chatappfirebase.Models

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.lang.Exception

class User {
    var name: String = ""
    var email: String = ""
    var photoUrl: String = ""

    constructor()

    constructor(name: String, email: String, photoUrl: String) {
        this.name = name
        this.email = email
        this.photoUrl = photoUrl
    }

    fun toHash(): HashMap<String, Any> {
        var hashMap = HashMap<String, Any>()
        hashMap.put("name", name)
        hashMap.put("email", email)
        hashMap.put("photoUrl", photoUrl)
        return hashMap
    }

    companion object {
        fun fromHash(hashMap: QueryDocumentSnapshot): User {
            val user = User(
                hashMap["name"] as String,
                hashMap["email"] as String,
                hashMap["photoUrl"] as String
            )
            return user
        }

        fun saveUserDisplayName(user: FirebaseUser, displayName: String): Boolean {
            try {
                val profile = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profile).addOnCompleteListener {
                    if(!it.isSuccessful) {
                        Log.d("SettingsActivity", "User display name not updated.")
                    }
                }
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        fun updateUserDb(name: String?, photoUrl: String?) {
            var dbRef = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
            if(name != null) {
                dbRef.update(mapOf(
                    "name" to name
                ))
            }
            if(photoUrl != null) {
                dbRef.update(mapOf(
                    "photoUrl" to photoUrl
                ))
            }
        }
    }
}