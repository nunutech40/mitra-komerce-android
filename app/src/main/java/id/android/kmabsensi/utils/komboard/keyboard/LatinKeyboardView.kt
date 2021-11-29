/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.android.kmabsensi.utils.komboard.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.inputmethodservice.Keyboard
import android.inputmethodservice.Keyboard.Key
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.view.inputmethod.InputMethodSubtype
import id.android.kmabsensi.R

class LatinKeyboardView : KeyboardView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun onLongPress(key: Key): Boolean {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            onKeyboardActionListener.onKey(KEYCODE_OPTIONS, null)
            return true
        } else if (key.codes[0] == '0'.toInt()) {
            onKeyboardActionListener.onKey('+'.toInt(), null)
            return true
        } else if (key.codes[0] == 'q'.toInt() || key.codes[0] == 'Q'.toInt()) {
            onKeyboardActionListener.onKey('1'.toInt(), null);
            return true;
        } else if (key.codes[0] == 'w'.toInt() || key.codes[0] == 'W'.toInt()) {
            onKeyboardActionListener.onKey('2'.toInt(), null);
            return true
        } else if (key.codes[0] == 'e'.toInt() || key.codes[0] == 'E'.toInt()) {
            onKeyboardActionListener.onKey('3'.toInt(), null);
            return true;
        } else if (key.codes[0] == 'r'.toInt() || key.codes[0] == 'R'.toInt()) {
            onKeyboardActionListener.onKey('4'.toInt(), null);
            return true;
        } else if (key.codes[0] == 't'.toInt() || key.codes[0] == 'T'.toInt()) {
            onKeyboardActionListener.onKey('5'.toInt(), null);
            return true;
        } else if (key.codes[0] == 'y'.toInt() || key.codes[0] == 'Y'.toInt()) {
            onKeyboardActionListener.onKey('6'.toInt(), null);
            return true;
        } else if (key.codes[0] == 'u'.toInt() || key.codes[0] == 'U'.toInt()) {
            onKeyboardActionListener.onKey('7'.toInt(), null);
            return true;
        } else if (key.codes[0] == 'i'.toInt() || key.codes[0] == 'I'.toInt()) {
            onKeyboardActionListener.onKey('8'.toInt(), null);
            return true;
        } else if (key.codes[0] == 'o'.toInt() || key.codes[0] == 'O'.toInt()) {
            onKeyboardActionListener.onKey('9'.toInt(), null);
            return true;
        } else if (key.codes[0] == 'p'.toInt() || key.codes[0] == 'P'.toInt()) {
            onKeyboardActionListener.onKey('0'.toInt(), null);
            return true;
        } else {
            return super.onLongPress(key)
        }
    }

    internal fun setSubtypeOnSpaceKey(subtype: InputMethodSubtype) {
        val keyboard = keyboard as LatinKeyboard
        keyboard.setSpaceIcon(resources.getDrawable(subtype.iconResId))
        invalidateAllKeys()
    }

    var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Add 123 numbers to labels of top row.
     *
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = resources.getDimension(R.dimen.canvasTextSize)
        val keyYLocation = resources.getDimensionPixelSize(R.dimen.canvasKeyY)
        paint.color = resources.getColor(R.color.cl_grey_dark_tx_new)
        //get all your keys and draw whatever you want
        val keys = keyboard.keys
        for (key in keys) {
            if (key.label != null) {
                if (key.label.toString() == "q" || key.label.toString() == "Q") canvas.drawText(
                    1.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "w" || key.label.toString() == "W") canvas.drawText(
                    2.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "e" || key.label.toString() == "E") canvas.drawText(
                    3.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "r" || key.label.toString() == "R") canvas.drawText(
                    4.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "t" || key.label.toString() == "T") canvas.drawText(
                    5.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "y" || key.label.toString() == "Y") canvas.drawText(
                    6.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "u" || key.label.toString() == "U") canvas.drawText(
                    7.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "i" || key.label.toString() == "I") canvas.drawText(
                    8.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "o" || key.label.toString() == "o") canvas.drawText(
                    9.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                ) else if (key.label.toString() == "p" || key.label.toString() == "P") canvas.drawText(
                    0.toString(),
                    (key.x + key.width / 2 + 10).toFloat(),
                    (key.y + keyYLocation).toFloat(),
                    paint
                )
            }
        }
    }

    companion object {

        internal val KEYCODE_OPTIONS = -100

        // TODO: Move this into android.inputmethodservice.Keyboard
        internal val KEYCODE_LANGUAGE_SWITCH = -101
        internal val KEYCODE_CALCULATOR = -102
        internal val KEYCODE_CEK_RESI = -103
        internal val KEYCODE_ORDERKU = -104
        internal val KEYCODE_CEK_ONGKIR = -105
        internal val KEYCODE_LEADS = -106
        internal val KEYCODE_BUTTON_CEK_RESI = -107
        internal val KEYCODE_EMOTICON = -110
        internal val KEYCODE_CHECKLIST = -111
    }
}
