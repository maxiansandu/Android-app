package com.example.project_for_apsfactory_engineering



data class ObjectResponse(
    val primaryImage: String?,  // Imaginea principală
    val additionalImages: List<String>?,  // Lista de imagini suplimentare
    val title: String?,  // Titlul obiectului
    val artistDisplayName: String?,  // Numele artistului
    val department: String?,  // Departamentul
    val objectName: String?  // Numele obiectului
)


