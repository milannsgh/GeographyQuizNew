package mdquizgames.geography.quiz;

public class Pitanje {
    private int id;
    private String slovaPitanja;
    private String slikaPitanja;
    private String originalnaSlika;
    private int bodovi;
    private String tekstPitanja;
    private String nazivGrupe;
    private String prikazaniNazivGrupe;
    private String slikaGrupe;

    public Pitanje() { }

    public String getOriginalnaSlika() {
        return originalnaSlika;
    }

    public void setOriginalnaSlika(String originalnaSlika) {
        this.originalnaSlika = originalnaSlika;
    }

    public String getPrikazaniNazivGrupe() {
        return prikazaniNazivGrupe;
    }

    public void setPrikazaniNazivGrupe(String prikazaniNazivGrupe) {
        this.prikazaniNazivGrupe = prikazaniNazivGrupe;
    }

    public String getSlovaPitanja() {
        return slovaPitanja;
    }

    public void setSlovaPitanja(String slovaPitanja) {
        this.slovaPitanja = slovaPitanja;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public String getSlikaPitanja() {
        return slikaPitanja;
    }

    public void setSlikaPitanja(String slikaPitanja) {
        this.slikaPitanja = slikaPitanja;
    }

    public int getBodovi() {
        return bodovi;
    }

    public void setBodovi(int bodovi) {
        this.bodovi = bodovi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazivGrupe() {
        return nazivGrupe;
    }

    public void setNazivGrupe(String nazivGrupe) {
        this.nazivGrupe = nazivGrupe;
    }

    public String getSlikaGrupe() {
        return slikaGrupe;
    }

    public void setSlikaGrupe(String slikaGrupe) {
        this.slikaGrupe = slikaGrupe;
    }
}
