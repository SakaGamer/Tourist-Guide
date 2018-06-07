package kh.com.touristguide.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Province implements Serializable {

    private String id;
    private String name;
    private String nameKh;
    private String photoUrl;
    private String iso;
    private long area;
    private long density;
    private long population;
//    private Map<String, Boolean> nearProvinces = new HashMap<>();
//    private Map<String, Boolean> nearProvinceKh = new HashMap<>();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameKh() {
        return nameKh;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getIso() {
        return iso;
    }

    public long getArea() {
        return area;
    }

    public long getDensity() {
        return density;
    }

    public long getPopulation() {
        return population;
    }

    // end class
}
