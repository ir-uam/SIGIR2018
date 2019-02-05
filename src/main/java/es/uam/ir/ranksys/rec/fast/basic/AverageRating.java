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
package es.uam.ir.ranksys.rec.fast.basic;

import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.function.IntPredicate;
import static java.util.stream.Collectors.toList;
import static org.ranksys.core.util.tuples.Tuples.tuple;
import org.ranksys.core.util.tuples.Tuple2id;
import static java.lang.Math.min;

/**
 * Average rating recommender. Non-personalized recommender that returns the
 * items with the greatest average rating value, according to the preference
 * data provided.
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class AverageRating<U, I> extends AbstractFastRecommender<U, I> {

    private final List<Tuple2id> popList;

    /**
     * Constructor.
     *
     * @param data preference data
     * @param threshold ratings with a value larger than or equal to this
     * threshold are considered relevant
     */
    public AverageRating(FastPreferenceData<U, I> data, double threshold) {
        super(data, data);

        double p = data.getAllUsers().mapToDouble(user -> data.getUserPreferences(user).filter(up -> up.v2 >= threshold).count()).sum() / data.numPreferences();
        double mu = data.numPreferences() * 1.0 / data.numItems();
        popList = data.getIidxWithPreferences()
                .mapToObj(iidx -> tuple(iidx, (data.getIidxPreferences(iidx).filter(ip -> ip.v2 >= threshold).count() + mu * p) * 1.0 / (data.numUsers(iidx) + mu)))
                .sorted(comparingDouble(Tuple2id::v2).reversed())
                .collect(toList());
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {

        List<Tuple2id> items = popList.stream()
                .filter(is -> filter.test(is.v1))
                .limit(min(maxLength, popList.size()))
                .collect(toList());

        return new FastRecommendation(uidx, items);
    }
}
