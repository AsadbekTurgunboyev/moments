package com.example.lahza.utils

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import com.example.lahza.R

object  CreateDialog {

    fun showDialog(context: Context): Dialog{
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)

        return dialog
    }
}