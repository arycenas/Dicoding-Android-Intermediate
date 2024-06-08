package com.intermediate.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.databinding.ActivityLoginBinding
import com.intermediate.storyapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
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

    private fun setupViewModel() {
        viewModel.userModel().observe(this) { userModel ->
            val model = UserModel(userModel.email, userModel.token, userModel.isLogin)
            viewModel.saveSession(model)
        }
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
            } else {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText
            val password = binding.passwordEditText
            if (email.error !== null || password.error !== null) {
                Toast.makeText(
                    this, "Email dan password tidak valid", Toast.LENGTH_SHORT
                ).show()
            } else if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
                when {
                    email.text.toString().isEmpty() -> email.error = "Email kosong"
                    password.text.toString().isEmpty() -> password.error = "Password kosong"
                }
            } else {
                viewModel.login(email.text.toString(), password.text.toString())
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, message, email, emailEdit, password, passwordEdit, login)
            start()
        }
    }
}