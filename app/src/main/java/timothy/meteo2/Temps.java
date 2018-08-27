package timothy.meteo2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by timot on 23/06/2017.
 */

public class Temps implements Serializable {

    int dt_unix;
    int year;
    int mois;
    int jour;
    String dt_text;
    String nomDeYear;
    String nomDeMois;
    String nomDeJour;

    public Temps(int dt_unix, String dt_text) {
        this.dt_unix = dt_unix;
        this.dt_text = dt_text;

        Date date = new Date ();
        date.setTime((long)dt_unix*1000);

        Calendar mydate = Calendar.getInstance();
        mydate.setTime(date);
        //mydate.setTimeInMillis((long)dt_unix);

        this.year = mydate.get(Calendar.YEAR);
        this.mois = mydate.get(Calendar.MONTH);
        this.jour = mydate.get(Calendar.DATE);

        this.nomDeMois = Utilites.getMois(mydate.get(Calendar.MONTH));
        this.nomDeJour = Utilites.getJour(mydate.get(Calendar.DAY_OF_WEEK));

        int jfjfk = 0;
    }

    public int getDt_unix() {
        return dt_unix;
    }

    public void setDt_unix(int dt_unix) {
        this.dt_unix = dt_unix;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public String getDt_text() {
        return dt_text;
    }

    public void setDt_text(String dt_text) {
        this.dt_text = dt_text;
    }

    public String getNomDeYear() {
        return nomDeYear;
    }

    public void setNomDeYear(String nomDeYear) {
        this.nomDeYear = nomDeYear;
    }

    public String getNomDeMois() {
        return nomDeMois;
    }

    public void setNomDeMois(String nomDeMois) {
        this.nomDeMois = nomDeMois;
    }

    public String getNomDeJour() {
        return nomDeJour;
    }

    public void setNomDeJour(String nomDeJour) {
        this.nomDeJour = nomDeJour;
    }
}
