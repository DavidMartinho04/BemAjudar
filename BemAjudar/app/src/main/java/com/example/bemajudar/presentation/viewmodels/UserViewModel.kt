package com.example.bemajudar.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    // Dados do primeiro formulário
    var photoUri: Uri? = null
    var name: String = ""
    var birthDate: String = ""
    var phone: String = ""
    var email: String = ""
    var password: String = ""

    // Dados do segundo formulário
    var address: String = ""
    var postalCode: String = ""
    var gender: String = "Masculino" // Valor padrão
    var userType: String = "Gestor" // Valor padrão

    // Função para atualizar os dados do primeiro formulário
    fun updateUserData(
        photoUri: Uri?,
        name: String,
        birthDate: String,
        phone: String,
        email: String,
        password: String
    ) {
        this.photoUri = photoUri
        this.name = name
        this.birthDate = birthDate
        this.phone = phone
        this.email = email
        this.password = password
    }

    // Função para atualizar os dados do segundo formulário
    fun updateFinalizeData(
        address: String,
        postalCode: String,
        gender: String,
        userType: String
    ) {
        this.address = address
        this.postalCode = postalCode
        this.gender = gender
        this.userType = userType
    }
}
