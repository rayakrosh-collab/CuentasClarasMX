package com.example.cuentasclarasmx.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cuentasclarasmx.data.local.dao.*
import com.example.cuentasclarasmx.data.local.entity.*

@Database(
    entities = [
        CuentaEntity::class,
        CategoriaEntity::class,
        PresupuestoMensualEntity::class,
        TransaccionEntity::class,
        CompraMSIEntity::class,
        TransaccionRecurrenteEntity::class,
        AjustesEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cuentaDao(): CuentaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun presupuestoMensualDao(): PresupuestoMensualDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun compraMSIDao(): CompraMSIDao
    abstract fun transaccionRecurrenteDao(): TransaccionRecurrenteDao
    abstract fun ajustesDao(): AjustesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cuentas_claras_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Poblar categorias madre
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (1, 'Vivienda', NULL, NULL, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (2, 'Alimentación', NULL, NULL, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (3, 'Transporte', NULL, NULL, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (4, 'Servicios', NULL, NULL, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (5, 'Salud y Bienestar', NULL, NULL, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (6, 'Educación', NULL, NULL, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (7, 'Entretenimiento', NULL, NULL, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (8, 'Otros', NULL, NULL, 'reiniciar', NULL, NULL)")

                // Poblar subcategorias de Vivienda
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (9, 'Renta / Hipoteca', 1, 5000.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (10, 'Mantenimiento', 1, 500.0, 'acumular', NULL, NULL)")

                // Poblar subcategorias de Alimentacion
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (11, 'Supermercado', 2, 3000.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (12, 'Restaurantes / Cafés', 2, 1000.0, 'reiniciar', NULL, NULL)")

                // Poblar subcategorias de Transporte
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (13, 'Gasolina', 3, 1500.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (14, 'Uber / Transporte Público', 3, 800.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (15, 'Seguro / Tenencia', 3, 500.0, 'acumular', NULL, NULL)")

                // Poblar subcategorias de Servicios
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (16, 'Luz', 4, 400.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (17, 'Agua', 4, 200.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (18, 'Internet / Celular', 4, 600.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (19, 'Suscripciones', 4, 300.0, 'reiniciar', NULL, NULL)")

                // Poblar subcategorias de Salud y Bienestar
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (20, 'Farmacia / Médicos', 5, 500.0, 'acumular', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (21, 'Gimnasio', 5, 400.0, 'reiniciar', NULL, NULL)")

                // Poblar subcategorias de Educacion
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (22, 'Cursos / Colegiaturas', 6, 1000.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (23, 'Libros / Papelería', 6, 200.0, 'acumular', NULL, NULL)")

                // Poblar subcategorias de Entretenimiento
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (24, 'Cine / Diversión', 7, 800.0, 'reiniciar', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (25, 'Ropa / Compras', 7, 500.0, 'reiniciar', NULL, NULL)")

                // Poblar subcategorias de Otros
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (26, 'Fondo de Ahorro', 8, 1000.0, 'acumular', NULL, NULL)")
                db.execSQL("INSERT INTO categorias (id, nombre, categoriaPadreId, presupuestoMensualDefault, modoArrastre, icono, color) VALUES (27, 'Imprevistos / Varios', 8, 500.0, 'reiniciar', NULL, NULL)")

                // Poblar Ajustes por defecto
                db.execSQL("INSERT INTO ajustes (id, moneda, diaInicioMes, preferenciasJson) VALUES (1, 'MXN', 1, NULL)")

            }
        }
    }
}
