package com.omarshehe.forminput.compose.ui.utils


fun Any?.isNotNull(): Boolean = this != null


fun String?.ifNullSetThis(default: String): String {
    return this ?: default
}

inline fun Boolean.isTrue(action: () -> Unit): Boolean {
    if (this) {
        action()
    }
    return this
}

inline fun Boolean.isNotTrue(action: () -> Unit) {
    if (!this) {
        action()
    }
}

fun Int?.isGreaterThan(number: Int): Boolean {
    return if (this.isNotNull()) {
        this!! > number
    } else {
        false
    }
}
