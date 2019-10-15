package id.ac.unja.si.ktmscanner.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import id.ac.unja.si.ktmscanner.R;
import id.ac.unja.si.ktmscanner.http.SenderBarcode;
import id.ac.unja.si.ktmscanner.common.Url;

public class Validation extends AppCompatActivity {
    Handler handler;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        handler = new Handler();
        progressBar = findViewById(R.id.progressBar);

        // Checks the key from a file
        if (keyExists()) validateKey(getKey());
        else {
            // Check the key passed from barcode activity
            Intent intent = getIntent();

            if (intent.hasExtra("KEY")) {
                Bundle bundle = intent.getExtras();

                if (bundle != null) {
                    String key = bundle.getString("KEY");
                    validateKey(key);
                }

            } else goToBarcodeActivity();
        }

    }

//    private void keyNotExists() {
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Terjadi kesalahan");
//        alert.setMessage("Key tidak tersedia. Mohon isi form pendaftaran pada website terlebih" +
//                " dahulu");
//        alert.setCancelable(false);
//        alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface d, int i) {
//                goToBarcodeActivity();
//            }
//        });
//        alert.show();
//    }


    /**
     * Checks the validity of the key by sending it to the web server and checking whether it exists
     * or not in the database.
     *
     * @param key A string from the qr code that you wish to validate.
     * @return the boolean value. True if the key is valid.
     */
    private void validateKey(String key) {
        if(!key.equals("")) {
            String url = Url.getKeyVerification();
            SenderBarcode senderBarcode = new SenderBarcode(this, progressBar, url, key);
            senderBarcode.execute();
        }
    }

    // REDIRECT METHODS //
    private void goToBarcodeActivity() {
        Intent intent = new Intent(this, Barcode.class);
        startActivity(intent);
        this.finish();
    }

    // Checks key availability in internal storage
    private boolean keyExists() {
        File file = new File(getFilesDir() + "/key");
        return file.exists();
    }

    // Reads the key from a file in internal storage
    private String getKey() {
        String result = "";
        String value;

        try {
            FileInputStream fileInputStream = openFileInput("key");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuffer = new StringBuilder();

            try{
                while ((value = bufferedReader.readLine()) != null) stringBuffer.append(value);
                result = stringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

}
