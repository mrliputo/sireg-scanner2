package id.ac.unja.si.ktmscanner.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import id.ac.unja.si.ktmscanner.R;

public class Barcode extends AppCompatActivity {
    TextView welcomeText, instruction, stepText;
    Typeface helvetica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        welcomeText = findViewById(R.id.welcomeText);
        instruction = findViewById(R.id.instructionText);
        stepText = findViewById(R.id.stepText);
        helvetica = Typeface.createFromAsset(getAssets(), "fonts/helvetica.ttf");

        welcomeText.setTypeface(helvetica);
        instruction.setTypeface(helvetica);
        stepText.setTypeface(helvetica);
    }

    public void onScanButtonClicked(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan QR code yang ada pada website");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setCaptureActivity(Orientation.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    /**
     * Redirects to the validation activity. The key from this activity is also sent
     * to be validated.
     * @param key The string from qr code
     */
    private void sendKeyToBeValidated(String key) {
        Intent intent = new Intent(this, Validation.class);
        intent.putExtra("KEY",key);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                sendKeyToBeValidated(result.getContents());
                this.finish();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            moveTaskToBack(true);
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
            this.finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }

}
