package com.example.testing4.utils

import android.app.Dialog
import android.content.Context
import com.example.testing4.R

object Loader {
    private var dialog: Dialog? = null

    fun showDialog(context: Context) {
        if (dialog == null || dialog?.isShowing == false) {
            dialog = Dialog(context)
            dialog?.setContentView(R.layout.loader_dialog)
            dialog?.setCancelable(false)
            dialog?.show()
        }
    }
    fun hideDialog() {
        dialog?.dismiss()
        dialog = null
    }
}