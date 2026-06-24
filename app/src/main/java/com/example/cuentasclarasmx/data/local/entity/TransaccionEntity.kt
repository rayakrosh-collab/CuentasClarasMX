package com.example.cuentasclarasmx.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transacciones",
    foreignKeys = [
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["cuentaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["cuentaDestinoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["subcategoriaId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CompraMSIEntity::class,
            parentColumns = ["id"],
            childColumns = ["compraMsiId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TransaccionRecurrenteEntity::class,
            parentColumns = ["id"],
            childColumns = ["recurrenteId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["cuentaId"]),
        Index(value = ["cuentaDestinoId"]),
        Index(value = ["subcategoriaId"]),
        Index(value = ["compraMsiId"]),
        Index(value = ["recurrenteId"])
    ]
)
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: String, // ingreso / gasto / transferencia
    val fecha: Long, // timestamp en milisegundos
    val monto: Double,
    val descripcion: String,
    val cuentaId: Long, // cuenta de origen (o afectada en ingresos/gastos)
    val cuentaDestinoId: Long? = null, // cuenta de destino (solo transferencias)
    val subcategoriaId: Long? = null, // null en transferencias
    val fotoReciboUri: String? = null,
    val compraMsiId: Long? = null,
    val recurrenteId: Long? = null
)
