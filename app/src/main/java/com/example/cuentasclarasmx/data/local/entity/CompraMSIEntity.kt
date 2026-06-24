package com.example.cuentasclarasmx.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "compras_msi",
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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cuentaId"]),
        Index(value = ["subcategoriaId"])
    ]
)
data class CompraMSIEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val descripcion: String,
    val montoTotal: Double,
    val numeroMeses: Int,
    val mesInicio: String, // Formato AAAA-MM
    val cuentaId: Long, // Tarjeta de crédito afectada
    val subcategoriaId: Long, // Dónde pegan las mensualidades
    val montoMensual: Double
)
