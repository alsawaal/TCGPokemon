package com.ghost.tcgpokemon.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ghost.tcgpokemon.R
import com.ghost.tcgpokemon.databinding.DialogDownloadBinding

class DialogUtil(context: Context) {

    private val dialog = Dialog(context, R.style.DialogTheme)

    fun showDownloadDialog(
        context: Context,
        message: String,
        title: String = "Downloading ..."
    ) {
        try {
            // init dialog binding
            val dialogBinding = DialogDownloadBinding.inflate(
                LayoutInflater.from(context)
            )

            // set dialog content view
            with(dialog) {
                setContentView(dialogBinding.root)
                setCanceledOnTouchOutside(true)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                with(dialogBinding) {
                    tvTitle.text = title
                    tvMessage.text = message
                }

                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissDownloadDialog() {
        dialog.dismiss()
    }
}