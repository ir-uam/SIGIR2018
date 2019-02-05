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
package es.uam.ir.datagenerator;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import static org.ranksys.formats.parsing.Parsers.lp;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public class MapNegativeRatingsTo0 {

    /**
     * 
     * @param data
     * @param threshold
     * @return
     * @throws IOException 
     */
    public static FastPreferenceData<Long, Long> run(FastPreferenceData<Long, Long> data, double threshold) throws IOException {

        ByteArrayOutputStream newDataOutputStream = new ByteArrayOutputStream();
        PrintStream newData = new PrintStream(newDataOutputStream);

        data.getAllUsers().forEachOrdered(user -> {
            data.getUserPreferences(user).forEachOrdered(up -> newData.println(user + "\t" + up.v1 + "\t" + Math.max(up.v2 - threshold + 1, 0)));
        });

        ByteArrayInputStream newDataInputStream = new ByteArrayInputStream(newDataOutputStream.toByteArray());

        return SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(newDataInputStream, lp, lp), data, data);
    }

}
