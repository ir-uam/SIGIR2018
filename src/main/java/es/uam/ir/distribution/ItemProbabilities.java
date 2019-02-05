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
package es.uam.ir.distribution;

import java.util.Random;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public abstract class ItemProbabilities {
    
    /**
     * 
     */
    public int nItems;
    
    /**
     * 
     */
    public Integer items[];
    
    /**
     * 
     */
    public double[] prel, prated, prelrated;
    
    /**
     * 
     */
    protected Random rnd;
    
    /**
     * 
     * @param nItems 
     */
    public void init(int nItems) {
        this.nItems = nItems;
        items = new Integer[nItems];
        prel = new double[nItems];
        prated = new double[nItems];
        prelrated = new double[nItems];
        rnd = new Random();
    }

    /**
     * 
     */
    public abstract void sample();
}
