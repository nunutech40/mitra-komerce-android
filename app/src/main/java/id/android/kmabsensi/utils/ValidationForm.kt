package id.android.kmabsensi.utils

import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

object ValidationForm {

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(

        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun validationEmail(email: EditText, errormsg: String): Boolean {
        val emailText = email.text.toString()
        if (!EMAIL_ADDRESS_PATTERN.matcher(emailText).matches()) {
            email.error = errormsg
            email.requestFocus()
            return false
        } else {
            return true
        }

    }

    fun validationEmail(edt: TextInputEditText, til: TextInputLayout, errormsg: String): Boolean {
        val emailText = edt.text.toString()
        if (!EMAIL_ADDRESS_PATTERN.matcher(emailText).matches()) {
            til.error = errormsg
            til.requestFocus()
            return false
        } else {
            return true
        }
    }

    fun validationPassword(password: EditText, errormsg: String):Boolean{
        //val passwordText = password.text.toString()
        if (password.toString().trim().length<6){
            password.error = errormsg
            password.requestFocus()
            return false
        }else{
            return true
        }
    }

    fun validationSingkronPassword(password: EditText, konfir: EditText, errormsg: String):Boolean{
        val passwordText = password.text.toString()
        val konfirText = konfir.text.toString()
        if (!passwordText.equals (konfirText)){
            konfir.error = errormsg
            konfir.requestFocus()
            return false
        }else{
            return true
        }
    }

    fun validationInput(edt: EditText, errormsg: String): Boolean {
        if (TextUtils.isEmpty(edt.text.toString())) {
            edt.error = errormsg
            edt.requestFocus()
            return false
        } else {
            return true
        }
    }

    fun validationTextInputEditText(edt: TextInputEditText, til : TextInputLayout, errormsg: String): Boolean {
        if (TextUtils.isEmpty(edt.text.toString())) {
            til.error = errormsg
            til.requestFocus()
            return false
        } else {
            return true
        }
    }

}

fun AppCompatAutoCompleteTextView.validateAutoComplete(et: TextInputLayout, msg: String): Boolean{
    var result = false
    if (this.text.toString() == ""){
        et.requestFocus()
        et.error = msg
    }else result = true

    return result
}