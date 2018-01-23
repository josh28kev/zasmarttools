package com.example.keffe_000.zasmarttools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimationDrawable;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Act_trafico_datos extends AppCompatActivity{
    //Vectores
    String[] aplicaciones;
    String[] envio;
    String[] recepcion;
    int[] vector_uid;
    boolean[] vector_cambios;
    //Hilo
    Thread tr;
    boolean vidaThread = true;
    boolean controladorThread = false;
    boolean pauseFlag = false;
    //Lista_TraficoDatos
    Lista_TraficoDatos l_tr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_trafico_datos);

        inicializarVectorAplicaciones();

        tr = new Thread(new Runnable() {
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    actualizarVectorAplicaciones();
                }
            };

            @Override
            public void run() {
                while(vidaThread) {
                    CincoSegundos();
                    if(controladorThread) {
                        Message message = mHandler.obtainMessage();
                        message.sendToTarget();
                    }
                }
            }
        });
        tr.start();

        // alambramos el ImageButton
        final ImageButton MiImageButton = (ImageButton) findViewById(R.id.button_actualizar);
        //Programamos el evento onclick
        MiImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if(controladorThread) {
                    controladorThread = false;
                    MiImageButton.setImageResource(R.drawable.deteccion_inactiva);
                }
                else {
                    controladorThread = true;
                    MiImageButton.setImageResource(R.drawable.deteccion_activa);
                }
            }
        });

        DandoClickALosItems();
    }

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

    public long getUID(String packageName) {
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        //loop through the list of installed packages and see if the selected
        //app is in the list
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(packageName)){
                //get the UID for the selected app
                return packageInfo.uid;
            }
        }
        return 0;
    }

    public void inicializarVectorAplicaciones() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
        //String[] vect_final = new String[pkgAppsList.size()];
        aplicaciones = new String[pkgAppsList.size()];
        envio = new String[pkgAppsList.size()];
        recepcion = new String[pkgAppsList.size()];
        vector_uid = new int[pkgAppsList.size()];
        vector_cambios = new boolean[pkgAppsList.size()];
        int i = 0;long t = 0;
        for (ResolveInfo r: pkgAppsList) {
            String[] partes = r.toString().split(" ");
            String[] partes2 = partes[1].split("/");
            String[] partes3 = partes2[0].split("[.]");
            t = getUID(partes2[0]);
            //vect_final[i] = partes3[partes3.length-1] + "\n" + "UID -> " + Long.toString(t);
            /*aplicacion.add(partes3[partes3.length-1]);
            vector_uid.add(Long.toString(t));
            /*vect_final[i] = partes3[partes3.length-1] + "\n"
                    + "Kilobytes enviados  → " + Double.toString(Math.floor(TrafficStats.getUidTxBytes((int)t)/1024.0*1000)/1000) + "KB\n"
                    + "Kilobytes recibidos → " + Double.toString(Math.floor(TrafficStats.getUidRxBytes((int)t)/1024.0*1000)/1000) + "KB";
                    */

            aplicaciones[i] = partes3[partes3.length-1];
            envio[i] =  "Kilobytes enviados  → " + Double.toString(Math.floor(TrafficStats.getUidTxBytes((int)t)/1024.0*1000)/1000) + " KB";
            recepcion[i] = "Kilobytes recibidos → " + Double.toString(Math.floor(TrafficStats.getUidRxBytes((int)t)/1024.0*1000)/1000) + " KB";
            vector_uid[i] = (int)t;
            vector_cambios[i] = false;

            i++;
        }
        l_tr = new Lista_TraficoDatos(this,aplicaciones,envio,recepcion,vector_cambios);
        ListView milistview = (ListView) findViewById(R.id.listViewTrafico);
        milistview.setAdapter(l_tr);
    }

    private void CincoSegundos(){
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e){}
    }

    public void actualizarVectorAplicaciones() {
        //Mensaje("Hola");
        String tE = ""; String tR = "";
        for(int i = 0; i < vector_uid.length; i++) {
            tE = "Kilobytes enviados  → " + Double.toString(Math.floor(TrafficStats.getUidTxBytes(vector_uid[i])/1024.0*1000)/1000) + " KB";
            tR = "Kilobytes recibidos → " + Double.toString(Math.floor(TrafficStats.getUidRxBytes(vector_uid[i])/1024.0*1000)/1000) + " KB";
            if(envio[i].compareTo(tE) != 0) vector_cambios[i] = true;
            if(recepcion[i].compareTo(tR) != 0) vector_cambios[i] = true;
            //Mensaje(Integer.toString(envio[i].compareTo(tE)));
            envio[i] = tE;
            recepcion[i] = tR;
            i++;
        }
        l_tr = new Lista_TraficoDatos(this,aplicaciones,envio,recepcion,vector_cambios);
        ListView milistview = (ListView) findViewById(R.id.listViewTrafico);

        /*
        // DETECCION DE POSICIÓN DE Y DEL SCROLL
        View c = milistview.getChildAt(0);
        int scrolly = -c.getTop() + milistview.getFirstVisiblePosition() * c.getHeight();
        */

        milistview.setAdapter(l_tr);
        //milistview.scrollTo(0,scrolly);
        Mensaje("Datos actualizados");
    }

    public void DandoClickALosItems() {
        ListView list = (ListView) findViewById(R.id.listViewTrafico);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String temporal = "";
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked,
                                    int position, long id) {

                Long tiempo = android.os.SystemClock.elapsedRealtime();
                String s = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(tiempo),
                        TimeUnit.MILLISECONDS.toMinutes(tiempo) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(tiempo)),
                        TimeUnit.MILLISECONDS.toSeconds(tiempo) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tiempo))
                );
                String message = "Tiempo transcurrido desde el inicio del dispositivo: " + s + "\n\n";

                temporal = envio[position].split("→ ")[1];
                message += "Envío de datos desde el inicio del dispositivo:\n" +
                        temporal + " → " +
                        Double.toString(Math.floor(Double.parseDouble(temporal.substring(0,temporal.length()-3))/1024.0*1000)/1000)
                        + " MB\n\n";

                temporal = recepcion[position].split("→ ")[1];
                message += "Recepción de datos desde el inicio del dispositivo:\n" +
                        temporal + " → " +
                        Double.toString(Math.floor(Double.parseDouble(temporal.substring(0,temporal.length()-3))/1024.0*1000)/1000)
                        + " MB";

                MensajeOK(message);
            }
        });
    }

    public void MensajeOK(String msg){
        View v1 = getWindow().getDecorView().getRootView();
        AlertDialog.Builder builder1 = new AlertDialog.Builder( v1.getContext());
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {} });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        ;};


    protected void onPause() {
        super.onPause();
        pauseFlag = controladorThread;
        controladorThread = false;
    }

    protected void onResume() {
        super.onResume();
        controladorThread = pauseFlag;
    }
}
