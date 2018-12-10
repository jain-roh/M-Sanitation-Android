package com.example.rohitjain.m_sanitation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLav extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    Button requestLav;
    EditText name,mobile,emailId;
    TextView txtName,txtMobile,txtEmail;
    JSONObject jsonObject;
    JSONArray jArray;
    Marker marker;
    JSONObject json_data;
    int res=-1;
    String reqNo;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
    public boolean onMarkerClick(final Marker marker) {

        if (marker.equals(mMap)) {
            // handle click here
            // map.getMyLocation();
//            System.out.println("Clicked");
//            double lat = mMap.getMyLocation().getLatitude();
//            System.out.println("Lat" + lat);
//            //Toast.makeText(RequestLav.this,
//                    "Current location " + mMap.getMyLocation().getLatitude(),
//                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }



    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_lav);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



// Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
//
//        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
//
//        // Construct a FusedLocationProviderClient.
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        requestLav=(Button)findViewById(R.id.requestLav);
        name=(EditText)findViewById(R.id.nameUser);
        mobile=(EditText)findViewById(R.id.mobile);
        emailId=(EditText)findViewById(R.id.emailId);
        JSONArray jArray;



        requestLav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean error=false;
                String nameUser=name.getText().toString();
                String mobileNo=mobile.getText().toString();
                String email=emailId.getText().toString();
                String lat="";
                String lon="";
                if(nameUser.equals(""))
                {
                    error=true;

                }
                if(mobileNo.equals(""))
                {
                    error=true;

                }
                if(email.equals(""))
                {
                    error=true;

                }
                if(!validateEmail(email))
                {
                    Toast.makeText(getApplicationContext(),"Email Address not in correct form",Toast.LENGTH_LONG).show();
                    return;
                }
                if(mMap!=null && marker!=null)
                {
                    LatLng position = marker.getPosition(); //
                    lat=position.latitude+"";
                    lon=position.longitude+"";

                }
                if(!error)
                {

                    Geocoder geocoder;
                    List<Address> addresses;
                    try {
                        geocoder = new Geocoder(RequestLav.this, Locale.getDefault());

                        addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        Map<String,String> nameValuePair=new HashMap<>();
                        nameValuePair.put("name",nameUser);
                        nameValuePair.put("mobile",mobileNo);
                        nameValuePair.put("email",email);
                        nameValuePair.put("latitude",lat);
                        nameValuePair.put("longitude",lon);
                        nameValuePair.put("address",address.toUpperCase());
                        nameValuePair.put("city",city.toUpperCase());
                        nameValuePair.put("state",state.toUpperCase());
                        nameValuePair.put("country",country.toUpperCase());
                        nameValuePair.put("postalCode",postalCode.toUpperCase());
                       // nameValuePair.put("knownname",knownName.toUpperCase());

                        DBConnect dc = new DBConnect("http://bhartiyamattress.com/sendLavRequest.php", "POST", nameValuePair);
                        // DBConnect.setDataObject("http://bhartiyamattress.com/fetchLavData.php", "POST", parameter);
                        dc.execute();

                                           }
                    catch (Exception ex)
                    {
                        showCustomAlert("Error!",ex.getMessage());
                    }
                    //  System.out.println(jsonArray);

                    // End Loop
                }
                else
                {
                   // Toast.makeText(getApplicationContext(),"Some fields may be empty",Toast.LENGTH_SHORT).show();
                    showCustomAlert("Error!","Some field may be empty or the user location is unknown");

                }

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
    //    LatLng sydney = new LatLng(-34, 151);
        //   mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = getLastKnownLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        final LatLng MYLOCATION=new LatLng(latitude,longitude);//mMa.get;

        marker = mMap.addMarker(new MarkerOptions()
                .position(MYLOCATION)
                .draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MYLOCATION));
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //  marker=perth;

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                // TODO Auto-generated method stub
                // Here your code
//                Toast.makeText(RequestLav.this, "Dragging Start",
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub
//                Toast.makeText(
//                        RequestLav.this,
//                        "Lat " + marker.getPosition().latitude + " "
//                                + "Long " + marker.getPosition().latitude,
//                        Toast.LENGTH_LONG).show();
//                System.out.println("yalla b2a "
//                        + mMap.getMyLocation().getLatitude());
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub
                // Toast.makeText(MainActivity.this, "Dragging",
                // Toast.LENGTH_SHORT).show();
          //      System.out.println("Draagging");
            }
        });

    }
    @Override
    public void onConnected(Bundle bundle) {
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
        ProgressDialog progressDialog=new ProgressDialog(RequestLav.this);
        //ProgressDialog progress = new ProgressDialog(this);

        protected void onPreExecute() {
        progressDialog.setMessage("Downloading your data...");
            progressDialog.setCancelable(true);
        progressDialog.show();
       // progressDialog.setOnCancelListener(new OnCancelListener() {
         //   public void onCancel(DialogInterface arg0) {
            //    DBConnect.this.cancel(true);
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
                System.out.println(statusCode);
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
                    json_data = new JSONObject(result);
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

            progressDialog.cancel();

try {
//                for (int i = 0; i < jArray.length(); i++) {
//
                    // JSONObject jObject = jArray.getJSONObject(i);

                //    json_data = jArray.getJSONObject(i);

                    System.out.println(json_data);

                    //System.out.println(json_data);
                    res=json_data.getInt("result");


                    reqNo=json_data.getString("reqno");


    if(res==1)
    {
        EditText name=(EditText)findViewById(R.id.nameUser);
        EditText email=(EditText)findViewById(R.id.emailId);
        EditText mobile=(EditText)findViewById(R.id.mobile);

        Intent intent=new Intent(RequestLav.this,ReqNoDisplay.class);
        intent.putExtra("ReqNo",reqNo);
        name.setText("");
        email.setText("");
        mobile.setText("");
        startActivity(intent);
    }
    else
    {

    }

    //tid[i] = json_data.getInt("thread_id");
                    //chk = 1;

                    //  jObject= jArray.getJSONObject(i);

                    //String name = jObject.getString("id");
                    //String tab1_text = jObject.getString("RequestNo");

                    //System.out.print(name +" "+tab1_text);
                    // int active = jObject.getInt("active");

             //   }
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }

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
}

