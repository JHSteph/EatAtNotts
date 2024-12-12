buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        // Add other classpath dependencies here if needed
    }
}

plugins {
    // Apply your plugins using the appropriate alias or plugin ID
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}



