package com.example.ladm_u3_ejercicio3

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.ladm_u3_ejercicio3.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var listaIds= ArrayList<String>()
    var idActualizar=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.filtro.isVisible= false
        mostrar()

        binding.filtro.setOnClickListener{
            mostrar()
            binding.filtro.isVisible= false
        }

        binding.mostrar.setOnItemClickListener { adapterView, view, itemSeleccionado, l ->
            var idSeleccionado= listaIds.get(itemSeleccionado)

            AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("¿QUE DESEA HACER?")
                .setPositiveButton("Eliminar"){d,i->
                    eliminar(idSeleccionado)
                }
                .setNeutralButton("Actualizar"){d,i->
                    actualizar(idSeleccionado)
                }
                .setNegativeButton("Nada"){d,i->}
                .show()
        }

        binding.buscar.setOnClickListener {
            if(binding.buscar.text.toString().startsWith("CANCE")){
                binding.nombre.setText("")
                binding.correo.setText("")
                binding.edad.setText("")
                binding.insertar.setText("INSERTAR")
                binding.buscar.setText("BUSCAR")
                idActualizar=""
                return@setOnClickListener
            }
            //HACER UNA VENTANITA COMO EN XML
            var layin=  LinearLayout(this)
            var comboCampos= Spinner(this)
            var itemCampo= ArrayList<String>()
            var claveBusqueda= EditText(this)

            itemCampo.add("NOMBRE")
            itemCampo.add("CORREO")
            itemCampo.add("EDAD <")
            itemCampo.add("EDAD ==")
            itemCampo.add("EDAD >")

            comboCampos.adapter= ArrayAdapter<String>(this,
                R.layout.simple_list_item_1,itemCampo)

            layin.orientation=LinearLayout.VERTICAL
            claveBusqueda.setHint("CLAVE A BUSCAR")
            layin.addView(comboCampos)
            layin.addView(claveBusqueda)
            AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("ELIJA CAMPO PARA BUSQUEDA")
                .setView(layin)
                .setPositiveButton("BUSCAR"){d,i->
                    binding.filtro.isVisible= true
                    consulta(comboCampos,claveBusqueda) }
                .setNeutralButton("CANCELAR"){d,i->}
                .show()

        }

        binding.insertar.setOnClickListener {
            var datos = hashMapOf(
                "nombre" to binding.nombre.text.toString(),
                "correo" to binding.correo.text.toString(),
                "edad" to binding.edad.text.toString().toInt(),
                "registrado" to Date()
            )

            FirebaseFirestore.getInstance().collection("personas") //LA COLECCION SERIA COMO UNA TABLA PERSONA
                .add(datos) //una cantidad de documentos es una coleccion
                //eventos asincronos
                .addOnSuccessListener {
                    toast("SE INSERTO CON EXITO")
                    binding.nombre.setText("")
                    binding.edad.setText("")
                    binding.correo.setText("")

                }
                .addOnFailureListener {
                    aler(it.message!!)

                }
        }//setOnClickListener

        /*if(binding.insertar.text.toString().startsWith("actual")){
            actualizar()
            return@setOnClickListener
        }*/
    }

    private fun mostrar() {
        FirebaseFirestore.getInstance()
            .collection("personas")
            .addSnapshotListener { value, error ->
                if(error!=null){
                    aler("NO SE PUDO REALIZAR LA CONSULTA")
                    return@addSnapshotListener
                }
                var lista= ArrayList<String>() //lista se comporta como un curso
                listaIds.clear()

                for(documento in value!!){
                    val cadena= documento.getString("nombre")+"\n"+
                            documento.get("edad").toString()+"--"+
                            documento.getString("correo")
                    lista.add(cadena)
                    listaIds.add(documento.id)
                }
                binding.mostrar.adapter=ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,lista)
            }
    }

    private fun consulta(comboCampos: Spinner, claveBusqueda: EditText) {
        var posicionCampoSelec=comboCampos.selectedItemId.toInt()
        when(posicionCampoSelec){
            0->{ //NOMBRE
                FirebaseFirestore.getInstance()
                    .collection("personas")
                    .whereEqualTo("nombre",claveBusqueda.text.toString())
                    .get()
                    .addOnSuccessListener {
                        var resultado= ArrayList<String>()
                        for (documento in it!!){
                            var cad=documento.getString("nombre")
                            resultado.add(cad!!)
                        }
                        binding.mostrar.adapter=ArrayAdapter<String>(
                            this,android.R.layout.simple_list_item_1,resultado )
                    }
            }
            1->{ //CORREO
                FirebaseFirestore.getInstance()
                    .collection("personas")
                    .addSnapshotListener { value, error->
                        var resultado= ArrayList<String>()
                        for (documento in value!!){
                            if(documento.getString("correo").toString().contains(claveBusqueda.text.toString())){
                                var cad=documento.getString("nombre") //se recuperaran nombres
                                resultado.add(cad!!)
                            }
                        }
                        binding.mostrar.adapter=ArrayAdapter<String>(
                            this,android.R.layout.simple_list_item_1,resultado )
                    }

            }
            2->{ //EDAD <
                FirebaseFirestore.getInstance()
                    .collection("personas")
                    .whereLessThan("edad",claveBusqueda.text.toString())
                    .get()
                    .addOnSuccessListener {
                        var resultado= ArrayList<String>()
                        for (documento in it!!){
                            var cad=documento.getString("nombre")
                            resultado.add(cad!!)
                        }
                        binding.mostrar.adapter=ArrayAdapter<String>(
                            this,android.R.layout.simple_list_item_1,resultado )
                    }

            }
            3->{ //EDAD ==

            }
            4->{ //EDAD >

            }
        }

    }

    private fun eliminar(idSeleccionado: String) {
        FirebaseFirestore.getInstance()
            .collection("personas")
            .document(idSeleccionado)
            .delete()
            .addOnSuccessListener {
                toast("SE BORRO")
            }
            .addOnFailureListener {
                aler(it.message!!)
            }

    }

    private fun actualizar(idSeleccionado: String) {
        idActualizar=idSeleccionado
        FirebaseFirestore.getInstance()
            .collection("personas")
            .document(idSeleccionado)
            .get()
            .addOnSuccessListener {
                //recuperar la data
                binding.nombre.setText(it.getString("nombre"))
                binding.correo.setText(it.getString("correo"))
                binding.edad.setText(it.get("edad").toString())
                //cambiar los nombres de botones
                binding.insertar.setText("ACTUALIZAR")
                binding.buscar.setText("CANCELAR ACTUALIZAR")
            }

    }

    private fun actualizar2(){
        FirebaseFirestore.getInstance()
            .collection("personas")
            .document(idActualizar)
            .update("nombre",binding.nombre.text.toString(),"correo",binding.correo.text.toString(),
                "edad",binding.edad.text.toString().toInt())
            .addOnSuccessListener {
                toast("SE ACTUALIZO CON EXITO")
                binding.nombre.setText("")
                binding.correo.setText("")
                binding.edad.setText("")
                binding.insertar.setText("INSERTAR")
                binding.buscar.setText("BUSCAR")
                idActualizar=""

            }
            .addOnFailureListener { aler(it.message!!) }
    }

    fun toast(m:String){
        Toast.makeText(this,m,Toast.LENGTH_LONG).show()
    }
    fun aler(m:String){
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(m)
            .setPositiveButton("OK"){d,i->}
            .show()
    }

}