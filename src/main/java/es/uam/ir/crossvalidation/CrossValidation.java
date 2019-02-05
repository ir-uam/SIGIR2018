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
package es.uam.ir.crossvalidation;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Random;
import static org.ranksys.formats.parsing.Parsers.lp;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;

/**
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 */
public class CrossValidation {

    private static final Random rnd = new Random();

    /**
     * 
     * @param dataPath
     * @param nfolds
     * @return
     * @throws IOException 
     */
    public static ByteArrayOutputStream[] crowssValidation(String dataPath, int nfolds) throws IOException {
        return crowssValidation(new FileInputStream(dataPath), nfolds);
    }


    /**
     * 
     * @param in
     * @param nfolds
     * @return
     * @throws IOException 
     */
    public static ByteArrayOutputStream[] crowssValidation(InputStream in, int nfolds) throws IOException {

        ByteArrayOutputStream[] foldOutputStream = new ByteArrayOutputStream[nfolds];
        PrintStream foldPrint[] = new PrintStream[nfolds];

        for (int i = 0; i < nfolds; i++) {
            foldOutputStream[i] = new ByteArrayOutputStream();
            foldPrint[i] = new PrintStream(foldOutputStream[i]);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(l -> {
                foldPrint[rnd.nextInt(nfolds)].println(l);
            });
        }
        for (int i = 0; i < nfolds; i++) {
            foldPrint[i].close();
        }

        return foldOutputStream;
    }

    /**
     * 
     * @param data
     * @param nfolds
     * @return
     * @throws IOException 
     */
    public static FastPreferenceData<Long, Long>[] crowssValidation(FastPreferenceData<Long, Long> data, int nfolds) throws IOException {

        ByteArrayOutputStream[] foldOutputStream = new ByteArrayOutputStream[nfolds];
        PrintStream foldPrint[] = new PrintStream[nfolds];

        for (int i = 0; i < nfolds; i++) {
            foldOutputStream[i] = new ByteArrayOutputStream();
            foldPrint[i] = new PrintStream(foldOutputStream[i]);
        }

        data.getUsersWithPreferences().forEachOrdered(user -> {
            data.getUserPreferences(user).forEachOrdered(up -> foldPrint[rnd.nextInt(nfolds)].println(user + "\t" + up.v1 + "\t" + up.v2));
        });

        for (int i = 0; i < nfolds; i++) {
            foldPrint[i].close();
        }

        FastPreferenceData[] dataFolds = new FastPreferenceData[nfolds];
        for (int i = 0; i < nfolds; i++) {
            ByteArrayInputStream stream = new ByteArrayInputStream(foldOutputStream[i].toByteArray());
            dataFolds[i] = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(stream, lp, lp), data, data);
        }

        return dataFolds;
    }
}
