package mdquizgames.geography.quiz;

import java.util.ArrayList;
import java.util.List;

public class GrupaPitanja {
    private String nazivGrupe;
    private String prikazaniNazivGrupe;
    List<Pitanje> pitanjaGrupe;
    private String slikaGrupe;

    public GrupaPitanja() {
        this.nazivGrupe = "DefaultNaziv";
        this.pitanjaGrupe = new ArrayList<>();
        this.slikaGrupe = "DefaultSlika";
    }

    public String getPrikazaniNazivGrupe() {
        return prikazaniNazivGrupe;
    }

    public void setPrikazaniNazivGrupe(String prikazaniNazivGrupe) {
        this.prikazaniNazivGrupe = prikazaniNazivGrupe;
    }

    public String getNazivGrupe() {
        return nazivGrupe;
    }

    public void setNazivGrupe(String nazivGrupe) {
        this.nazivGrupe = nazivGrupe;
    }

    public List<Pitanje> getPitanjaGrupe() {
        return pitanjaGrupe;
    }

    public void setPitanjaGrupe(List<Pitanje> pitanjaGrupe) {
        this.pitanjaGrupe = pitanjaGrupe;
    }

    public String getSlikaGrupe() {
        return slikaGrupe;
    }

    public void setSlikaGrupe(String slikaGrupe) {
        this.slikaGrupe = slikaGrupe;
    }
}

