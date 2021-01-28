package com.tramites.alcaldiadeyolombo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {

    InAppUpdateManager inAppUpdateManager;
    private ImageView ivPredial, ivIca, ivDeclaracionIca;
    String IDImpuesto;

    ImageView ivFacebook, ivInstagram, ivTwitter, ivPortal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //metodo actualizar app
        inAppUpdateManager = InAppUpdateManager.Builder(this, 101)
                .resumeUpdates(true)
                .mode(Constants.UpdateMode.IMMEDIATE)
                .snackBarAction("Actualización disponible.")
                .snackBarAction("RESTART")
                .handler(this);
        inAppUpdateManager.checkForAppUpdate();

        //Variables que validan conexion
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Icono en el actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        ivPredial = (ImageView) findViewById(R.id.ivPredial);
        ivIca = (ImageView) findViewById(R.id.ivIca);

        ivFacebook = (ImageView) findViewById(R.id.ivFacebook);
        ivTwitter = (ImageView) findViewById(R.id.ivTwitter);
        ivPortal = (ImageView) findViewById(R.id.ivPortal);

        //validamos conexion
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            //Toast.makeText(this,
            //        "Bienvenido a la alcaldia de Girardota", Toast.LENGTH_LONG).show();
            ivPredial.setEnabled(true);
            ivIca.setEnabled(true);
        } else {
            Toast.makeText(this,
                    "No se detecta conexión a internet, revise su conexión e ingrese nuevamente", Toast.LENGTH_LONG).show();
            ivPredial.setEnabled(false);
            ivIca.setEnabled(false);
        }
    }

    public void Predial(View view){
        IDImpuesto = "1";
        Intent i = new Intent(MainActivity.this, ConsultaImpuesto.class);
        i.putExtra("IDImpuesto", IDImpuesto);
        startActivity(i);
    }

    public void Ica(View view){
        IDImpuesto = "2";
        Intent i = new Intent(MainActivity.this, ConsultaImpuesto.class);
        i.putExtra("IDImpuesto", IDImpuesto);
        startActivity(i);
    }

    public void Portal(View view){

        String urlCanal = "http://www.yolombo-antioquia.gov.co/Paginas/default.aspx";

        Intent i = new Intent(MainActivity.this, Canales.class);
        i.putExtra("urlCanal", urlCanal);
        startActivity(i);
    }

    public void Facebook(View view){

        String urlCanal = "https://www.facebook.com/Alcaldiadeyol/";

        Intent i = new Intent(MainActivity.this, Canales.class);
        i.putExtra("urlCanal", urlCanal);
        startActivity(i);
    }

    public void Twitter(View view){

        String urlCanal = "https://twitter.com/alcaldiadeyol";

        Intent i = new Intent(MainActivity.this, Canales.class);
        i.putExtra("urlCanal", urlCanal);
        startActivity(i);
    }

    public void Dashboard(View view){
        Intent i = new Intent(MainActivity.this, Dashboard.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }

    @Override
    public void onInAppUpdateError(int code, Throwable error) {

    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {
        if(status.isDownloaded()) {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(view,
                    "Actualización exitosa.",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inAppUpdateManager.completeUpdate();
                }
            });

            snackbar.show();
        }
    }
}