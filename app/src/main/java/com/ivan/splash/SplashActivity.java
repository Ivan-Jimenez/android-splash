package com.ivan.splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends Activity {
    String getURL = "http://www.your-server.com/english-proper-names.txt";
    String ip204 =  "http://www.your-server.com/return204.php";

    public static int ConnectTimeout = 10000;
    public static int ReadTimeout    = 10000;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                              WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.activity_splash);

        if (haveNetworkConnection()) new HttpDownload().execute();
        else noConnection();
    }

    private boolean haveNetworkConnection () {
        boolean haveConnectedWifi   = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : networkInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"));
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedMobile || haveConnectedWifi;
    }

    public class HttpDownload extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground (Void... unsused) {
            publishProgress("Conectando", "0");
            String fromServer = "";
            int BUFFER_SIZE = 2000;
            float fsize = 890000;
            InputStream inputStream;

            try {
                // Check for reachability
                URL url = new URL(ip204);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(ConnectTimeout);
                conn.setReadTimeout(ReadTimeout);
                conn.setRequestMethod("HEAD");
                inputStream = conn.getInputStream();
                int status = conn.getResponseCode();
                inputStream.close();
                conn.disconnect();
                if (status == HttpURLConnection.HTTP_NO_CONTENT) {
                    // Server is reachable, so initiate the download
                    publishProgress("Conectando:", "0");
                    inputStream = OpenHttpConnection(getURL);
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    int charRead;
                    char[] inputBuffer = new char[BUFFER_SIZE];
                    while ((charRead = inputStreamReader.read(inputBuffer)) > 0) {
                        // -- Convert chars to string ---
                        String readString = String.copyValueOf(inputBuffer, 0, charRead);
                        fromServer += readString;
                        inputBuffer = new char[BUFFER_SIZE];
                        // --- update the progress
                        float ratio = (fromServer.length() / fsize) * 100;
                        int num = (int) ratio;
                        publishProgress("Conectando: " + String.valueOf(num) + "%", String.valueOf(num));
                    }
                    inputStream.close();
                } else {
                    publishProgress("No disponible", "0");
                    failedReach();
                }
            } catch (IOException e) {
                failedDownload();
            }
            publishProgress("Completado", "100");
            return null;
        }

        @Override
        protected void onPostExecute (Void unused) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }

        @Override
        protected void onProgressUpdate (String... item) {
            TextView txt = findViewById(R.id.text);
            txt.setText(item[0]);
            ProgressBar progressBar = findViewById(R.id.progBar);
            int num = Integer.parseInt(item[1]);
            progressBar.setProgress(num);
        }

        @Override
        protected void onPreExecute () {

        }
    }

    public void failedDownload () {
        AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
        alertDialog.setTitle("Conexión");
        alertDialog.setIcon(android.R.drawable.stat_sys_warning);
        alertDialog.setMessage("La conexión esta disponible, pero la descarga ha fallado. Reinicie");
        alertDialog.setButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.show();
    }

    public void failedReach () {
        Looper.prepare();
        AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
        alertDialog.setTitle("Conexión");
        alertDialog.setIcon(android.R.drawable.stat_sys_warning);
        alertDialog.setMessage("La conexión esta disponible, pero no se puede acceder al servidor. Reinicie.");
        alertDialog.setCancelable(false);
        alertDialog.setButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.show();
    }

    public void noConnection () {
        AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
        alertDialog.setTitle("Conexión");
        alertDialog.setIcon(android.R.drawable.stat_sys_warning);
        alertDialog.setMessage("Conexión de datos no disponible. Reinicie");
        alertDialog.setCancelable(false);
        alertDialog.setButton("salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.show();
    }

    public static InputStream OpenHttpConnection (String urlString) throws IOException {
        InputStream inputStream = null;
        int response;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection)) throw new IOException("No an HTTP Connection");

        try {
            HttpURLConnection httpURLConn = (HttpURLConnection) conn;
            httpURLConn.setAllowUserInteraction(false);
            httpURLConn.setInstanceFollowRedirects(true);
            httpURLConn.setRequestMethod("GET");
            httpURLConn.connect();

            response = httpURLConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) inputStream = httpURLConn.getInputStream();
        } catch (Exception ex) { throw new IOException("Error connecting"); }
        return inputStream;
    }
}
