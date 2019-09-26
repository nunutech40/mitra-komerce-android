package id.android.kmabsensi.utils.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import id.android.kmabsensi.R

class MyDialog(val context: Context) {

    private var dialog: Dialog = Dialog(context)

    init {
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        dialog.window?.setContentView(R.layout.progress_dialog)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun setCancelable(cancelable: Boolean) {
        dialog.setCancelable(cancelable)
    }


    fun setCanceledOnTouchOutside(flag: Boolean) {
        dialog.setCanceledOnTouchOutside(flag)
    }

//    fun setColor(colour: Int) {
//        progressBar.indeterminateDrawable.setColorFilter(colour, android.graphics.PorterDuff.Mode.MULTIPLY)
//    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }
}