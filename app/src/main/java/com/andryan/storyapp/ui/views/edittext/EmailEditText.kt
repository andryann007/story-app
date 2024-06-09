package com.andryan.storyapp.ui.views.edittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.andryan.storyapp.R

class EmailEditText : AppCompatEditText {
    private lateinit var drawableIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        drawableIcon = ContextCompat.getDrawable(context, R.drawable.ic_email_black_24) as Drawable
        compoundDrawablePadding = 24
        setDrawable(drawableIcon)

        hint = resources.getString(R.string.email_hint)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if (s.toString().isEmpty()) {
                    resources.getString(R.string.email_empty_error)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    resources.getString(R.string.email_format_error)
                } else {
                    null
                }

                if (error != null) {
                    requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}