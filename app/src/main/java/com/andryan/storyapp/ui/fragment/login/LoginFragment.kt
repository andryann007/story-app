package com.andryan.storyapp.ui.fragment.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.R
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.data.pref.UserModel
import com.andryan.storyapp.databinding.FragmentLoginBinding
import com.andryan.storyapp.ui.activity.main.MainActivity
import com.andryan.storyapp.ui.views.edittext.EmailEditText
import com.andryan.storyapp.ui.views.edittext.PasswordEditText
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalPagingApi
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var id: String
    private lateinit var name: String
    private lateinit var token: String
    private var isLogin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        setButtonState()

        binding?.edLoginEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        binding?.edLoginPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        binding?.buttonLogin?.setOnClickListener {
            loginAccount()
        }
    }

    private fun playAnimation() {
        val tvLoginTitle =
            ObjectAnimator.ofFloat(binding?.tvLoginTitle, View.ALPHA, 1f).setDuration(100)
        val tvLoginEmail =
            ObjectAnimator.ofFloat(binding?.tvLoginEmail, View.ALPHA, 1f).setDuration(100)
        val tilLoginEmail =
            ObjectAnimator.ofFloat(binding?.tilLoginEmail, View.ALPHA, 1f).setDuration(100)
        val tvLoginPassword =
            ObjectAnimator.ofFloat(binding?.tvLoginPassword, View.ALPHA, 1f).setDuration(100)
        val tilLoginPassword =
            ObjectAnimator.ofFloat(binding?.tilLoginPassword, View.ALPHA, 1f).setDuration(100)
        val buttonLogin =
            ObjectAnimator.ofFloat(binding?.buttonLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                tvLoginTitle,
                tvLoginEmail,
                tilLoginEmail,
                tvLoginPassword,
                tilLoginPassword,
                buttonLogin
            )
            startDelay = 100
        }.start()
    }

    private fun emailValidation(editText: EmailEditText): Boolean {
        val text = editText.text.toString().trim()

        return text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    private fun passwordValidation(editText: PasswordEditText): Boolean {
        val text = editText.text.toString().trim()

        return text.isNotEmpty() && text.length >= 8
    }

    private fun setButtonState() {
        val isValid: Boolean = binding?.edLoginEmail?.let { emailValidation(it) } == true &&
                binding?.edLoginPassword?.let { passwordValidation(it) } == true

        binding?.buttonLogin?.isEnabled = isValid
    }

    private fun loginAccount() {
        val email = binding?.edLoginEmail?.text.toString().trim()
        val password = binding?.edLoginPassword?.text.toString().trim()

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userLogin(email, password).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            setLoadingState(true)
                        }

                        is Result.Success -> {
                            setLoadingState(false)

                            id = result.data.loginResult?.userId.toString()
                            name = result.data.loginResult?.name.toString()
                            token = result.data.loginResult?.token.toString()
                            isLogin = true

                            viewModel.saveSession(UserModel(id, name, token, isLogin))

                            Toast.makeText(
                                context,
                                resources.getString(R.string.login_success_message),
                                Toast.LENGTH_SHORT
                            ).show()

                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(
                                    Intent(context, MainActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }, DELAY_TIME)
                        }

                        is Result.Error -> {
                            setLoadingState(false)

                            binding?.root?.let {
                                Snackbar.make(
                                    it,
                                    result.error,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            Timber.tag(TAG).d("userLogin: %s", result.error)
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding?.containerProgressbarLogin?.visibility = View.VISIBLE
        } else {
            binding?.containerProgressbarLogin?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val DELAY_TIME = 1000L
    }
}