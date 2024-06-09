package com.andryan.storyapp.ui.fragment.register

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.R
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.databinding.FragmentRegisterBinding
import com.andryan.storyapp.ui.views.edittext.NameEditText
import com.andryan.storyapp.ui.views.edittext.EmailEditText
import com.andryan.storyapp.ui.views.edittext.PasswordEditText
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalPagingApi
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        setButtonState()

        binding?.edRegisterName?.addTextChangedListener(object : TextWatcher {
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

        binding?.edRegisterEmail?.addTextChangedListener(object : TextWatcher {
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

        binding?.edRegisterPassword?.addTextChangedListener(object : TextWatcher {
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

        binding?.buttonRegister?.setOnClickListener {
            registerAccount()
        }
    }

    private fun playAnimation() {
        val tvRegisterTitle =
            ObjectAnimator.ofFloat(binding?.tvRegisterTitle, View.ALPHA, 1f).setDuration(100)
        val tvRegisterName =
            ObjectAnimator.ofFloat(binding?.tvRegisterName, View.ALPHA, 1f).setDuration(100)
        val tilRegisterName =
            ObjectAnimator.ofFloat(binding?.tilRegisterName, View.ALPHA, 1f).setDuration(100)
        val tvRegisterEmail =
            ObjectAnimator.ofFloat(binding?.tvRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val tilRegisterEmail =
            ObjectAnimator.ofFloat(binding?.tilRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val tvRegisterPassword =
            ObjectAnimator.ofFloat(binding?.tvRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val tilRegisterPassword =
            ObjectAnimator.ofFloat(binding?.tilRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val buttonRegister =
            ObjectAnimator.ofFloat(binding?.buttonRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                tvRegisterTitle,
                tvRegisterName,
                tilRegisterName,
                tvRegisterEmail,
                tilRegisterEmail,
                tvRegisterPassword,
                tilRegisterPassword,
                buttonRegister
            )
            startDelay = 100
        }.start()
    }

    private fun nameValidation(editText: NameEditText): Boolean {
        val text = editText.text.toString().trim()

        return text.isNotEmpty()
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
        val isValid: Boolean = binding?.edRegisterName?.let { nameValidation(it) } == true &&
                binding?.edRegisterEmail?.let { emailValidation(it) } == true &&
                binding?.edRegisterPassword?.let { passwordValidation(it) } == true

        binding?.buttonRegister?.isEnabled = isValid
    }

    private fun registerAccount() {
        val name = binding?.edRegisterName?.text.toString().trim()
        val email = binding?.edRegisterEmail?.text.toString().trim()
        val password = binding?.edRegisterPassword?.text.toString().trim()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userRegister(name, email, password).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        setLoadingState(true)
                    }

                    is Result.Success -> {
                        setLoadingState(false)

                        Toast.makeText(
                            context,
                            resources.getString(R.string.register_success_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Error -> {
                        setLoadingState(false)

                        binding?.let {
                            Snackbar.make(
                                it.root,
                                result.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        Timber.tag(TAG).d("userRegister: %s", result.error)
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding?.containerProgressbarRegister?.visibility = View.VISIBLE
        } else {
            binding?.containerProgressbarRegister?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}