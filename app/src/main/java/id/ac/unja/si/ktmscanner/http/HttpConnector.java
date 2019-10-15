package id.ac.unja.si.ktmscanner.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by norman on 2/18/18.
 */

class HttpConnector {

    static HttpURLConnection connect(String urlAddress) {

        try
        {
            URL url=new URL(urlAddress);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoInput(true);
            con.setDoOutput(true);

            return con;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


}
