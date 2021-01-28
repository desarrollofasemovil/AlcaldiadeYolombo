package com.tramites.alcaldiadeyolombo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {

    WebView webViewDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Barra de progreso de carga de la pagina
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Cargando...");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        //Variables que validan conexion
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Icono en el actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        webViewDB = (WebView)findViewById(R.id.webViewDB);

        String URL = "http://190.248.37.181:2500/AdminMunicipio/User/Login.aspx";

        webViewDB.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
        });

        //descarga localmente y abre en navegador
        webViewDB.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Descarga");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Archivo descargadado.", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();
            }
        });

        //Validamos conexion
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            Toast.makeText(this,
                    "Por favor ingresa tu usuario y contraseña para iniciar sesión", Toast.LENGTH_LONG).show();
            webViewDB.setInitialScale(1);
            webViewDB.setWebChromeClient(new WebChromeClient());
            webViewDB.getSettings().setAllowFileAccess(true);
            webViewDB.getSettings().setPluginState(WebSettings.PluginState.ON);
            webViewDB.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            webViewDB.getSettings().setBuiltInZoomControls(true);
            webViewDB.getSettings().setJavaScriptEnabled(true);
            webViewDB.getSettings().setLoadWithOverviewMode(true);
            webViewDB.getSettings().setUseWideViewPort(true);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            webViewDB.loadUrl(URL);

            //Invocamos el circulo de cargando
            webViewDB.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //elimina ProgressBar.
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this,
                    "No se detecta conexión a internet, revise su conexión e intente nuevamente", Toast.LENGTH_LONG).show();
            Intent i = new Intent(Dashboard.this, MainActivity.class);
            startActivity(i);
        }

        isStoragePermissionGranted();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public void Retornar(View view){
        Intent i = new Intent(Dashboard.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (webViewDB.canGoBack()) {
            webViewDB.goBack();
        } //else {
        //super.onBackPressed();
        //}
    }
}