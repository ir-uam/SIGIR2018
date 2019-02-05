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
 * Optimal non-personalized recommender in terms of true precision. 
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class OptimalTrueRanking<U, I> extends AbstractFastRecommender<U, I> {

    private final List<Tuple2id> popList;

    /**
     * Constructor.
     *
     * @param trainData train preference data
     * @param relData preference data to estimate relevance
     * @param threshold ratings with a value larger than or equal to this
     * threshold are considered relevant
     */
    public OptimalTrueRanking(FastPreferenceData<U, I> trainData, FastPreferenceData<U, I> relData, double threshold) {
        super(trainData, trainData);

        int m = trainData.numUsers();

        popList = trainData.getIidxWithPreferences()
                .mapToObj(iidx -> {
                    long rel = relData.getIidxPreferences(iidx).filter(ip -> ip.v2 >= threshold).count();
                    long relTrain = trainData.getIidxPreferences(iidx).filter(ip -> ip.v2 >= threshold).count();
                    long mTrain = trainData.numUsers(iidx);
                    return tuple(iidx, (rel - relTrain) * 1.0 / (m - mTrain));
                })
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
