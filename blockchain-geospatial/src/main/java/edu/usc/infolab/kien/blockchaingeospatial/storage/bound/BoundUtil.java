package edu.usc.infolab.kien.blockchaingeospatial.storage.bound;

public class BoundUtil {
    /**
     * Whether a point is inside a bound
     * @param bound
     * @param lat
     * @param lon
     * @return
     */
    public static boolean isInside(Bound bound, double lat, double lon) {
        return bound.getMinLat() <= lat &&
            lat <= bound.getMaxLat() &&
            bound.getMinLon() <= lon &&
            lon < bound.getMaxLon();
    }
}
