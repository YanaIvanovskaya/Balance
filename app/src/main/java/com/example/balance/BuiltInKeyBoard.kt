package com.example.balance

import android.widget.Button
import android.widget.EditText

interface BuiltInKeyBoard {
    var passcodeField: EditText?

    fun onClickNumber(number: String) {
        passcodeField?.text = passcodeField?.text?.append(number)
    }

    fun onClickClear() {
        val passcode = passcodeField?.text.toString().trim()
        val newPasscode = if (passcode.isNotEmpty()) passcode.dropLast(1) else ""
        passcodeField?.setText(newPasscode)
    }

    fun connectKeyBoardClickListeners(keyBoard : List<Button?>) {
        for (button in keyBoard) {
            val number = button?.text.toString()
            button?.setOnClickListener { onClickNumber(number) }
        }
    }
}