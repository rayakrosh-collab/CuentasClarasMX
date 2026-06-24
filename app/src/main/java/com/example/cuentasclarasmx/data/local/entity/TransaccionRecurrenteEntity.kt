package com.example.cuentasclarasmx.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transacciones_recurrentes",
    foreignKeys = [
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["cuentaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["subcategoriaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["cuentaId"]),
        Index(value = ["subcategoriaId"])
    ]
)
data class TransaccionRecurrenteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: String, // ingreso / gasto
    val monto: Double,
    val descripcion: String,
    val cuentaId: Long,
    val subcategoriaId: Long? = null,
    val frecuencia: String, // mensual / semanal / etc.
    val diaDelMes: Int,
    val activa: Boolean = true
)
