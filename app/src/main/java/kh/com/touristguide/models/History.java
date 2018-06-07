package kh.com.touristguide.models;

public class History {

    private String id;
    private String deviceUniqueId;
    private String placeId;
    private String placeCategories;
    private long timestamp;

    public History() {
    }

    public History(String id, String deviceUniqueId, String placeId, String placeCategories, long timestamp) {
        this.id = id;
        this.deviceUniqueId = deviceUniqueId;
        this.placeId = placeId;
        this.placeCategories = placeCategories;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceCategories() {
        return placeCategories;
    }

    public void setPlaceCategories(String placeCategories) {
        this.placeCategories = placeCategories;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
