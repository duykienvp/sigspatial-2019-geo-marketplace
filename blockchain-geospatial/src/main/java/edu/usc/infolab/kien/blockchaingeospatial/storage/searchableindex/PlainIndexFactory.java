package edu.usc.infolab.kien.blockchaingeospatial.storage.searchableindex;

import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.AreaBound;
import edu.usc.infolab.kien.blockchaingeospatial.storage.bound.BoundFactory;

public class PlainIndexFactory {
    /**
     * Create a plain index for a given area
     * @param areaBound
     * @return
     */
    public static PlainIndex createPlainIndex(AreaBound areaBound) {
        PlainIndex plainIndex = new PlainIndex();
        switch (areaBound) {
            case LOS_ANGELES:
                plainIndex.setBound(BoundFactory.createBound(areaBound));
                return plainIndex;
            default:
                return plainIndex;
        }
    }
}
