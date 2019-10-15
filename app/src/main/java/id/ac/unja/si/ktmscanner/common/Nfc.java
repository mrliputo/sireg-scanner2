package id.ac.unja.si.ktmscanner.common;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import id.ac.unja.si.ktmscanner.http.SenderRegistration;
import id.ac.unja.si.ktmscanner.http.SenderStudent;

/**
 * Created by norman on 2/20/18.
 */

public class Nfc {
    private NfcAdapter nfcAdapter;
    private Context context;

    public void getAdapter(Context context) {
        this.context = context;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this.context);
    }

    public boolean notAvailable() {
        return (this.nfcAdapter == null || !this.nfcAdapter.isEnabled());
    }


    // Listener
    public void enableFDS() {
        Intent intent = new Intent(this.context, this.context.getClass()).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        this.nfcAdapter.enableForegroundDispatch((Activity) this.context, pendingIntent, intentFilters,null);
    }

    public void disableFDS() {
        this.nfcAdapter.disableForegroundDispatch((Activity) this.context);
    }


    // Read content from the card
    public void readTextFromMessage(NdefMessage ndefMessage, View view) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            sendToken(tagContent, view);
        }else{
            Toast.makeText(this.context, "No Ndef records found", Toast.LENGTH_LONG).show();
        }
    }

    public void readTextFromMessage(NdefMessage ndefMessage, String key, View view) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            sendToken(tagContent, key, view);
        }else{
            Toast.makeText(this.context, "No Ndef records found", Toast.LENGTH_LONG).show();
        }
    }

    private String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 51;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        }catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }

        return tagContent;
    }


    // Send the token (and the key) to the server
    private void sendToken(String token, View view) {
        if (!token.equals("")) {
            String url = Url.getRegistrationVerification();
            SenderRegistration senderRegistration = new SenderRegistration(this.context, url, token, view);
            senderRegistration.execute();
        }else{
            Snackbar.make(view, "Data tidak valid. Pastikan KTM yang Anda gunakan" +
                    " adalah KTM Universitas X", Snackbar.LENGTH_LONG).show();
        }
    }

    private void sendToken(String nim, String key, View view) {
        if (!nim.equals("") && !key.equals("")) {
            String url = Url.getNewMember();
            SenderStudent senderStudent = new SenderStudent(this.context, url, nim, key);
            senderStudent.execute();
        } else {
            Snackbar.make(view, "Data tidak valid. Pastikan KTM yang Anda gunakan" +
                    " adalah KTM Universitas X", Snackbar.LENGTH_LONG).show();
            Toast.makeText(this.context, "Data KTM tidak lengkap", Toast.LENGTH_SHORT).show();
        }
    }

}
