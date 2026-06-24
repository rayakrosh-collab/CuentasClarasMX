package com.example.cuentasclarasmx.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ajustes")
data class AjustesEntity(
    @PrimaryKey val id: Long = 1,
    val moneda: String = "MXN",
    val diaInicioMes: Int = 1,
    val preferenciasJson: String? = null
)
