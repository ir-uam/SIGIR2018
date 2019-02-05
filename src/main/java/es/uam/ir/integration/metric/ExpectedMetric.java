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

import es.uam.ir.integration.SampleableFunction;
import es.uam.ir.distribution.ItemProbabilities;
import es.uam.ir.recommender.Recommender;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public abstract class ExpectedMetric implements SampleableFunction {
    
    /**
     * 
     */
    public Recommender rec;
    
    /**
     * 
     */
    public ItemProbabilities probabilities;
    
    /**
     * 
     */
    public double splitRatio;
    
    /**
     * Constructor 
     * 
     * @param rec
     * @param probabilities
     * @param splitRatio 
     */
    public ExpectedMetric(Recommender rec, ItemProbabilities probabilities, double splitRatio) {
        this.rec = rec;
        this.splitRatio = splitRatio;
        this.probabilities = probabilities;
    }
    

    @Override
    public void sample() {
        probabilities.sample();
        rec.rank(probabilities);
    }
}
