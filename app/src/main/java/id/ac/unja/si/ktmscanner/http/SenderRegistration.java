package id.ac.unja.si.ktmscanner.http;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import id.ac.unja.si.ktmscanner.activity.Barcode;

/**
 * Created by norman on 2/19/18.
 */

public class SenderRegistration extends AsyncTask<Void,Void,String> {
    @SuppressLint("StaticFieldLeak")
    private Context c;
    private String urlAddress, token;
    @SuppressLint("StaticFieldLeak")
    private View view;
    private ProgressDialog progressDialog;

    public SenderRegistration(Context c, String urlAddress, String token, View view) {
        this.c = c;
        this.urlAddress = urlAddress;
        this.token = token;
        this.view = view;
        progressDialog = new ProgressDialog(this.c);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Memvalidasi KTM. Harap tunggu...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.send();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        progressDialog.dismiss();
        Snackbar.make(view, this.urlAddress,
                Snackbar.LENGTH_LONG).show();

        if(response == null) Snackbar.make(view, "Gagal mengirim data. Pastikan koneksi internet" +
                " Anda aktif", Snackbar.LENGTH_LONG).show();
        else {

            String res = "";
            try {
                JSONObject jObj = new JSONObject(response);
                res = jObj.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            switch (res) {
                case "1":
                    Intent intent = new Intent(this.c, Barcode.class);
                    this.c.startActivity(intent);
                    break;
                case "404":
                    Snackbar.make(view, "Tidak dapat menemukan penganggung jawab organisasi.",
                            Snackbar.LENGTH_LONG).show();
                    break;
                default:
                    Snackbar.make(view, "Terjadi kesalahan. Coba beberapa saat lagi.",
                            Snackbar.LENGTH_LONG).show();
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
            bw.write(new PackagerRegistration(token).packData());
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
