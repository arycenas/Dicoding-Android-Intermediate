package com.intermediate.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.intermediate.storyapp.databinding.ActivitySignupBinding
import com.intermediate.storyapp.view.welcome.WelcomeActivity

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupError()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupError() {
        viewModel.error.observe(this) { error ->
            if (error == true) {
                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setTitle("Gagal")
                    setMessage(viewModel.result.value)
                    setPositiveButton("Lanjut") { _, _ -> }
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else if (error == false) {
                val intent = Intent(this@SignupActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (!validateFields(name, email, password)) {
                return@setOnClickListener
            }

            viewModel.signUp(name, email, password)
        }
    }

    private fun validateFields(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameEditText.error = "Nama Tidak Boleh Kosong"
            isValid = false
        } else if (email.isEmpty()) {
            binding.emailEditText.error = "Email Tidak Boleh Kosong"
            isValid = false
        } else if (password.isEmpty()) {
            binding.passwordEditText.error = "Password Tidak boleh kosong"
            isValid = false
        }
        return isValid
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title, name, nameEdit, email, emailEdit, password, passwordEdit, signup
            )
            start()
        }
    }
}