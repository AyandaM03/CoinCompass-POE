package com.example.opsc_code

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.AnticipateInterpolator
import com.example.opsc_code.ui.theme.LoginActivity

/**
 * MainActivity serves as the animated splash screen for the Budget Tracker app.
 * Displays the app logo and name with animations, then navigates to LoginActivity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var ivLogo: ImageView
    private lateinit var tvBudget: TextView
    private lateinit var tvTracker: TextView
    private lateinit var tvTagline: TextView
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            initializeViews()
            startAnimations()
            setupContinueButton()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: navigate directly to login if splash screen fails
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * Initializes all view references.
     */
    private fun initializeViews() {
        ivLogo = findViewById(R.id.ivLogo)
        tvBudget = findViewById(R.id.tvBudget)
        tvTracker = findViewById(R.id.tvTracker)
        tvTagline = findViewById(R.id.tvTagline)
        btnContinue = findViewById(R.id.btnContinue)
    }

    /**
     * Starts the splash screen animations sequence.
     * Animates logo, text, and continue button in sequence.
     */
    private fun startAnimations() {
        // Initial states - hide all elements
        ivLogo.alpha = 0f
        ivLogo.scaleX = 0f
        ivLogo.scaleY = 0f
        tvBudget.alpha = 0f
        tvBudget.translationX = -200f
        tvTracker.alpha = 0f
        tvTracker.translationX = 200f
        tvTagline.alpha = 0f
        btnContinue.alpha = 0f
        btnContinue.translationY = 100f

        // Animate Logo - Scale up with bounce
        val logoScaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f)

        logoScaleX.duration = 1000
        logoScaleY.duration = 1000
        logoAlpha.duration = 500

        logoScaleX.interpolator = AnticipateInterpolator()
        logoScaleY.interpolator = AnticipateInterpolator()

        // Animate "Budget" text - Slide in from left
        val budgetSlide = ObjectAnimator.ofFloat(tvBudget, "translationX", -200f, 0f)
        val budgetAlpha = ObjectAnimator.ofFloat(tvBudget, "alpha", 0f, 1f)

        budgetSlide.duration = 800
        budgetAlpha.duration = 600
        budgetSlide.startDelay = 500
        budgetAlpha.startDelay = 500

        // Animate "Tracker" text - Slide in from right
        val trackerSlide = ObjectAnimator.ofFloat(tvTracker, "translationX", 200f, 0f)
        val trackerAlpha = ObjectAnimator.ofFloat(tvTracker, "alpha", 0f, 1f)

        trackerSlide.duration = 800
        trackerAlpha.duration = 600
        trackerSlide.startDelay = 700
        trackerAlpha.startDelay = 700

        // Animate Tagline - Fade in
        val taglineAlpha = ObjectAnimator.ofFloat(tvTagline, "alpha", 0f, 1f)
        taglineAlpha.duration = 800
        taglineAlpha.startDelay = 1200

        // Animate Continue Button - Slide up and fade in
        val buttonSlide = ObjectAnimator.ofFloat(btnContinue, "translationY", 100f, 0f)
        val buttonAlpha = ObjectAnimator.ofFloat(btnContinue, "alpha", 0f, 1f)

        buttonSlide.duration = 600
        buttonAlpha.duration = 600
        buttonSlide.startDelay = 1600
        buttonAlpha.startDelay = 1600

        // Start all animations
        logoScaleX.start()
        logoScaleY.start()
        logoAlpha.start()
        budgetSlide.start()
        budgetAlpha.start()
        trackerSlide.start()
        trackerAlpha.start()
        taglineAlpha.start()
        buttonSlide.start()
        buttonAlpha.start()
    }

    /**
     * Sets up the continue button click listener.
     * Navigates to LoginActivity with a fade transition.
     */
    private fun setupContinueButton() {
        btnContinue.setOnClickListener {
            // Animate button click
            btnContinue.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    btnContinue.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            navigateToLogin()
                        }
                        .start()
                }
                .start()
        }
    }

    /**
     * Navigates to LoginActivity with a fade transition animation.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // Apply fade transition
        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}