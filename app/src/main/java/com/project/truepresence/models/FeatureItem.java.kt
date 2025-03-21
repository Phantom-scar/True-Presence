package com.project.truepresence.models

// ✅ Correct Kotlin Data Class for Feature Items
data class FeatureItem(
    val iconResId: Int,
    val titleResId: Int,
    val activityClass: Class<*>
)
