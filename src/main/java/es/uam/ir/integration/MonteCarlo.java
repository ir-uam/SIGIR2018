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
package es.uam.ir.integration;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public class MonteCarlo {
    
    /**
     * 
     * @param f
     * @param nSamples
     * @return 
     */
    public static double integrate(SampleableFunction f, int nSamples) {
        double sum = 0;
        for (int i = 0; i < nSamples; i++) {
            f.sample();
            sum += f.value();
        }
        return sum / nSamples;
    }
}
