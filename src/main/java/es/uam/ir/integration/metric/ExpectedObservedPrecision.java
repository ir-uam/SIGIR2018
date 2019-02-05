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
package es.uam.ir.integration.metric;

import es.uam.ir.distribution.ItemProbabilities;
import es.uam.ir.recommender.Recommender;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public class ExpectedObservedPrecision extends ExpectedMetric {

    /**
     * Constructor 
     * 
     * @param rec
     * @param probabilities
     * @param splitRatio 
     */
    public ExpectedObservedPrecision(Recommender rec, ItemProbabilities probabilities, double splitRatio) {
        super(rec, probabilities, splitRatio);
    }

    @Override
    public double value() {
        double precision = 0;
        double ptrain = 1;
        for (int item = 0; item < probabilities.nItems; item++) {
            precision += ptrain * probabilities.prelrated[probabilities.items[item]];
            ptrain *= splitRatio * probabilities.prated[probabilities.items[item]];
        }
        return (1-splitRatio) * precision;
    }

}
