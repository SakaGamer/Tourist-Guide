package kh.com.touristguide.helpers;

public class Distance {

//    private double lat1;
//    private double lng1;
//    private double lat2;
//    private double lng2;
//    private static double distance;
    private static final int EARTH_RADIUS = 6371; // km

    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (EARTH_RADIUS * c);
    }

}
