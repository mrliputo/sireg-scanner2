package id.ac.unja.si.ktmscanner.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import id.ac.unja.si.ktmscanner.common.Nfc;
import id.ac.unja.si.ktmscanner.R;

public class Reader extends AppCompatActivity {
    Nfc nfc = new Nfc();
    TextView readerMsg;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        readerMsg = findViewById(R.id.readerMsg);
        nfc.getAdapter(this);
        if (nfc.notAvailable()) showNoNFCAlert();

        Intent intent = getIntent();
        if (intent.hasExtra("EVENT_TITLE")) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                String eventTitle = bundle.getString("EVENT_TITLE");
                assert eventTitle != null;
                readerMsg.setText("TAP KTM DI BELAKANG PERANGKAT UNTUK MENDAFTAR " + eventTitle.toUpperCase());
            }

        }
    }

    private void showNoNFCAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Terjadi kesalahan");
        alert.setMessage("Jika perangkat Anda memiliki fitur Nfc, mohon aktifkan" +
                " di Connections -> Nfc and payment");
        alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int i) {
                finishAffinity();
            }
        });
        alert.show();
    }


    @Override
    /*
      This method is executed every time a new card is detected.
      All the data inside the card is stored in parcelable class.
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(parcelables != null && parcelables.length > 0) {
            nfc.readTextFromMessage((NdefMessage)parcelables[0], getKey(), findViewById(R.id.reader_layout));
        }else{
            Toast.makeText(this, "Data tidak tersedia", Toast.LENGTH_LONG).show();
        }
    }


    // Get the key from a file in internal storage
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

    // Delete the key
    private void deleteKey() {
        File file = new File(getFilesDir() + "/key");
        if (file.exists()) file.delete();
    }

    private void goToBarcodeActivity() {
        Intent intent = new Intent(this, Barcode.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onResume() {
        nfc.enableFDS();
        super.onResume();
    }

    @Override
    protected void onPause() {
        nfc.disableFDS();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.actionbutton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.remove) {
            deleteKey();
            goToBarcodeActivity();
        }else if(id == R.id.exit) {
//            moveTaskToBack(true);
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
            this.finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
