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
package es.uam.ir.sigir2018;

import es.uam.ir.integration.MonteCarlo;
import es.uam.ir.distribution.ItemProbabilities;
import es.uam.ir.distribution.NoAssumptionProbabilitySampler;
import es.uam.ir.recommender.RandomRecommender;
import es.uam.ir.recommender.PopularityRecommender;
import es.uam.ir.recommender.AverageRatingRecommender;
import es.uam.ir.integration.metric.ExpectedTruePrecision;
import es.uam.ir.integration.metric.ExpectedObservedPrecision;
import es.uam.ir.recommender.ObservedOptimalRecommender;
import es.uam.ir.recommender.TrueOptimalRecommender;
import es.uam.ir.util.Timer;
import java.io.PrintStream;

/**
 * Method to generate figure 3 of the paper:
 *
 * R. Cañamares, P. Castells. Should I Follow the Crowd? A Probabilistic
 * Analysis of the Effectiveness of Popularity in Recommender Systems. 41st
 * Annual International ACM SIGIR Conference on Research and Development in
 * Information Retrieval (SIGIR 2018). Ann Arbor, Michigan, USA, July 2018, pp.
 * 415-424
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public class Figure3 {

    /**
     * Method to generate figure 3 of the paper:
     *
     * R. Cañamares, P. Castells. Should I Follow the Crowd? A Probabilistic
     * Analysis of the Effectiveness of Popularity in Recommender Systems. 41st
     * Annual International ACM SIGIR Conference on Research and Development in
     * Information Retrieval (SIGIR 2018). Ann Arbor, Michigan, USA, July 2018,
     * pp. 415-424
     *
     * @param out
     */
    public static void run(PrintStream out) {
        Timer.start("\nStarting Figure 3 (this one should not take much longer than a minute or two)");
        out.println("------------------ Figure 3 ------------------\n");

        int nItems = 3700;
        int nSamples = 10000;
        ItemProbabilities probabilities = new NoAssumptionProbabilitySampler();

        out.println("\tRandom\tPopularity\tAverage rating\tOptimal");
        double splitRatio = 0.8;
        probabilities.init(nItems);
        out.println("Observed P@1"
                + "\t" + MonteCarlo.integrate(new ExpectedObservedPrecision(new RandomRecommender(), probabilities, splitRatio), nSamples)
                + "\t" + MonteCarlo.integrate(new ExpectedObservedPrecision(new PopularityRecommender(), probabilities, splitRatio), nSamples)
                + "\t" + MonteCarlo.integrate(new ExpectedObservedPrecision(new AverageRatingRecommender(), probabilities, splitRatio), nSamples)
                + "\t" + MonteCarlo.integrate(new ExpectedObservedPrecision(new ObservedOptimalRecommender(splitRatio), probabilities, splitRatio), nSamples));

        splitRatio = 1;
        probabilities.init(nItems);
        out.println("True P@1"
                + "\t" + MonteCarlo.integrate(new ExpectedTruePrecision(new RandomRecommender(), probabilities, splitRatio), nSamples)
                + "\t" + MonteCarlo.integrate(new ExpectedTruePrecision(new PopularityRecommender(), probabilities, splitRatio), nSamples)
                + "\t" + MonteCarlo.integrate(new ExpectedTruePrecision(new AverageRatingRecommender(), probabilities, splitRatio), nSamples)
                + "\t" + MonteCarlo.integrate(new ExpectedTruePrecision(new TrueOptimalRecommender(), probabilities, splitRatio), nSamples));

    }
}
