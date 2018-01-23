package com.example.keffe_000.zasmarttools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class act_interna extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private File root;
    private File root2;
    private ArrayList<File> fileList = new ArrayList<File>();
    private long pesoIm=0;
    private long pesoMu=0;
    private long pesoVi=0;
    private long pesoDoc=0;
    private long pesoVrs=0;
    public static ProgressDialog progress= null;
    Hilo01 MiHilo01 = new Hilo01();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_interna);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setTitle("Za Smart Tools");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            root2=new File("storage/emulated/0"+"/Android");
            root=new File("storage/emulated/0");
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            root = new File("storage/sdcard0");
            root2=new File("storage/sdcard0/Android");
        }
        else {
            root = new File("storage/sdcard1");
            root2=new File("storage/sdcard1/Android");
        }

        TextView textView_T = (TextView) findViewById(R.id.txt_int_T);
        TextView txt_U = (TextView) findViewById(R.id.txt_int_U);
        TextView txt_L = (TextView) findViewById(R.id.txt_int_L);

        textView_T.setText("Total: " + String.valueOf(memoriaTotal() + "MB"));
        txt_U.setText("Utilizado: " + String.valueOf(memoriaTotal()-memoriaLibre()) + "MB");
        txt_L.setText("Libre: " + String.valueOf(memoriaLibre()) + "MB");

        pesoIm=0;
        pesoMu=0;
        pesoVi=0;
        pesoDoc=0;
        pesoVrs=0;

        calcularPeso(root);

        dibujarGraficoMemInt();
    }

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
        int id = item.getItemId();

        if (id == R.id.nav_int_ram) {
            Intent intento2 = new Intent(getApplicationContext(), act_ram.class);
            startActivity(intento2);
        }else  if (id == R.id.nav_int_ext) {
            if(memoriaDisponible()) {
                new Hilo01().execute();
            }else{
                TextView txt_total = (TextView) findViewById(R.id.txt_int_L);
                DialogoAlerta(txt_total, "Tarjeta SD no disponible");
            }
        }
        else
        if (id == R.id.nav_int_menu) {
            Intent intento3 = new Intent(getApplicationContext(), act_principal.class);
            startActivity(intento3);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static long memoriaLibre() {/*Retorna la memoria interna disponible en MB*/
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize/ 1048576L;
    }

    public static long memoriaTotal() {/*Retorna la memoria interna total del dispositivo en MB*/
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize/ 1048576L;
    }

    private void dibujarGraficoMemInt() {
        List<PieEntry> datos = new ArrayList();
        long memapp =   (memoriaTotal() - memoriaLibre())- ((pesoIm / 1048576L + pesoMu/ 1048576L + pesoDoc/ 1048576L + pesoVrs/ 1048576L));
        datos.add(new PieEntry(memapp, "Aplicaciones"));
        //Agrega variables al gráfico
        if(pesoIm/ 1048576L>5) {
            datos.add(new PieEntry(pesoIm / 1048576L, "Imágenes"));
        }
        if(pesoMu/ 1048576L>5) {
            datos.add(new PieEntry(pesoMu / 1048576L, "Música"));
        }
        if(pesoVi/ 1048576L>5) {
            datos.add(new PieEntry(pesoVi / 1048576L, "Videos"));
        }
        if(pesoDoc/ 1048576L>5) {
            datos.add(new PieEntry(pesoDoc, "Documentos"));
        }
        datos.add(new PieEntry(memoriaLibre(), "Libre"));
        if(pesoVrs/ 1048576L>5) {
            datos.add(new PieEntry(pesoVrs / 1048576L, "Varios"));
        }

        PieDataSet dataSet = new PieDataSet(datos, "Memoria Interna");

        int colores []= {Color.rgb(193, 37, 82), Color.rgb(255,102,0), Color.rgb(245,199,0), Color.rgb(106,150,31),
                Color.rgb(159,100,53), Color.rgb(192,255,140),Color.rgb(255,208,140),Color.rgb(140,234,255), Color.rgb(254,149,7)};

        dataSet.setColors(colores);
        dataSet.setValueFormatter(new Formato());
        PieData data = new PieData(dataSet);

        PieChart plot = (PieChart) findViewById(R.id.plot_int);
        plot.getLegend().setEnabled(false);
        plot.getDescription().setEnabled(false);
        plot.setData(data);

        plot.animateY(1000);
        plot.invalidate();
    }

    public void calcularPeso(File dir) {/*Carga el peso total de cada archivo en la memoria interna*/

        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory() && !dir.equals(root2)) {
                    fileList.add(listFile[i]);
                    calcularPeso(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".png")
                            || listFile[i].getName().endsWith(".jpg")
                            || listFile[i].getName().endsWith(".jpeg")
                            || listFile[i].getName().endsWith(".wmf")
                            || listFile[i].getName().endsWith(".gifv")
                            || listFile[i].getName().endsWith(".bmp")
                            || listFile[i].getName().endsWith(".odg")
                            || listFile[i].getName().endsWith(".gif"))

                    {
                        pesoIm += listFile[i].length();

                        fileList.add(listFile[i]);

                    } else if (listFile[i].getName().endsWith(".mp3")
                            ||
                            listFile[i].getName().endsWith(".wav")
                            || listFile[i].getName().endsWith(".aac")
                            || listFile[i].getName().endsWith(".wma")
                            || listFile[i].getName().endsWith(".ogg"))

                    {
                        pesoMu += listFile[i].length();

                        fileList.add(listFile[i]);

                    } else if (listFile[i].getName().endsWith(".mp4")
                            || listFile[i].getName().endsWith(".mpeg")
                            || listFile[i].getName().endsWith(".avi")
                            || listFile[i].getName().endsWith(".vcd")
                            || listFile[i].getName().endsWith(".vmm")
                            || listFile[i].getName().endsWith(".flv")
                            || listFile[i].getName().endsWith(".3gp"))

                    {
                        pesoVi += listFile[i].length();

                        fileList.add(listFile[i]);

                    } else if (listFile[i].getName().endsWith(".doc")
                            || listFile[i].getName().endsWith(".docx")
                            || listFile[i].getName().endsWith(".odt")
                            || listFile[i].getName().endsWith(".ppt")
                            || listFile[i].getName().endsWith(".pptx")
                            || listFile[i].getName().endsWith(".ods")
                            || listFile[i].getName().endsWith(".pdf")
                            || listFile[i].getName().endsWith(".odp")
                            || listFile[i].getName().endsWith(".xlsx")
                            || listFile[i].getName().endsWith(".xls"))

                    {
                        pesoDoc += listFile[i].length();

                        fileList.add(listFile[i]);

                    } else {
                        pesoVrs += listFile[i].length();

                    }
                }

            }
        }
    }

    public static boolean memoriaDisponible() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public void DialogoAlerta(View view, String mensaje){ //Diálogo

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
    };

    public class Formato implements IValueFormatter {

        private DecimalFormat mFormat;

        public Formato() {
            mFormat = new DecimalFormat("###,###");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            return mFormat.format(value) + " MB";
        }
    }

    private class Hilo01 extends AsyncTask<Integer,Integer,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress= new ProgressDialog(act_interna.this);
            progress.setTitle("Cargando");
            progress.setMessage("Por favor espere");
            progress.setCancelable(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            Intent intento = new Intent(getApplicationContext(), act_externa.class);
            startActivity(intento);

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            //super.onPostExecute(aVoid);
            if(act_interna.progress!=null){
                progress.dismiss();

            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();


        }
    }
}
