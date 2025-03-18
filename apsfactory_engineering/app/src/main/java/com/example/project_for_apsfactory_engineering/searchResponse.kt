package com.example.project_for_apsfactory_engineering

data class SearchResponse(
    val total: Int,
    val objectIDs: List<Int>? // Poate fi null, deci trebuie verificat când îl folosești
)