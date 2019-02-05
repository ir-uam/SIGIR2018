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
import es.uam.eps.ir.ranksys.nn.user.UserNeighborhoodRecommender;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import static java.lang.Math.pow;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Normalized user-based nearest neighbors recommender.
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class NormUserNeighborhoodRecommender<U, I> extends UserNeighborhoodRecommender<U, I> {

    private final Function<U, BiFunction<I, Double, Double>> normalizationMap;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param neighborhood user neighborhood
     * @param q exponent of the similarity
     */
    public NormUserNeighborhoodRecommender(FastPreferenceData<U, I> data, UserNeighborhood<U> neighborhood, int q) {
        super(data, neighborhood, q);
        this.normalizationMap = user -> {
            int uidx = data.user2uidx(user);
            Int2DoubleOpenHashMap normMap = new Int2DoubleOpenHashMap();

            normMap.defaultReturnValue(0.0);
            neighborhood.getNeighbors(uidx).forEach(vs -> {
                double w = pow(vs.v2, q);
                data.getUidxPreferences(vs.v1).forEach(iv -> {
                    normMap.addTo(iv.v1, w);
                });
            });
            return (item, value) -> value * 1.0 / normMap.get(data.item2iidx(item));
        };
    }

    @Override
    public Int2DoubleMap getScoresMap(int uidx) {
        Int2DoubleMap scoresMap = super.getScoresMap(uidx);
        BiFunction<I, Double, Double> norm = normalizationMap.apply(data.uidx2user(uidx));
        scoresMap.replaceAll((idx, value) -> norm.apply(data.iidx2item(idx), value));

        return scoresMap;
    }
}
