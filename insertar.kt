package com.example.ladm_u3_ejercicio1_sqllite

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity : AppCompatActivity() {
    var basedatos = BaseDatos(this, "Ejemplo1", null, 1)
    var IDs = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mostrarTodos()

        insertar.setOnClickListener {
            var persona = basedatos.writableDatabase
            var datos = ContentValues()
            datos.put("NOMBRE", nombre.text.toString())
            datos.put("DOMICILIO", domicilio.text.toString())
            datos.put("SUELDO", sueldo.text.toString().toFloat())

            var resultado = persona.insert("PERSONA", "ID", datos)
            if (resultado == -1L) {
                AlertDialog.Builder(this).setTitle("error")
                    .setMessage("NO SE PUDO GUARDAR").show()
            } else {
                Toast.makeText(this, "se insertaron", Toast.LENGTH_LONG)
                    .show()
                nombre.setText("")
                domicilio.setText("")
                sueldo.setText("")
                mostrarTodos()
            }
        }
        //boton consultar
        consultar.setOnClickListener{
            consulta();

        }

    }

    private fun consulta() {
        try {
           var persona=basedatos.readableDatabase
            var nombreBuscar=nombre.text.toString()

            var cursor=persona.query("PERSONA"
                , arrayOf("DOMICILIO","SUELDO")
                ,"NOMBRE=?"
                ,arrayOf(nombreBuscar)
                ,null,null,null
            )
            if (cursor.moveToFirst()){
                muestraConsulta.setText("DOMICILIO: ${cursor.getString(0)}\n" +
                        "SUELDO: ${cursor.getString(1)}")
            }else{
                mensaje("error no se encontro el resultado")
            }
            persona.close()
        }catch (err:SQLiteException){

        }
    }
    fun mensaje(m:String){
        AlertDialog.Builder(this).setTitle("atencion").setMessage(m)
            .setPositiveButton("ok"){d,i->}
            .show()
    }

    fun mostrarTodos() {
        var persona = basedatos.readableDatabase
        val lista = ArrayList<String>()
        IDs.clear()

        var resultado = persona.query("PERSONA", arrayOf("*"), null, null, null, null, null)
        if (resultado.moveToFirst()) {
            do {
                val data =
                    resultado.getString(1) + "\n" + resultado.getString(2) + "\n$ " + resultado.getFloat(
                        3
                    )
                lista.add(data)
                IDs.add(resultado.getInt(0))
            } while (resultado.moveToNext())
        } else {
            lista.add("LA TABLA ESTA VACIA")
        }

        listaPersonas.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, lista

        )
        listaPersonas.setOnItemClickListener { adapterView, view, i, l ->
            val idSeleccionado = IDs.get(i)
            var nombre = lista.get(i)
            nombre = nombre.substring(0, nombre.indexOf("\n")).uppercase()


            AlertDialog.Builder(this)
                .setTitle("ATENCION")
                .setMessage("QUE DESEAS HACER CON: ${nombre}?")
                .setPositiveButton("NADA") { d, i -> }
                .setNegativeButton("ELIMINAR") { d, i ->
                    eliminar(idSeleccionado)
                }
                .setNeutralButton("ACTUALIZAR") { d, i ->
                    actualizar(idSeleccionado)
                }
                .show()

        }
    }
    private fun actualizar(idSeleccionado: Int){
        val otraVentana=Intent(this,MainActivity2::class.java)
        otraVentana.putExtra("idSeleccionado",idSeleccionado.toString())//enviar datos de una ventana a otra
        startActivity(otraVentana)
    }

    private fun eliminar(idSeleccionado: Int) {
        var resultado=basedatos.writableDatabase.delete("PERSONA","ID=?", arrayOf(idSeleccionado.toString()))
        if (resultado==0){
            AlertDialog.Builder(this).setMessage("ERROR NO SE BORRO").show()
        }else{
            Toast.makeText(this,"se borro con exito",Toast.LENGTH_LONG).show()
            mostrarTodos()
        }
    }


    override fun onRestart() {
        super.onRestart()
        mostrarTodos()
    }
}