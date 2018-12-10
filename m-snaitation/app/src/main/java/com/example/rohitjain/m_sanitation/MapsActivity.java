package com.example.rohitjain.m_sanitation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


class LatitudeNLongitude
{
public String latitude="",longitude="";
public LatitudeNLongitude(String lat,String longi)
{
    this.latitude=lat;
    this.longitude=longi;
}

}

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback, android.location.LocationListener {

    private GoogleMap mMap;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton fabAddLav, fabTrack;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
   // LocationManager mLocationManager;
    Location currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    String locationText;
    JSONArray jsonArray;
    ArrayList<LatitudeNLongitude> latLongValues;

    public boolean onMarkerClick(final Marker marker) {


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        latLongValues=new ArrayList<>();


     //   System.out.println(mLocationManager);
        //checkLocation();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        fabAddLav = (FloatingActionButton) findViewById(R.id.material_design_floating_action_addlav);
        fabTrack = (FloatingActionButton) findViewById(R.id.material_design_floating_action_track);
        //floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);

        fabAddLav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                Intent myIntent = new Intent(MapsActivity.this, RequestLav.class);
                startActivity(myIntent);

            }
        });
        fabTrack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Intent myIntent = new Intent(MapsActivity.this, RequestTrack.class);
                startActivity(myIntent);


            }
        });





    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
//        System.out.println(  "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println( "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(500);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }
                });
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        // mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        //mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private void showCustomAlert(String title, String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title)
                .setMessage(msg);

        dialog.show();
    }

    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MapsActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

       // LatLng sydney = new LatLng(-34, 151);
     //   mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
          //      sydney, 7));
//        mMap.addCircle(new CircleOptions()
//                .center(sydney)
//                .radius(5000)
//                .strokeColor(Color.RED)
//                .fillColor(0x220000FF).strokeWidth(3));

        Geocoder geocoder;
        List<Address> addresses;
       if( isLocationEnabled()) {
           Criteria criteria = new Criteria();

           Location location = getLastKnownLocation();
           LocationManager locationManager = mLocationManager;
           double latitude = location.getLatitude();
           double longitude = location.getLongitude();
           final LatLng MYLOCATION=new LatLng(latitude,longitude);//mMa.get;
          Marker marker;
           marker = mMap.addMarker(new MarkerOptions()
                           .position(MYLOCATION).title("Your Location"));
           mMap.moveCamera(CameraUpdateFactory.newLatLng(MYLOCATION));
           mMap.setMyLocationEnabled(true);
           mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
           // Criteria criteria = new Criteria();
           try {

//               Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
//
//
//               double latitude = location.getLatitude();
//               double longitude = location.getLongitude();
//               final LatLng MYLOCATION = new LatLng(latitude, longitude);//mMa.get;


               geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());


               addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


               String city = addresses.get(0).getLocality();

               String state = addresses.get(0).getAdminArea();

               String country = addresses.get(0).getCountryName();

               Map<String, String> nameValuePair = new HashMap<>();


               nameValuePair.put("city", city.toUpperCase());

               nameValuePair.put("state", state.toUpperCase());

               nameValuePair.put("country", country.toUpperCase());


               // nameValuePair.put("knownname",knownName.toUpperCase());


               DBConnect dc = new DBConnect("http://bhartiyamattress.com/fetchLavatoryData.php?city=" + nameValuePair.get("city") + "&state=" + nameValuePair.get("state") + "&country=" + nameValuePair.get("country"), "GET", nameValuePair);

               // DBConnect.setDataObject("http://bhartiyamattress.com/fetchLavData.php", "POST", parameter);

               dc.execute();


               System.out.println("-------Starting to print Data--------");

//        marker = mMap.addMarker(new MarkerOptions()
//                .position(MYLOCATION).title("PERTH")
//                .draggable(true));
               float zoomLevel = 13.0f;
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MYLOCATION, zoomLevel));
               mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
               mMap.setOnMyLocationClickListener(onMyLocationClickListener);
               enableMyLocationIfPermitted();

               mMap.getUiSettings().setZoomControlsEnabled(true);


               //  mMap.setMinZoomPreference(11);
           } catch (SecurityException se) {
               showCustomAlert("Permission Needed", "Location permission not granted");
           } catch (Exception ex) {
               System.out.println(ex.getMessage() + "\n" + ex.getStackTrace());
               showCustomAlert("Connecticity Error", "Please make sure if the Internet connectivity is available");
           }
       }
    }
    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
      //  LatLng redmond = new LatLng(47.6739881, -122.121512);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                //return;
            }

        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                  //  mMap.setMinZoomPreference(15);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(200);
                    circleOptions.fillColor(0x220000FF);

                    circleOptions.strokeWidth(3);


                    mMap.addCircle(circleOptions);
                }
            };

    class DBConnect extends AsyncTask<Void, Void, Void> {

        String urlLink = "";
        //  private ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
        InputStream inputStream = null;
        String method="";
        Map<String,String> urlData;
        String result = "";

        public DBConnect(String url, String method, Map<String,String> urlData)
        {

            this.urlLink=url;
            this.method=method;
            this.urlData=urlData;
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
//                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(urlData);
//                writer.flush();
//                writer.close();
//                out.close();

                urlConnection.setDoInput(true);

                // urlConnection.setDoOutput(true);

                //  urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestMethod(this.method);
                System.out.print(urlConnection.getRequestMethod() + urlConnection.getURL());
                System.out.println(urlConnection.getURL());
                // OPTIONAL - Sets an authorization header
                //  urlConnection.setRequestProperty("Authorization", "someAuthString");



                if(method.equals("POST"))
                {
                    if (this.urlData != null) {

                        System.out.println("Success 1");
                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                        System.out.println("Success 2");
                        writer.write(getPostDataString(new JSONObject(urlData)));
                        System.out.println("Success 3");
                        writer.flush();
                        System.out.println("Success 4");
                        writer.close();
                        System.out.println("Success 5");
                        urlConnection.connect();
                        System.out.println("Success 6");


                    }
                }
                else
                {

                }
                // Send the post body

                int statusCode = urlConnection.getResponseCode();

                System.out.println("Success 7");
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
                    jsonArray = new JSONArray(result);

                    //System.out.println(jArray);
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
//System.out.println(jArray);

            try {
                System.out.println(jsonArray);
             for (int i = 0; i < jsonArray.length(); i++) {
//
                // System.out.println(jsonObject);
                // JSONObject jObject = jArray.getJSONObject(i);

                //    json_data = jArray.getJSONObject(i);
                // JSONObject jsonObject= json_Array.getJSONObject(i);
                 //System.out.println(jsonObject);
                 System.out.println(jsonArray.get(i));
                 JSONObject jsonObject=jsonArray.getJSONObject(i);
               //  System.out.println(jsonObject);
                 System.out.println(jsonObject.getString("Latitude")+" "+jsonObject.getString("Longitude"));
                 LatitudeNLongitude latLong=new LatitudeNLongitude(jsonObject.getString("Latitude"),jsonObject.getString("Longitude"));

                latLongValues.add(latLong);
                 mMap.addMarker(new MarkerOptions()
                         .position(new LatLng(Double.parseDouble(latLong.latitude), Double.parseDouble(latLong.longitude)))
                         .draggable(true));
                  }
            }
            catch(Exception ex)
            {
                System.out.println("Exception:"+ex.getMessage());
                System.out.println(ex.getStackTrace());
            }

            //parse JSON data
            // catch (JSOsNException e)
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

}
