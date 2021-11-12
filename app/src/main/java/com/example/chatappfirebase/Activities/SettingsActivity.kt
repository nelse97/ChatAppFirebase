package com.example.chatappfirebase.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.chatappfirebase.Config.Permission
import com.example.chatappfirebase.Models.User
import com.example.chatappfirebase.R
import com.example.chatappfirebase.databinding.ActivitySettingsBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val CAMERA_INTENT = 100
    private val GALLERY_INTENT = 200
    private lateinit var firestoreRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //validate permissions
        Permission.validatePermissions(this, permissions, 1)

        //initialize firestore reference
        firestoreRef = FirebaseStorage.getInstance().reference

        //get current user profile image url
        val imageUrl = FirebaseAuth.getInstance().currentUser!!.photoUrl

        //set user profile image
        if (imageUrl != null) {
            Log.d("profileImageUrl", "ImageUrl: $imageUrl")
            Glide.with(this)
                .load(imageUrl)
                .into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.padrao)
            Log.d("profileImageUrl", "ImageUrl: $imageUrl")
        }

        Log.d("settings", imageUrl.toString())

        //get current user profile image url
        val displayName = FirebaseAuth.getInstance().currentUser!!.displayName

        //set user display name
        if (displayName != null)
            binding.etUsername.setText(displayName)

        binding.editDisplayNamebtn.setOnClickListener {
            val displayNameChange = User.saveUserDisplayName(
                FirebaseAuth.getInstance().currentUser!!,
                binding.etUsername.text.toString()
            )

            if (displayNameChange) {
                Toast.makeText(
                    this, "Nome atualizado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
                //update user name on firestore db record
                User.updateUserDb(binding.etUsername.text.toString(), null)
            } else {
                Toast.makeText(
                    this, "Nome NÃO atualizado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //toolbar settings
        var toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        toolbar.title = "Editar Perfil"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //open camera
        binding.profileImageCamera.setOnClickListener {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_INTENT)

        }

        //open gellery
        binding.profileImageGallery.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_INTENT)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (permissionResult in grantResults) {
                if (permissionResult == PackageManager.PERMISSION_DENIED) {
                    permissionAlert()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var imagem: Bitmap? = null
            try {
                when (requestCode) {
                    GALLERY_INTENT -> {
                        var imageUri = data?.data
                        imagem = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    }
                    CAMERA_INTENT -> {
                        imagem = data!!.extras!!.get("data") as Bitmap
                    }
                }

                if (imagem != null) {
                    binding.profileImage.setImageBitmap(imagem)

                    val baos = ByteArrayOutputStream()
                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val imageData = baos.toByteArray()

                    val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
                    val imageProfileRef = firestoreRef.child("images")
                        .child("profileImages")
                        .child("$currentUserUid.jpg")

                    imageProfileRef.putBytes(imageData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Upload de imagem concluido", Toast.LENGTH_SHORT)
                                .show()

                            // get the image Url of the file uploaded
                            imageProfileRef.downloadUrl
                                .addOnSuccessListener { uri -> // getting image uri and converting into string
                                    var fileUrl = uri.toString()

                                    //save image to firestore
                                    User.updateUserDb(null, fileUrl)

                                    //if image is saved successfully, we will save it's uri to the users profile
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    var profile = UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build()

                                    currentUser!!.updateProfile(profile)
                                        .addOnCompleteListener { updateTask ->
                                            if (!updateTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Erro ao atualizar imagem perfil.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Upload de imagem falhado", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun permissionAlert() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Permissões Negadas")
        builder.setMessage("Para editar o seu perfil, precisa de aceitar as permissões da aplicação.")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirmar") { dialogInterface, which ->
            finish()
        }
        var dialog = builder.create()
        dialog.show()
    }
}