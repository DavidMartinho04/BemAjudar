package com.example.bemajudar.presentation.viewmodels

import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    // Dados do primeiro formulário
    var photoUrl: String? = null // URL da imagem de perfil do utilizador
    var name: String = "" // Nome do utilizador
    var birthDate: String = "" // Data de nascimento do utilizador
    var phone: String = "" // Número de telemóvel do utilizador
    var email: String = "" // Endereço de email do utilizador
    var password: String = "" // Palavra-passe do utilizador

    // Dados do segundo formulário
    var address: String = "" // Morada do utilizador
    var postalCode: String = "" // Código postal do utilizador
    var gender: String = "Masculino" // Género do utilizador, com valor padrão "Masculino"
    var userType: String = "Gestor" // Tipo de utilizador, com valor padrão "Gestor"

    // Função para atualizar os dados do primeiro formulário
    fun updateUserData(
        photoUrl: String?, // URL da imagem de perfil
        name: String, // Nome do utilizador
        birthDate: String, // Data de nascimento
        phone: String, // Número de telemóvel
        email: String, // Endereço de email
        password: String // Palavra-passe
    ) {
        this.photoUrl = photoUrl
        this.name = name
        this.birthDate = birthDate
        this.phone = phone
        this.email = email
        this.password = password
    }

    // Função para atualizar os dados do segundo formulário
    fun updateFinalizeData(
        address: String, // Morada do utilizador
        postalCode: String, // Código postal
        gender: String, // Género do utilizador
        userType: String // Tipo de utilizador
    ) {
        this.address = address
        this.postalCode = postalCode
        this.gender = gender
        this.userType = userType
    }
}
