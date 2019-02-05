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

import java.io.IOException;
import java.io.PrintStream;

/**
 * Method to generate figures 3, 5 and 6 of the paper:
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
public class Main {

    /**
     * Method to generate figures 3, 5 and 6 of the paper:
     *
     * R. Cañamares, P. Castells. Should I Follow the Crowd? A Probabilistic
     * Analysis of the Effectiveness of Popularity in Recommender Systems. 41st
     * Annual International ACM SIGIR Conference on Research and Development in
     * Information Retrieval (SIGIR 2018). Ann Arbor, Michigan, USA, July 2018,
     * pp. 415-424
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream("results.txt");
        Figure3.run(out);
        Figure5.run(out);
        Figure6.run(out);
        out.close();
    }

}
