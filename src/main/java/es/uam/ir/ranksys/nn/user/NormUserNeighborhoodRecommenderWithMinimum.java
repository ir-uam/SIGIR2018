/*
 * Copyright 2013 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This file is part of the UAM@SIGIR2018 library.
 *
 * The UAM@SIGIR2018 library is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The UAM@SIGIR2018 library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the UAM@SIGIR2018 library. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package es.uam.ir.ranksys.nn.user;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Variant of the normalized user-based nearest neighbors recommender which
 * requires a minimum of neighbors rating an item in order to consider it.
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class NormUserNeighborhoodRecommenderWithMinimum<U, I> extends NormUserNeighborhoodRecommender<U, I> {

    private final Function<Integer, Predicate<Integer>> minFilter;

    private Function<Integer, Predicate<Integer>> getMinFilter(int min) {
        return uidx -> {
            Int2DoubleOpenHashMap numMap = new Int2DoubleOpenHashMap();
            numMap.defaultReturnValue(0.0);
            neighborhood.getNeighbors(uidx).forEach(vs -> {
                data.getUidxPreferences(vs.v1).forEach(iv -> {
                    numMap.addTo(iv.v1, 1);
                });
            });
            return iidx -> numMap.get(iidx) >= min;
        };
    }

    /**
     * Constructor.
     *
     * @param data preference data
     * @param neighborhood item neighborhood
     * @param q exponent of the similarity
     * @param min minimum number of neighbors needed to be recommended.
     */
    public NormUserNeighborhoodRecommenderWithMinimum(FastPreferenceData<U, I> data, UserNeighborhood<U> neighborhood, int q, int min) {
        super(data, neighborhood, q);
        this.minFilter = getMinFilter(min);
    }

    @Override
    public Int2DoubleMap getScoresMap(int uidx) {
        Predicate<Integer> filter = minFilter.apply(uidx);
        Int2DoubleOpenHashMap scoresMap = new Int2DoubleOpenHashMap();

        super.getScoresMap(uidx).int2DoubleEntrySet()
                .stream()
                .filter(e -> filter.test(e.getIntKey()))
                .forEach(e -> scoresMap.addTo(e.getIntKey(), e.getDoubleValue()));

        return scoresMap;
    }

}
