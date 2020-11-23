package example.CustomKeyboard.keyboard

import example.CustomKeyboard.controllers.KeyboardController

interface KeyboardListener {
    fun characterClicked(c: Char)
    fun specialKeyClicked(key: KeyboardController.SpecialKey)
}