package tw.org.iii.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager mgr;
    private String data;
    private TextView mmsg;
    private StringBuffer sb;
    private UIHandler handler;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new UIHandler();
        mmsg = (TextView)findViewById(R.id.mmsg);

        mgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()){
            try {
                Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
                while (ifs.hasMoreElements()){
                    NetworkInterface ip = ifs.nextElement();
                    Enumeration<InetAddress> ips = ip.getInetAddresses();
                    while (ips.hasMoreElements()){
                        InetAddress ia = ips.nextElement();
                        Log.d("DK", ia.getHostAddress());
                    }
                }


            } catch (SocketException e) {
                e.printStackTrace();
            }
        }else{
            Log.d("DK", "NOT Connect");
        }


    }

    public void test1(View v){
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://www.google.com");
//                    HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
//                    conn.connect();
//                    InputStream in = conn.getInputStream();
//                    int c; StringBuffer sb = new StringBuffer();
//                    while ( (c = in.read()) != -1){
//                        sb.append((char)c);
//                    }
//                    in.close();
//                    Log.d("DK", sb.toString());
//                }catch(Exception ee){
//                    Log.d("DK", ee.toString());
//                }
//            }
//        }.start();

        mmsg.setText("");

        MyTread mt1 = new MyTread();
        mt1.start();
    }

    private class MyTread extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL("http://data.coa.gov.tw/Service/OpenData/EzgoTravelFoodStay.aspx");
                HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
                conn.connect();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));
                data = reader.readLine();
                reader.close();
                parseJSON();
            }catch(Exception ee){
                Log.d("DK", ee.toString());
            }
        }
    }

    private void parseJSON(){
        sb = new StringBuffer();
        try {
            JSONArray root = new JSONArray(data);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                String name = row.getString("Name");
                String add = row.getString("Address");
                Log.d("DK", name + " -> " + add);
                sb.append(name + " -> " + add + "\n");
            }
            handler.sendEmptyMessage(0);
        }catch(Exception ee){
            Log.d("DK", ee.toString());
        }
    }

    private class UIHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mmsg.setText(sb);


        }

    }

}
