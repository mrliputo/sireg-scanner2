package id.ac.unja.si.ktmscanner.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import id.ac.unja.si.ktmscanner.common.Url;

/**
 * Created by norman on 2/18/18.
 */

public class SenderStudent extends AsyncTask<Void,Void,String> {

    @SuppressLint("StaticFieldLeak")
    private Context c;
    private String urlAddress, nim, key;

    public SenderStudent(Context c, String urlAddress, String nim, String key) {
        this.c = c;
        this.urlAddress = urlAddress;
        this.nim = nim;
        this.key = key;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.send();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if(response == null) Toast.makeText(c,"Gagal mengirim data. Pastikan koneksi internet" +
                " Anda aktif" ,Toast.LENGTH_SHORT).show();
        else {
            String res = "";
            try {
                JSONObject jObj = new JSONObject(response);
                res = jObj.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            WebView webView = new  WebView(this.c);
            switch (res) {
                case "1":
                    webView.setVisibility(View.GONE);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(Url.getRealTime() + this.nim + "/" + this.key);
                    Toast.makeText(c,"Berhasil terdaftar." ,Toast.LENGTH_SHORT).show();
                    break;
                case "404":
                    Toast.makeText(c,"KTM tidak dikenali atau event tidak tersedia",
                            Toast.LENGTH_SHORT).show();
                    break;
                case "2":
                    webView.setVisibility(View.GONE);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(Url.getRealTime() + this.nim + "/" + this.key + "/1");
                    Toast.makeText(c,"Mahasiswa sudah pernah terdaftar", Toast.LENGTH_SHORT).show();
                    break;
                case "3":
                    Toast.makeText(c,"Jumlah pendaftar telah mencapai batas.",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(c,"Gagal mengirim data" ,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private String send() {

        // Connect
        HttpURLConnection con= HttpConnector.connect(urlAddress);
        if (con == null) return null;

        try {
            OutputStream os=con.getOutputStream();
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            bw.write(new PackagerStudent(nim, key).packData());
            bw.flush();
            bw.close();
            os.close();

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response=new StringBuilder();
                String line;

                while ((line=br.readLine()) != null) response.append(line);

                br.close();
                return response.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}


