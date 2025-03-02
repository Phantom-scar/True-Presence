package com.project.truepresence

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private var imageUri: Uri? = null

    // 🔹 New way to get image using Activity Result API
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            profileImageView.setImageURI(uri)
        }
    }

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
        // 🔹 Uses new Activity Result API to select an image
        pickImageLauncher.launch("image/*")
    }
}
