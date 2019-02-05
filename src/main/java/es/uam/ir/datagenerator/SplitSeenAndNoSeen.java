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

import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.ranksys.formats.index.ItemsReader;
import org.ranksys.formats.index.UsersReader;
import static org.ranksys.formats.parsing.Parsers.lp;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public class SplitSeenAndNoSeen {

    /**
     * 
     * @param inputPath
     * @return
     * @throws IOException 
     */
    public static FastPreferenceData<Long, Long>[] run(String inputPath) throws IOException {
        String dataPath = inputPath + "data.txt";
        String usersPath = inputPath + "users.txt";
        String itemsPath = inputPath + "items.txt";

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(usersPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemsPath, lp));

        ByteArrayOutputStream[] streams = run(new FileInputStream(dataPath));

        //Seen data
        ByteArrayInputStream seenStream = new ByteArrayInputStream(streams[0].toByteArray());
        FastPreferenceData<Long, Long> seenData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(seenStream, lp, lp), userIndex, itemIndex);

        //No seen data
        ByteArrayInputStream noSeenStream = new ByteArrayInputStream(streams[1].toByteArray());
        FastPreferenceData<Long, Long> noSeenData = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(noSeenStream, lp, lp), userIndex, itemIndex);

        return new FastPreferenceData[]{seenData, noSeenData};
    }

    /**
     * 
     * @param inputFile
     * @return 
     */
    public static ByteArrayOutputStream[] run(InputStream inputFile) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile));

        ByteArrayOutputStream seenStream = new ByteArrayOutputStream();
        PrintStream seenData = new PrintStream(seenStream);

        ByteArrayOutputStream noSeenStream = new ByteArrayOutputStream();
        PrintStream noSeenData = new PrintStream(noSeenStream);

        reader.lines().forEach(l -> {
            String[] data = l.split("\t");
            if (Integer.valueOf(data[3]) == 1) {
                seenData.println(l);
            } else {
                noSeenData.println(l);
            }
        });

        seenData.close();
        noSeenData.close();

        return new ByteArrayOutputStream[]{seenStream, noSeenStream};
    }

}
