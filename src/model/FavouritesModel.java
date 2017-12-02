package model;

/**
 * Created by angel on 02/12/2017.
 */
public class FavouritesModel {
    private String tipusResultat;
    private String titol;
    private String descripcio;
    private String nomCanal;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipusResultat() {
        return tipusResultat;
    }

    public void setTipusResultat(String tipusResultat) {
        this.tipusResultat = tipusResultat;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public String getNomCanal() {
        return nomCanal;
    }

    public void setNomCanal(String nomCanal) {
        this.nomCanal = nomCanal;
    }

    @Override
    public String toString() {
        return "FavouritesModel{" +
                "tipusResultat='" + tipusResultat + '\'' +
                ", titol='" + titol + '\'' +
                ", descripcio='" + descripcio + '\'' +
                ", nomCanal='" + nomCanal + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
