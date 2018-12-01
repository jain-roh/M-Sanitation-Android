package com.example.rohitjain.m_sanitation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.rohitjain.m_sanitation.MapsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DBConnect extends AsyncTask<Void, Void, Void> {

     String urlLink = "";
    //  private ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
    InputStream inputStream = null;
     String method="";
     Map<String,String> urlData;
    String result = "";
     public static JSONArray jArray;
    public DBConnect(String url, String method, Map<String,String> urlData)
    {

        this.urlLink=url;
        this.method=method;
        this.urlData=urlData;
        this.jArray=null;
    }

//    public static void setDataObject(String url, String method, Map<String,String> urlData)
//    {
//        jObject=null;
//        urlLink=null;
//        urlData=null;
//        DBConnect.urlLink=url;
//        DBConnect.method=method;
//        DBConnect.urlData=urlData;
////        new DBConnect(url,method,urlData).execute();
//  //      return  jObject;
//
//    }
public JSONArray fetchData()
    {
        return jArray;
    }
    protected void onPreExecute() {
//        progressDialog.setMessage("Downloading your data...");
//        progressDialog.show();
//       // progressDialog.setOnCancelListener(new OnCancelListener() {
//            public void onCancel(DialogInterface arg0) {
//                DBConnect.this.cancel(true);
//            }
//        });


    }
    protected Void doInBackground(Void... params) {



        try {
            // Set up HTTP post

            // HttpClient is more then less deprecated. Need to change to URLConnection

            URL url = new URL(this.urlLink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);

            urlConnection.setDoOutput(true);

          //  urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.setRequestMethod(this.method);
            System.out.print(urlConnection.getRequestMethod() + urlConnection.getURL());
            // OPTIONAL - Sets an authorization header
            //  urlConnection.setRequestProperty("Authorization", "someAuthString");



            if(method.equals("POST"))
            {
                if (this.urlData != null) {


                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(getPostDataString(new JSONObject(urlData)));
                    writer.flush();
                }
            }
            else
            {

            }
            // Send the post body

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {
                System.out.print("Data fetching");
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                System.out.println("Value to check:"+ total.toString());
                // JSONObject jsonObj = new JSONObject(total.toString());
                //JSONArray  contacts = new JSONArray (total.toString());
                result= total.toString();
                jArray = new JSONArray(result);
                System.out.println(jArray);
                //    String response = convertInputStreamToString(inputStream);

                // From here you can convert the string to JSON with whatever JSON parser you like to use
                // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
            } else {
                // Status code is not 200
                // Do something to handle the error

                throw new Exception("HTTP Connection error");
            }


            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Log.e("StringBuilding", "Error converting result " + e.toString());
            return null;
        }
    }
    protected void onPostExecute(Void v) {
        //parse JSON data
 // catch (JSONException e)
    } // protected void onPostExecute(Void v)
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}