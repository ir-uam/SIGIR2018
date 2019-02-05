
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
package es.uam.ir.recommender;

import es.uam.ir.distribution.ItemProbabilities;
import java.util.Arrays;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public abstract class Recommender {
    
    /**
     * 
     * @param item
     * @param probabilities
     * @return 
     */
    public abstract double f(int item, ItemProbabilities probabilities);

    /**
     * 
     * @param probabilities 
     */
    public void rank(ItemProbabilities probabilities) {
        Arrays.sort(probabilities.items, (i, j) -> {
            return (int) Math.signum(f(j, probabilities) - f(i, probabilities));
        });
    }
}
