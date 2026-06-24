package com.example.cuentasclarasmx.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "presupuestos_mensuales",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["subcategoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subcategoriaId", "mes"], unique = true)]
)
data class PresupuestoMensualEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subcategoriaId: Long,
    val mes: String, // Formato AAAA-MM
    val montoAsignado: Double,
    val arrastre: Double
)
