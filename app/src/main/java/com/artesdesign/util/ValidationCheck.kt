package com.artesdesign.util

import android.util.Patterns

fun validateEmail(email : String):RegisterValidation{
    if(email.isEmpty())
        return RegisterValidation.Failed("E-posta boş olamaz")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Yanlış e-posta biçimi")

    return RegisterValidation.Success
}

fun validatePassword (password:String):RegisterValidation {
    if(password.isEmpty())
        return RegisterValidation.Failed("Şifre boş olamaz")
    if(password.length < 6)
        return RegisterValidation.Failed("Şifre en az 6 karakter içermelidir")

    return RegisterValidation.Success


}