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
import android.widget.Button;
import android.widget.Toast;

public class Canales extends AppCompatActivity {

    WebView webViewCA;
    Button btnVolverCA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canales);

        //Variables que validan conexion
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Icono en el actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        btnVolverCA = (Button)findViewById(R.id.btnVolverCA);
        webViewCA = (WebView)findViewById(R.id.webViewCA);

        String URL = getIntent().getStringExtra("urlCanal");

        webViewCA.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
        });

        //descarga localmente y abre en navegador
        webViewCA.setDownloadListener(new DownloadListener() {

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
                Toast.makeText(getApplicationContext(), "Descargando Archivo", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();

            }
        });

        //Barra de progreso de carga de la pagina
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Cargando...");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        //Validamos conexion
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){

            webViewCA.setInitialScale(1);
            webViewCA.setWebChromeClient(new WebChromeClient());
            webViewCA.getSettings().setAllowFileAccess(true);
            webViewCA.getSettings().setPluginState(WebSettings.PluginState.ON);
            webViewCA.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            //webViewCA.getSettings().setBuiltInZoomControls(true);
            webViewCA.getSettings().setJavaScriptEnabled(true);
            webViewCA.getSettings().setLoadWithOverviewMode(true);
            webViewCA.getSettings().setUseWideViewPort(true);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            webViewCA.loadUrl(URL);

            //Invocamos el circulo de cargando
            webViewCA.setWebViewClient(new WebViewClient(){
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
            Intent i = new Intent(Canales.this, MainActivity.class);
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

    public void VolverCA(View view){
        Intent i = new Intent(Canales.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (webViewCA.canGoBack()) {
            webViewCA.goBack();
        } else {
            super.onBackPressed();
        }
    }
}