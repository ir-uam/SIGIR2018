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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
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
public class SampleItemIndependent {

    
    private static Random rnd = new Random();

    /**
     * 
     * @param path
     * @param threshold
     * @return
     * @throws IOException 
     */
    public static FastPreferenceData[] run(String path, double threshold) throws IOException {
        String dataPath = path + "data.txt";
        String usersPath = path + "users.txt";
        String itemsPath = path + "items.txt";

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(usersPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemsPath, lp));

        ByteArrayOutputStream[] streams = run(new FileInputStream(dataPath), threshold);

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
     * @param dataInput
     * @param threshold
     * @return 
     */
    public static ByteArrayOutputStream[] run(InputStream dataInput, double threshold) {
        HashMap<Long, HashMap<Long, Integer>> map = new HashMap<>();
        Scanner scn = new Scanner(dataInput);

        Set<Long> items = new HashSet<>();
        Set<Long> users = new HashSet<>();

        int nRelRatings = 0, nSeenRelRatings = 0, nSeenNoRelRatings = 0;
        while (scn.hasNext()) {
            String tokens[] = scn.nextLine().split("\t");
            long user = new Integer(tokens[0]);
            long item = new Integer(tokens[1]);
            int rating = Integer.valueOf(tokens[2]);
            int known = Integer.valueOf(tokens[3]);

            int rel = rating >= threshold ? 1 : 0;
            nRelRatings += rel;
            nSeenRelRatings += known * rel;
            nSeenNoRelRatings += known * (1 - rel);

            items.add(item);
            users.add(user);
            users.add(user);
            if (!map.containsKey(user)) {
                map.put(user, new HashMap<>());
            }
            map.get(user).put(item, rating);
        }

        int nUsers = users.size();
        int nItems = items.size();
        double pSeenGivenRel = nSeenRelRatings * 1.0 / nRelRatings;
        double pSeenGivenNoRel = nSeenNoRelRatings * 1.0 / (nUsers * nItems - nRelRatings);

        ByteArrayOutputStream seenStream = new ByteArrayOutputStream();
        PrintStream seenData = new PrintStream(seenStream);

        ByteArrayOutputStream noSeenStream = new ByteArrayOutputStream();
        PrintStream noSeenData = new PrintStream(noSeenStream);

        map.forEach((user, ratings) -> {
            ratings.forEach((item, rating) -> {
                if ((rating >= threshold && rnd.nextDouble() < pSeenGivenRel) || (rating < threshold && rnd.nextDouble() < pSeenGivenNoRel)) {
                    seenData.println(user + "\t" + item + "\t" + rating + "\t1");
                } else {
                    noSeenData.println(user + "\t" + item + "\t" + rating + "\t0");
                }
            });
            //No ratings
            for (long item : items) {
                if (!ratings.containsKey(item) && rnd.nextDouble() < pSeenGivenNoRel) {
                    seenData.println(user + "\t" + item + "\t0\t1");
                }
            }
        });
        seenData.close();
        noSeenData.close();

        return new ByteArrayOutputStream[]{seenStream, noSeenStream};
    }

}
