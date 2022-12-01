package com.example.ladm_u3_ejercicio1_sqllite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        /*se ejecuta cuando se "ejecuta  x primera vez" la app
        dentreo del celucar del cliente y contruye la base de datos
        y las tablas
//         */
//        db.execSQL("create table persona()")
//        db.execSQL("insert into persona values (9,"JORGE")")
//        db.rawQuery("select")
//        db.insert()
//        db.query()
//        db.update()
//        db.delete()

        db.execSQL("create table PERSONA(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NOMBRE VARCHAR(400)," +
                "DOMICILIO VARCHAR(300)," +
                "SUELDO FLOAT)")

    }

    override fun onUpgrade(p0: SQLiteDatabase?, anterior: Int, nueva: Int) {
////        si y solo si hay un cambio de version
//        la version utiliza los numeros naturales y se espera que a un cambio de version uses un
//        numero mayor que el actual

    }
}