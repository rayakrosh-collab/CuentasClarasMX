package com.example.cuentasclarasmx.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categorias",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaPadreId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoriaPadreId"])]
)
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoriaPadreId: Long? = null, // null = categoria madre; con valor = subcategoria
    val presupuestoMensualDefault: Double? = null, // solo para subcategorias
    val modoArrastre: String = "reiniciar", // acumular / reiniciar
    val icono: String? = null,
    val color: String? = null
)
