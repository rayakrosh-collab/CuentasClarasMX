package com.example.cuentasclarasmx

import android.app.Application
import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.DefaultDataRepository
import com.example.cuentasclarasmx.data.local.AppDatabase

class CuentasClarasApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { DefaultDataRepository(database) }
}
