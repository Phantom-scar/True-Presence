package com.project.truepresence

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var updateButton: Button
    private lateinit var logoutButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        profileImageView = findViewById(R.id.profileImage)
        nameField = findViewById(R.id.nameField)
        emailField = findViewById(R.id.emailField)
        updateButton = findViewById(R.id.updateButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Load user data
        loadUserProfile()

        profileImageView.setOnClickListener {
            openImageChooser()
        }

        updateButton.setOnClickListener {
            updateProfile()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserProfile() {
        val user: FirebaseUser? = auth.currentUser

        user?.let {
            emailField.setText(it.email)

            // Load user data from Firestore
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nameField.setText(document.getString("name"))
                    }
                }
        }
    }

    private fun updateProfile() {
        val user: FirebaseUser? = auth.currentUser
        val newName = nameField.text.toString()

        if (user != null) {
            val userProfile = hashMapOf(
                "name" to newName,
                "email" to user.email
            )

            firestore.collection("users").document(user.uid).set(userProfile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            profileImageView.setImageURI(imageUri)
        }
    }
}
