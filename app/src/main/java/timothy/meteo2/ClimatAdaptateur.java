package timothy.meteo2;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by timot on 23/06/2017.
 */

public class ClimatAdaptateur extends RecyclerView.Adapter<ClimatAdaptateur.ViewHolder> {

    Climat climat;

    public ClimatAdaptateur(Climat climat) {
        this.climat = climat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Déclare que item_climat est le template
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_climat, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ClimatInfo climatInfo = climat.climatInfoArray.get(position);
        Temps temps = climat.getTempsArray().get(position);

        String temperatureString = (int) climatInfo.getTemperature() + "° C";

        holder.jour.setText(temps.getNomDeJour());
        holder.temperature.setText(temperatureString);

        Context context = holder.icon.getContext();

        String iconUri = Utilites.getIconUri(climatInfo.getClimat_id());
        int iconId = context.getResources().getIdentifier(iconUri,null,context.getPackageName());

        Drawable iconDrawable = context.getResources().getDrawable(iconId);
        holder.icon.setImageDrawable(iconDrawable);

/*        if (iconUri == "scatteredClouds" || iconUri == "brokenClouds"){
            String backgroudString = "cloudyy";
            int backgroundId = context.getResources().getIdentifier(backgroudString,null,context.getPackageName());

            Drawable backgroundDrawable = context.getResources().getDrawable(backgroundId);
            holder.
        }
        else if (iconUri == "clearSky" || iconUri == "fewClouds"){

        }
        else {

        }*/

    }

    @Override
    public int getItemCount() {
        return climat.climatInfoArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView jour;
        TextView temperature;
        ImageView icon;


        public ViewHolder(View itemView) {
            super(itemView);

            jour = (TextView)itemView.findViewById(R.id.jour_tv);
            temperature = (TextView)itemView.findViewById(R.id.temperature_tv);
            icon = (ImageView) itemView.findViewById(R.id.icon_iv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    int position = getLayoutPosition();

                    Intent intent = new Intent(context,DetailActivity.class);
                    intent.putExtra(DetailActivity.LOCATION_CLEF,climat.location);
                    intent.putExtra(DetailActivity.TEMPS_CLEF,climat.tempsArray.get(position));
                    intent.putExtra(DetailActivity.CLIMATINFO_CLEF,climat.climatInfoArray.get(position));
                    context.startActivity(intent);
                }
            });
        }
    }


}
