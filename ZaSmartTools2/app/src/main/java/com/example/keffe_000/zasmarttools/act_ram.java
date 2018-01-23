package com.example.keffe_000.zasmarttools;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class act_ram extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ProgressDialog progress = null;
    Hilo01 MiHilo01 = new Hilo01();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_ram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Za Smart Tools");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab.hide();
        TextView txt_total = (TextView) findViewById(R.id.txt_ram_T);
        TextView txt_usado = (TextView) findViewById(R.id.txt_ram_U);
        TextView txt_libre = (TextView) findViewById(R.id.txt_ram_L);
        txt_total.setText("Total: " + String.valueOf(totalRam()) + "MB");
        txt_libre.setText("Libre: " + String.valueOf(RamLibre()) + "MB");
        txt_usado.setText("Utilizada: " + String.valueOf(totalRam() - RamLibre()) + "MB");
        dibujarGraficoRAM();
    }

    ////////////////////////////////////////////////////////////
    //Código generado
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ram_int) {
            new Hilo01().execute(0);
        } else if (id == R.id.nav_ram_ext) {
            if (memoriaDisponible()) {
                new Hilo01().execute(1);
            } else {
                TextView txt_total = (TextView) findViewById(R.id.txt_ram_T);
                DialogoAlerta(txt_total, "Tarjeta SD no disponible");
            }
        } else if (id == R.id.nav_ram_menu) {
            Intent intento3 = new Intent(getApplicationContext(), act_principal.class);
            startActivity(intento3);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dibujarGraficoRAM() {/*Dibuja el gráfico*/
        List<PieEntry> datos = new ArrayList();
        datos.add(new PieEntry(totalRam() - RamLibre(), "RAM Utilizada"));
        datos.add(new PieEntry(RamLibre(), "RAM Libre"));

        PieDataSet dataSet = new PieDataSet(datos, "Memoria RAM");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueFormatter(new Formato());
        PieData data = new PieData(dataSet);

        PieChart plot = (PieChart) findViewById(R.id.plot_ram);
        plot.getLegend().setEnabled(false);
        plot.getDescription().setEnabled(false);
        plot.setData(data);

        plot.animateY(1000);
        plot.invalidate();
    }

    private long totalRam() { /*Calcula el total de RAM del dispositivo*/
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.totalMem / 1048576L;
        return availableMegs;
    }

    private long RamLibre() {/*Calcula la memoria RAM disponible*/
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;

        return availableMegs;
    }

    public class Formato implements IValueFormatter {/*Clase para darle formato a los valores "x" del gráfico*/

        private DecimalFormat mFormat;

        public Formato() {
            mFormat = new DecimalFormat("###,###");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            return mFormat.format(value) + " MB";
        }
    }

    public static boolean memoriaDisponible() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public void DialogoAlerta(View view, String mensaje) { //Diálogo

        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setMessage(mensaje);
        builder1.setCancelable(true);
        builder1.setNeutralButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    ;

    private class Hilo01 extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(act_ram.this);
            progress.setTitle("Cargando");
            progress.setMessage("Por favor espere");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            if (params[0] == 1) {
                Intent intento = new Intent(getApplicationContext(), act_externa.class);
                startActivity(intento);
            } else {
                Intent intento = new Intent(getApplicationContext(), act_interna.class);
                startActivity(intento);
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            //super.onPostExecute(aVoid);
            if (act_ram.progress != null) {
                progress.dismiss();

            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();


        }
    }

}
