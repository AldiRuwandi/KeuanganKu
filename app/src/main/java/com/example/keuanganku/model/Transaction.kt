package com.example.keuanganku.model

import com.google.firebase.Timestamp

data class Transaction(
    var id: String?,
    var username: String,
    var category: String,
    var type: String,
    var amount: Int,
    var note: String,
    var created: Timestamp? = Timestamp.now()
)
