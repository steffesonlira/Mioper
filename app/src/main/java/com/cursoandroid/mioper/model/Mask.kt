package com.cursoandroid.mioper.model

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

object Mask {
    const val FORMAT_CPF = "###.###.###-##"
    const val FORMAT_FONE = "(##)#####-####"
    const val FORMAT_CEP = "#####-###"
    const val FORMAT_DATE = "##/##/####"
    const val FORMAT_HOUR = "##:##"

    fun mask(ediTxt: EditText, mask: String): TextWatcher {
        return object : TextWatcher {
            var isUpdating = false
            var old = ""

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = unmask(s.toString())
                if (isUpdating) {
                    old = str
                    isUpdating = false
                    return
                }
                val mascara = StringBuilder()
                var i = 0
                for (m in mask) {
                    if (m != '#' && str.length > old.length) {
                        mascara.append(m)
                        continue
                    }
                    if (i >= str.length) break
                    mascara.append(str[i])
                    i++
                }
                isUpdating = true
                ediTxt.setText(mascara)
                ediTxt.setSelection(mascara.length)
            }
        }
    }

    fun unmask(s: String): String {
        return s.replace(Regex("[.\\-/( :)]"), "")
    }
}
