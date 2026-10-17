package com.example.lapilearn.utils

import android.util.Patterns

fun String.isValidEmail(): Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}