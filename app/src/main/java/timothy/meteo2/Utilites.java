package timothy.meteo2;

/**
 * Created by timot on 22/06/2017.
 */

public class Utilites {

    static String defaultWeather = "@drawable/cloudy";
    static String thunderstorm = "@drawable/light_bolt";
    static String showerRain = "@drawable/rain";
    static String rain = "@drawable/rain_1";
    static String snow = "@drawable/snowflake";
    static String mist = "@drawable/mist";
    static String tornado = "@drawable/twister";
    static String clearSky = "@drawable/sun";
    static String fewClouds = "@drawable/cloudy_1";
    static String scatteredClouds = "@drawable/cloud";
    static String brokenClouds = "@drawable/cloudy_2";





    public static String getMois (int mois){

        String nomDuMois = null;
        switch (mois){
            case 0:
                nomDuMois = "Janvier";
                break;
            case 1:
                nomDuMois = "Février";
                break;
            case 2:
                nomDuMois = "Mars";
                break;
            case 3:
                nomDuMois = "Avril";
                break;
            case 4:
                nomDuMois = "Mai";
                break;
            case 5:
                nomDuMois = "Juin";
                break;
            case 6:
                nomDuMois = "Juillet";
                break;
            case 7:
                nomDuMois = "Aout";
                break;
            case 8:
                nomDuMois = "Septembre";
                break;
            case 9:
                nomDuMois = "Octobre";
                break;
            case 10:
                nomDuMois = "Novembre";
                break;
            case 11:
                nomDuMois = "Décembre";
                break;
        }
        return nomDuMois;
    }

    public static String getJour (int jour){

        String nomDuJour = null;
        switch (jour){
            case 0:
                nomDuJour = "Dimanche";
                break;
            case 1:
                nomDuJour = "Dimanche";
                break;
            case 2:
                nomDuJour = "Lundi";
                break;
            case 3:
                nomDuJour = "Mardi";
                break;
            case 4:
                nomDuJour = "Mercredi";
                break;
            case 5:
                nomDuJour = "Jeudi";
                break;
            case 6:
                nomDuJour = "Vendredi";
                break;
            case 7:
                nomDuJour = "Samedi";
                break;
        }
        return nomDuJour;

    }

    public static String getIconUri (int iconId){

        String toReturn = defaultWeather;

        if (iconId >= 200 && iconId <= 232){
            toReturn = thunderstorm;
        }
        if (iconId >= 300 && iconId <= 321){
            toReturn = showerRain;
        }
        if (iconId >= 500 && iconId <= 504){
            toReturn = rain;
        }
        if (iconId >= 511 && iconId <= 531){
            toReturn = showerRain;
        }
        if (iconId >= 600 && iconId <= 622){
            toReturn = snow;
        }
        if (iconId >= 701 && iconId <= 771){
            toReturn = mist;
        }
        if (iconId == 781){
            toReturn = tornado;
        }
        if (iconId == 800){
            toReturn = clearSky;
        }
        if (iconId == 801){
            toReturn = fewClouds;
        }
        if (iconId == 802){
            toReturn = scatteredClouds;
        }
        if (iconId == 803 || iconId == 804){
            toReturn = brokenClouds;
        }

        return toReturn;
    }

}

