package com.example.keffe_000.zasmarttools;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class Act_aplicaciones extends AppCompatActivity {

    private String[] aplicaciones;
    private String[] datos_aplicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_aplicaciones);

        inicializarVectorAplicaciones();

        DandoClickALosItems();
    }

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

    public long getApkSize(String packageName) {
        try {
            return new File(this.getPackageManager().getApplicationInfo(
                    packageName, 0).publicSourceDir).length();
        } catch (Exception e) {
            //Mensaje(e.toString());
        }
        return 0;
    }

    public void inicializarVectorAplicaciones() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);

        //String[] vect_final = new String[pkgAppsList.size()];

        aplicaciones = new String[pkgAppsList.size()];
        datos_aplicaciones = new String[pkgAppsList.size()];
        int i = 0;long t = 0;
        for (ResolveInfo r: pkgAppsList) {
            String[] partes = r.toString().split(" ");
            String[] partes2 = partes[1].split("/");
            String[] partes3 = partes2[0].split("[.]");
            /*
            // IMPRIMIR NOMBRE DE APLICACION / RUTA DE ACTIVITY
            for(int iaaa = 0; iaaa < partes.length; iaaa++) {
                Mensaje(partes[iaaa]);
            }
            */
            t = getApkSize(partes2[0]);
            //vect_final[i] = partes3[partes3.length-1] + "\n" + Double.toString(Math.floor(t/1024.0*1000.0)/1000.0) + " KB → " + Double.toString(Math.floor(t/1024.0/1024.0*1000)/1000) + " MB";
            aplicaciones[i] = partes3[partes3.length-1];
            datos_aplicaciones[i] = Double.toString(Math.floor(t/1024.0*1000.0)/1000.0) + " KB → " + Double.toString(Math.floor(t/1024.0/1024.0*1000)/1000) + " MB";
            i++;
        }

        Lista_RegistroDatos adaptador =new Lista_RegistroDatos(this,aplicaciones,datos_aplicaciones);
        ListView milistview = (ListView) findViewById(R.id.listViewAp);
        milistview.setAdapter(adaptador);
    }

    public void DandoClickALosItems() {
        ListView list = (ListView) findViewById(R.id.listViewAp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked,
                                    int position, long id) {
                //TextView textView = (TextView) viewClicked;
                //String s = textView.getText().toString().split("\n")[0];
                Intent intento = new Intent(getApplicationContext(), Act_aplicaciones_datos.class);
                //intento.putExtra("aplicación", textView.getText().toString().split("\n")[0]);
                //intento.putExtra("tamaño", Double.parseDouble(textView.getText().toString().split("\n")[1].split("KB")[0]));
                intento.putExtra("aplicación", aplicaciones[position]);
                intento.putExtra("tamaño", Double.parseDouble(datos_aplicaciones[position].split(" ")[0]));
                //Mensaje(datos_aplicaciones[position].split(" ")[0]);
                startActivity(intento);
            }
        });
    }

}

