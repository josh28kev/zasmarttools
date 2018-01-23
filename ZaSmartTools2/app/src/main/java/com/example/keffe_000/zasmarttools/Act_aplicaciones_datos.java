package com.example.keffe_000.zasmarttools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

public class Act_aplicaciones_datos extends AppCompatActivity {

    private String dato_actual = "";
    private boolean nueva_escritura = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_aplicaciones_datos);

        Intent callingIntent = getIntent();
        String aplicacion = callingIntent.getStringExtra("aplicación");
        Double tamaño = callingIntent.getDoubleExtra("tamaño",0.0);
        TextView texto1 = (TextView)  findViewById(R.id.textViewApData);
        texto1.setText(aplicacion.toUpperCase());
        TextView texto2 = (TextView)  findViewById(R.id.textViewApTam);
        texto2.setText(Double.toString(tamaño) + " KB");
        //Mensaje(Double.toString(tamaño));
        String[] fechaaux = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()).split("/");
        String fecha = fechaaux[2]+"/"+fechaaux[0]+"/"+fechaaux[1];
        //Mensaje(fecha);

        dato_actual=aplicacion+","+Double.toString(tamaño)+","+fecha+";";

        String escritura = busquedaDatoExistente(dato_actual);
        if(escritura != "") {
            texto2.setText(escritura);
        }
        if(nueva_escritura) {
            escribirAppend(dato_actual);
        }
        //Mensaje(escritura);
    }

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

    public void escribirAppend(String s) {
        try {
            FileOutputStream fOut =
                    openFileOutput("ApDatos", MODE_APPEND);
            // si quiere agregar al archivo cambie el mode a append.
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            //---escribimos en el archivo--
            osw.write(s);
            osw.close();
            //---Desplegamos la linea escrita---
            Mensaje("¡Nuevo registro salvado!");
        } catch (IOException ioe) {
            Mensaje("Registro nuevo");
            ioe.printStackTrace();
        }
    }

    public String busquedaDatoExistente(String str) {
        String[] datos = str.split(",");
        final int Tam_bloque_lectura = 100;

        try
        {
            FileInputStream fIn = openFileInput("ApDatos");
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[Tam_bloque_lectura];
            String s = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer))>0)
            {
                //---convert the chars to a String---
                String readString =
                        String.copyValueOf(inputBuffer, 0,
                                charRead);
                s += readString;
                inputBuffer = new char[Tam_bloque_lectura];
            }
            isr.close();
            //Mensaje("Texto en archivo: \n" + s);

            String[] datos_archivo = s.split(";");///////////////////SE ESCRIBE (BANDERA BOOLEAN) AL FINAL
            String datos_retornados = "";
            //Mensaje("Length: "+Integer.toString(datos_archivo.length));
            for(int i = 0; i < datos_archivo.length; i++) {
                String[] datos_archivo_2 = datos_archivo[i].split(",");
                //Mensaje(datos_archivo_2[0] + "--" + dato_actual.split(",")[0]);
                //if(datos_archivo_2[0] == dato_actual.split(",")[0]) { // Si se trata de la misma aplicación...
                if(datos_archivo_2[0].equals(dato_actual.split(",")[0])) { // Si se trata de la misma aplicación...
                    //Mensaje(datos_archivo_2[2]+"--"+dato_actual.split(",")[0]);
                    if(! datos_archivo_2[2].equals(dato_actual.split(",")[2].split(";")[0])) { // Si el archivo no es de la misma fecha...
                        datos_retornados = datos_retornados + "20"+datos_archivo_2[2] + " → " + datos_archivo_2[1] + " KB" + "\n"; // Se añade a lo que se mostrará en pantalla.
                        // Mensaje("I "+datos_archivo_2[2]+"--"+dato_actual.split(",")[2]);
                    } else { // Si es de la misma fecha...
                        datos_retornados = datos_retornados + "20"+datos_archivo_2[2] + " → " + datos_archivo_2[1] + " KB";
                        nueva_escritura = false;
                        //Mensaje("F"+datos_archivo_2[2]+"--"+dato_actual.split(",")[2]);
                        return datos_retornados; //Como se encontró a sí mismo, es el final de la búsqueda.
                    }
                } // Si no es la misma aplicación se continúa revisando
            }
            nueva_escritura = true;
            return datos_retornados;
        }

        catch (IOException ioe) { // Se escribe si no hay archivo
            ioe.printStackTrace();
            Mensaje("Error abriendo archivo");
            escribirAppend(dato_actual);
            return "";
        }

    }
}

