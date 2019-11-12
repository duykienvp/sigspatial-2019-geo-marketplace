package edu.usc.infolab.kien.blockchaingeospatial.storage.bound;

import edu.usc.infolab.kien.blockchaingeospatial.config.Config;

public class BoundFactory {
    /**
     * Create bound for a given area
     * @param areaBound
     * @return
     */
    public static Bound createBound(AreaBound areaBound) {
        Bound bound = new Bound();
        switch (areaBound) {
            case LOS_ANGELES:
                bound.setMaxLat(Config.getGridLosAngelesMaxLat());
                bound.setMinLat(Config.getGridLosAngelesMinLat());
                bound.setMaxLon(Config.getGridLosAngelesMaxLon());
                bound.setMinLon(Config.getGridLosAngelesMinLon());

                return bound;
            default:
                return bound;
        }
    }
}
