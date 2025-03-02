package com.project.truepresence

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Apply pixel font to buttons
        applyPixelFont(loginButton)
        applyPixelFont(registerButton)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.slide_in_left,  // Enter animation
                android.R.anim.slide_out_right // Exit animation
            ).toBundle()
            startActivity(intent, options)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                showToast("Please enter email and password")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Invalid email format")
                false
            }
            password.length < 6 -> {
                showToast("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    private fun loginUser(email: String, password: String) {
        // Disable button to prevent spam clicks
        loginButton.isEnabled = false
        applyButtonGlowEffect(loginButton)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginButton.isEnabled = true  // Re-enable button

                if (task.isSuccessful) {
                    showToast("Login Successful!")
                    val intent = Intent(this, MainActivity::class.java)
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,  // Enter animation
                        android.R.anim.fade_out // Exit animation
                    ).toBundle()
                    startActivity(intent, options)
                    finish()
                } else {
                    showToast("Login Failed: ${task.exception?.message}")
                }
            }
    }

    private fun applyPixelFont(button: Button) {
        val pixelFont = resources.getFont(R.font.pixel_font) // Ensure this font is added in res/font/
        button.typeface = pixelFont
    }

    private fun applyButtonGlowEffect(button: Button) {
        val glowAnimation = AlphaAnimation(0.3f, 1.0f)
        glowAnimation.duration = 300
        glowAnimation.repeatCount = 1
        glowAnimation.repeatMode = AlphaAnimation.REVERSE
        button.startAnimation(glowAnimation)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
