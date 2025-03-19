package com.project.truepresence

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FacialAttendanceActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var faceOverlay: ImageView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var instructionText: TextView
    private var attendanceMarked = false // Prevent multiple attendance marking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facial_attendance)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        cameraExecutor = Executors.newSingleThreadExecutor()

        faceOverlay = findViewById(R.id.faceOverlay)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        instructionText = findViewById(R.id.instructionText)

        val markAttendanceButton = findViewById<Button>(R.id.markAttendanceButton)
        markAttendanceButton.setOnClickListener { setupCamera() }

        startFaceOverlayAnimation() // ✅ Added pulsing animation for face overlay
        setupCamera() // ✅ Start camera immediately
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = findViewById<androidx.camera.view.PreviewView>(R.id.cameraPreview).surfaceProvider // ✅ Fixed setter method usage
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val faceDetector = FaceDetection.getClient(
                    FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) // ✅ Improved Detection Accuracy
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build()
                )

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImage(imageProxy, faceDetector)
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("CameraX", "Failed to set up camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImage(imageProxy: ImageProxy, faceDetector: com.google.mlkit.vision.face.FaceDetector) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            runOnUiThread { loadingIndicator.visibility = View.VISIBLE }

            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    runOnUiThread { loadingIndicator.visibility = View.GONE }
                    if (faces.isNotEmpty() && !attendanceMarked) {
                        attendanceMarked = true // ✅ Prevent multiple markings
                        markAttendance()
                    }
                }
                .addOnFailureListener { e ->
                    runOnUiThread { loadingIndicator.visibility = View.GONE }
                    Log.e("FaceDetection", "Face detection error: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun markAttendance() {
        val user = auth.currentUser
        if (user != null) {
            val attendanceRecord = hashMapOf(
                "userId" to user.uid,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("attendance").add(attendanceRecord)
                .addOnSuccessListener {
                    Toast.makeText(this, "Attendance Marked!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to mark attendance", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startFaceOverlayAnimation() {
        val scaleX = ObjectAnimator.ofFloat(faceOverlay, "scaleX", 1.0f, 1.1f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(faceOverlay, "scaleY", 1.0f, 1.1f, 1.0f)

        scaleX.duration = 1000
        scaleY.duration = 1000

        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatCount = ObjectAnimator.INFINITE

        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()

        scaleX.start()
        scaleY.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
