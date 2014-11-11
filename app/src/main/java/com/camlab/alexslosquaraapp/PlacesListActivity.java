package com.camlab.alexslosquaraapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.api.client.http.HttpResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 10.11.2014.
 */
public class PlacesListActivity extends ListActivity implements LocationListener, View.OnClickListener {

    private final List<Place> places = new ArrayList<Place>();
    private ArrayAdapter<Place> listAdapter;
    private String accessToken;
    private String clientID;
    private String clientSecret;

    protected LocationManager locationManager;
    protected LocationListener locationListener;

    double latitude, longitude;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_places);

        accessToken = getIntent().getStringExtra("AccessToken");
        clientID = getIntent().getStringExtra("ClientID");
        clientSecret = getIntent().getStringExtra("ClientSecret");

        listAdapter = new ArrayAdapter<Place>(this, android.R.layout.simple_list_item_2, android.R.id.text1, places){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                final Place place = getItem(position);

                ((TextView) view.findViewById(android.R.id.text1)).setText(place.getName());
                ((TextView) view.findViewById(android.R.id.text2)).setText(place.getCategory() + '\n' + place.getDistance());
                return view;

            }
        };
        setListAdapter(listAdapter);

        //Get Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //powersave = LocationManager.NETWORK_PROVIDER; performance =  LocationManager.GPS_PROVIDER
        // update every 1 second
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Toast.makeText(this, "NETWORK_PROVIDER not enable. Please reload you device and try again.", Toast.LENGTH_LONG).show();
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
    }

    public void onListItemClick(ListView parent, View v, int position, long id){
        Place place = (Place) getListView().getItemAtPosition(position);
        Intent intent = new Intent(PlacesListActivity.this, PlaceActivity.class);
        intent.putExtra("Name", place.getName());
        intent.putExtra("Category", place.getCategory());
        intent.putExtra("Distance", place.getDistance());
        intent.putExtra("Address", place.getAddress());
        startActivity(intent);
    }
    public void onClick(View view) {}

    @Override
    public void onLocationChanged(Location location) {
        if (latitude == 0.0 || longitude == 0.0)
        {
            //firstRun
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            startLoading();
        }
        else
        {
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();

            double LatitudeDx = latitude - currentLatitude;
            double LongitudeDy = latitude - currentLongitude;

            // if device move to > 100m
            if (LatitudeDx > 100 || LatitudeDx < -100 || LongitudeDy > 100 || LongitudeDy < -100) {
                latitude = currentLatitude;
                longitude = currentLongitude;
                //reload:
                places.clear();
                startLoading();
            }
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void startLoading() {
        new NetworkPlacesLoading().execute();
    }
    class NetworkPlacesLoading extends AsyncTask<Void, Void, Void> {
        //String search = "40.7,-74";
        String search = latitude + "," + longitude;
        String radius = "3000";
        String v = "20141111";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PlacesListActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... params) {
            try {
                //Form reqest
                // https://api.foursquare.com/v2/venues/search?ll=40.7,-74&oauth_token=WSDWY152YZPXQUKNJZU3MPMXEDZMLT1CBC4HB1F31DADS3EI&v=20141111

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost("https://api.foursquare.com/v2/venues/search?" +
                        "ll=" + search +
                        "&radius=" + radius +
                        "&oauth_token=" + accessToken +
                        "&v=" + v);

                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                String responseText = EntityUtils.toString(entity);
                placesNamesParser(responseText);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        private void placesNamesParser(String jsonStr) {
            try {
                JSONObject object = new JSONObject(jsonStr);
                JSONArray array = object.getJSONObject("response").getJSONArray("venues");
                //get all venues
                for (int i = 0; i < array.length(); i++) {
                    //get current venues
                    JSONObject json = array.getJSONObject(i);

                    String placeName ="",placeCategory = "", placeLocation = "", distance = "";
                    //validate and get name
                    if (json.toString().contains("name")) placeName = json.getString("name");
                    //validate and get place
                    if (json.toString().contains("categories")) placeCategory = json.getJSONArray("categories").getJSONObject(0).getString("name");
                    //describe:
                    placeCategory = "Категория: " + placeCategory;
                    //validate and get and format address
                    if (json.toString().contains("formattedAddress")) placeLocation = json.getJSONObject("location").getString("formattedAddress")
                            .replaceAll("\\[","")
                            .replaceAll("\\]","")
                            .replaceAll("\"","");
                    else placeLocation = json.getJSONObject("location").getString("country");
                    //describe:
                    placeLocation = "Адрес: " + placeLocation;
                    //validate and get distance
                    if (json.toString().contains("distance")) distance = json.getJSONObject("location").getString("distance");
                    //describe:
                    distance = "Дистанция до места: " + distance+ "м";
                    places.add(new Place(placeName, placeCategory,distance,placeLocation));

                }


            }
            catch (JSONException e) {
                e.printStackTrace();

            }
        }

        protected void onPostExecute(Void params) {
            //Update
            listAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

}