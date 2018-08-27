package timothy.meteo2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    boolean shouldExecuteOnResume;

    RecyclerView recyclerView;

    ClimatAdaptateur climatAdaptateur;

    int locationIdInt = 0;

    GPSTracker gps;
    String cityNameAndCountryFromGPS = null;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    int TAG_CODE_PERMISSION_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//TODO Add un refresh here pour le cas ou c'est la première fois sinon il faut le relancer suite à l'acceptation des permissions
        ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                TAG_CODE_PERMISSION_LOCATION);

        gps =new GPSTracker(this);

        double latitude1 = gps.getLatitude();
        Log.i("Debug", "LAT|"+String.valueOf(latitude1));

        double longitude1 = gps.getLongitude();
        Log.i("Debug", "LONG|"+String.valueOf(longitude1));

        cityNameAndCountryFromGPS = getCityNameAndCountryFromGPS(latitude1,longitude1);
        Log.i("Debug", cityNameAndCountryFromGPS);

        shouldExecuteOnResume = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        if (isNetworkAvailable(MainActivity.this)) {
            //if connection we go

            new Request5Days().execute();

        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setCancelable(false);
            builder1.setTitle("Error 522");
            builder1.setMessage("Verify your internet connection");

            builder1.setPositiveButton("Quit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            System.exit(0);
                        }
                    });

            AlertDialog alert1 = builder1.create();
            alert1.show();
        }
    }

    @Override
    public void onResume() {
        // After a pause OR at startup
        super.onResume();

        if(shouldExecuteOnResume){
            // Si la valeur dans les properties ont changé on recharge
            // Y'a un comportement bizarre c'est un peu noel au lancement...
            String locationId = getUserSettings();
            if (Integer.parseInt(locationId) != locationIdInt){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        } else{
            shouldExecuteOnResume = true;
        }
    }

    private class Request5Days extends AsyncTask<Void,Void,Climat>{

        @Override
        protected Climat doInBackground(Void... params) {

            // Get id saves
            String locationId = getUserSettings();
            locationIdInt = Integer.parseInt(locationId);
            String locationString = null;

            // get list of id
            String [] idOflocationList;
            idOflocationList = getResources().getStringArray(R.array.pref_location_list_values);

            // get list of titles
            String [] titlessOflocationList;
            titlessOflocationList = getResources().getStringArray(R.array.pref_location_list_titles);

            // Get the title corresponding to ID
            for (int i=0; i<idOflocationList.length;i++){
                int titlessOflocationListInt = Integer.parseInt(idOflocationList[i]);

                if (titlessOflocationListInt == locationIdInt){
                    locationString = titlessOflocationList[i];
                }
            }

            Climat climat = null;

            Log.i("Debug", "PosGPS"+cityNameAndCountryFromGPS);

            String QUERY_PARAM = "q";
            final String UNITS_PARAM = "units";
            final String APPID_PARAM = "appid";

            // si different de -1 (vide) ou 1 (si c'est pas le GPS)
            if (locationIdInt != -1 && locationIdInt != 1){
                // Put the name of the ity at the top
                    String NameOfApp = getApplicationContext().getResources().getString(R.string.app_name);
                    getSupportActionBar().setTitle(NameOfApp + " - " + locationString);

                // Modifier les paramètres pour la requete
                QUERY_PARAM = "id";
                cityNameAndCountryFromGPS = String.valueOf(locationIdInt);
                Log.i("Debug", "PosFromFile|"+cityNameAndCountryFromGPS);
            }
            else{ // si c'est le GPS
                // Put the name of the ity at the top
                String NameOfApp = getApplicationContext().getResources().getString(R.string.app_name);
                getSupportActionBar().setTitle(NameOfApp + " - " + "GPS");
            }

            Uri.Builder uriBuilder = new  Uri.Builder();
            uriBuilder.scheme("http")
                    .authority("api.openweathermap.org")
                    .appendPath("data"
                    ).appendPath("2.5")
                    .appendPath("forecast")
                    .appendQueryParameter(QUERY_PARAM,cityNameAndCountryFromGPS)
                    .appendQueryParameter(UNITS_PARAM,"metric")
                    .appendQueryParameter(APPID_PARAM,"9b4de6e92002d98a0ffa51e62ae937b8")
                    .build();

            URL url = null;

            try {
                url = new URL(uriBuilder.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            //ClimatElement climatElement = null;

            try {
                Response reponse = client.newCall(request).execute();
                String bodyReponse = reponse.body().string();

                Location location = parseLocation(bodyReponse);

                climat = parseMain(bodyReponse);
                climat.setLocation(location);


                // climatElement = parseJSON(bodyReponse);
                Log.i("Reponse",reponse.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return climat;
        }

        @Override
        protected void onPostExecute(Climat climat) {
            super.onPostExecute(climat);

            //Toast.makeText(MainActivity.this, climat.toString(), Toast.LENGTH_SHORT).show();
            String nomDuJour = climat.tempsArray.get(0).nomDeJour;
            int temperature = (int)climat.climatInfoArray.get(0).temperature;

/*            jour_item.setText(nomDuJour);
            temperature_item.setText(temperature + "° C");*/

            climatAdaptateur = new ClimatAdaptateur(climat);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(climatAdaptateur);
        }
    }

    private Climat parseMain(String bodyReponse) throws JSONException {
        JSONObject mainJSON = new JSONObject(bodyReponse);
        JSONArray list = mainJSON.getJSONArray("list");

        ArrayList<Temps> tempsArray = new ArrayList<Temps>();
        ArrayList<ClimatInfo> climatInfoArray = new ArrayList<ClimatInfo>();

        int i = 0;
        for(i=0; i<list.length(); i++){

            JSONObject elementi = list.getJSONObject(i);
            Temps tempsi = parseTemps(elementi);

            // On assume que le resultat de la météo du jour est à 13h

            //Si > 15h je prends
            if (i==0){
                if (Integer.valueOf(tempsi.dt_text.substring(11,13)) > 15){
                    tempsArray.add(tempsi);
                    climatInfoArray.add(parseClimatInfo(elementi));
                }
            }

            //Si temps == 15h je prends
            if (tempsi.dt_text.substring(11,13).equals("15")){

                tempsArray.add(tempsi);
                climatInfoArray.add(parseClimatInfo(elementi));
            }
        }
        Climat climat = new Climat(tempsArray,climatInfoArray);
        return climat;

    }

    private Temps parseTemps(JSONObject element0) throws JSONException {
        //Temps
        int dt = element0.getInt("dt");
        String dt_text = element0.getString("dt_txt");
        Temps temps0 = new Temps(dt, dt_text);
        return temps0;
    }

    private ClimatInfo parseClimatInfo(JSONObject element0) throws JSONException {
        ////main
        JSONObject main = element0.getJSONObject("main");
        float temperature = (float) main.getDouble("temp");
        float pression = (float) main.getDouble("pressure");
        float humidity = (float) main.getDouble("humidity");

        ////weather
        JSONArray weather = element0.getJSONArray("weather");
        JSONObject weather0 = weather.getJSONObject(0);
        int weatherId = weather0.getInt("id");
        String weatherMain = weather0.getString("main");
        String weatherDescription = weather0.getString("description");
        String weatherIcon = weather0.getString("icon");

        ////vent
        JSONObject vent = element0.getJSONObject("wind");
        float vent_vitesse = (float) vent.getDouble("speed");

        ClimatInfo climatInfo = new ClimatInfo(temperature,
                pression,
                humidity,
                vent_vitesse,
                weatherMain,
                weatherDescription,
                weatherIcon,
                weatherId);

        return climatInfo;
    }

    private Location parseLocation(String bodyReponse) throws JSONException {

        JSONObject mainJSON = new JSONObject(bodyReponse);
        JSONObject city = mainJSON.getJSONObject("city");
        int id = city.getInt("id");
        String ville = city.getString("name");
        String pays = city.getString("country");
        JSONObject coord = city.getJSONObject("coord");
        float lat = (float) coord.getDouble("lat");
        float lon = (float) coord.getDouble("lon");

        Location location = new Location(id,lat,lon,ville,pays);

        return location;
    }

    private ClimatElement parseJSON(String bodyReponse) throws JSONException {
        JSONObject mainJSON = new JSONObject(bodyReponse);
        String ville = mainJSON.get("name").toString();

        JSONObject sys = mainJSON.getJSONObject("sys");
        String pays = sys.get("country").toString();

        String location = ville + ", " + pays;

        JSONObject main = mainJSON.getJSONObject("main");
        String minTemp = main.get("temp_min").toString();
        String maxTemp = main.get("temp_max").toString();

        String dt = mainJSON.get("dt").toString();
        int timestamp = Integer.valueOf(dt);

        Calendar mydate = Calendar.getInstance();
        mydate.setTimeInMillis((long)timestamp*1000);

        String nomDuMois = Utilites.getMois(mydate.get(Calendar.MONTH));
        String nomDuJour = Utilites.getJour(mydate.get(Calendar.DAY_OF_WEEK));

        ClimatElement climatElement = new ClimatElement(
                ville,
                pays,
                location,
                minTemp,
                maxTemp,
                timestamp,
                nomDuMois,
                nomDuJour,
                mydate.get(Calendar.DAY_OF_MONTH),
                mydate.get(Calendar.MONTH),
                mydate.get(Calendar.YEAR));

        return climatElement;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param context
     * @return true if connected to internet
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            // if no network is available networkInfo will be null
            // otherwise check if we are connected
            return (networkInfo != null && networkInfo.isConnected());
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String savedValue = sharedPrefs.getString("location_list", "-1");
        return savedValue;
    }

    public String getCityNameAndCountryFromGPS(double latitude, double longitude) {
    /*------- To get city name from coordinates -------- */
        String resultat = null;

/*        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {*/
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            String cityName = null;
            String country = null;
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(latitude,
                        longitude, 1);
                if (addresses.size() > 0) {
                    //System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                    country = addresses.get(0).getCountryCode().toLowerCase();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = "LONG|"+longitude + "\n" + "LAT|" + latitude + "\n\nMy Current City is: "
                    + cityName +"," + country;
            Log.i("DebugHARAR", s);
            resultat = cityName +"," + country;

//            }
/*        else {

            Log.i("Debug", "HEREOK34");

            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
            Toast.makeText(this, "prob de permission, accept et restart app", Toast.LENGTH_LONG).show();

            resultat = "failed";
        }*/

        return resultat.toString();
    }
}
