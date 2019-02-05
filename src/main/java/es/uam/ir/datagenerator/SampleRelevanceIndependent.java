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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class SampleRelevanceIndependent {


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
        Map<Integer, Map<Integer, Integer>> ratings = new HashMap<>();
        Set<Integer> users = new HashSet<>();
        Map<Integer, Integer> discovery = new HashMap<>();
        Map<Integer, Integer> relevance = new HashMap<>();

        Scanner scn = new Scanner(dataInput);
        while (scn.hasNext()) {
            String line[] = scn.nextLine().split("\t");
            int user = new Integer(line[0]);
            int item = new Integer(line[1]);
            users.add(user);
            if (!ratings.containsKey(item)) {
                ratings.put(item, new HashMap<>());
                discovery.put(item, 0);
                relevance.put(item, 0);
            }
            ratings.get(item).put(user, new Integer(line[2]));
            if (line[3].equals("1")) {
                discovery.put(item, discovery.get(item) + 1);
            }
            if (ratings.get(item).get(user) >= threshold) {
                relevance.put(item, relevance.get(item) + 1);
            }
        }
        // Generate biased discovery samples
        Map<Integer, Map<Integer, Integer>> itemBiasedSample = new HashMap<>();
        List<Integer> items = new ArrayList<>(ratings.keySet());
        Collections.shuffle(items);

        int i = 0;

        ByteArrayOutputStream seenStream = new ByteArrayOutputStream();
        PrintStream seenData = new PrintStream(seenStream);

        ByteArrayOutputStream noSeenStream = new ByteArrayOutputStream();
        PrintStream noSeenData = new PrintStream(noSeenStream);

        for (int item : ratings.keySet()) {
            int randomItem = items.get(i++);

            for (int user : users) {
                if (!itemBiasedSample.containsKey(user)) {
                    itemBiasedSample.put(user, new HashMap<>());
                }
                Integer rating = ratings.get(item).get(user);
                if (rnd.nextInt(ratings.get(randomItem).size()) < discovery.get(randomItem)) {
                    itemBiasedSample.get(user).put(item, rating == null ? 0 : rating);
                    seenData.println(user + "\t" + item + "\t" + (rating == null ? 0 : rating) + "\t1");
                } else if (rating != null) {
                    noSeenData.println(user + "\t" + item + "\t" + rating + "\t0");
                }
            }
        }

        seenData.close();
        noSeenData.close();

        return new ByteArrayOutputStream[]{seenStream, noSeenStream};
    }
}
