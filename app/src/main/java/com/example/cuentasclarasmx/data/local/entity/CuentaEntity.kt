package com.example.cuentasclarasmx.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cuentas")
data class CuentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: String, // efectivo / debito / banco / credito / inversion / prestamo / otro
    val esPasivo: Boolean, // true si es crédito/préstamo (deuda)
    val saldoInicial: Double,
    val limiteCredito: Double? = null,
    val archivada: Boolean = false
)
