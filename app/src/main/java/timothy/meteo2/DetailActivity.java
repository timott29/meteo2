package timothy.meteo2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    public static String LOCATION_CLEF = "Location";
    public static String TEMPS_CLEF = "TempsArray";
    public static String CLIMATINFO_CLEF = "ClimatInfoArray";

    TextView tv_detail_titre;
    TextView tv_detail_temp;
    TextView tv_detail_pression;
    TextView tv_detail_vent;
    TextView tv_detail_humidity;
    TextView tv_detail_location;
    ImageView iv_detail_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)this).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_detail_titre = (TextView)findViewById(R.id.tv_detail_titre);
        tv_detail_temp = (TextView)findViewById(R.id.tv_detail_temp);
        tv_detail_pression = (TextView)findViewById(R.id.tv_detail_pression);
        tv_detail_vent = (TextView)findViewById(R.id.tv_detail_vent);
        tv_detail_humidity = (TextView)findViewById(R.id.tv_detail_humidity);
        tv_detail_location = (TextView)findViewById(R.id.tv_detail_location);
        iv_detail_logo = (ImageView)findViewById(R.id.iv_detail_logo);

        Location location = (Location)getIntent().getSerializableExtra(LOCATION_CLEF);
        Temps temp = (Temps) getIntent().getSerializableExtra(TEMPS_CLEF);
        ClimatInfo climatInfo = (ClimatInfo) getIntent().getSerializableExtra(CLIMATINFO_CLEF);

        String titreItem = temp.getNomDeJour() + " " +
                temp.getJour() + " " +
                temp.getNomDeMois() + " " +
                temp.getYear();

        tv_detail_titre.setText(titreItem);
        tv_detail_temp.setText("Temperature: " + (int)climatInfo.getTemperature() + "° C");
        tv_detail_pression.setText("Pression: " + (int)climatInfo.getPression() + " hPa");
        tv_detail_vent.setText("Vent: " + climatInfo.getVent_vitesse() + " mps");
        tv_detail_humidity.setText("Humidité: " +  (int)climatInfo.getHumidity() + " %");
        tv_detail_location.setText(location.getVille() + ", " + location.getPays());

        String iconUri = Utilites.getIconUri(climatInfo.getClimat_id());
        int iconId = getResources().getIdentifier(iconUri,null,getPackageName());

        Drawable iconDrawable = getResources().getDrawable(iconId);
        iv_detail_logo.setImageDrawable(iconDrawable);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
