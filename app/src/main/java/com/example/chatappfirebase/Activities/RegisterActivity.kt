package com.example.chatappfirebase.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatappfirebase.Models.User
import com.example.chatappfirebase.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*

import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {

    private val nome: TextInputEditText? = null
    private var email: TextInputEditText? = null
    private var senha: TextInputEditText? = null
    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.textViewSignIn.setOnClickListener {
            openLoginScreen()
        }

        binding.button3.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editSenha.text.toString()
            val name = binding.editNome.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userUid = auth.uid
                            val user = User(name, email, "").toHash()

                            db.collection("users").document(userUid!!).set(user)
                                .addOnSuccessListener {
                                    Log.d("firestore", "Utilizador criado com sucesso, UID: $userUid")
                                    //if image is saved successfully, we will save it's uri to the users profile
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    var profile = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()

                                    currentUser!!.updateProfile(profile)
                                        .addOnCompleteListener { updateTask ->
                                            if (!updateTask.isSuccessful) {
                                                Log.d("RegisterActivity", "Error saving user name to user auth object.")
                                            }
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.d("firestore", "Erro ao criar utilizador: $e")
                                }

                            openMainScreen()
                        } else {
                            var excecao = ""
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                excecao = "Digite uma senha mais forte!"
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                excecao = "Por favor, digite um e-mail válido"
                            } catch (e: FirebaseAuthUserCollisionException) {
                                excecao = "Esta conta já foi registada"
                            } catch (e: Exception) {
                                excecao = "Erro ao registar utilizador: " + e.message
                                e.printStackTrace()
                            }
                            Toast.makeText(this, excecao, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}