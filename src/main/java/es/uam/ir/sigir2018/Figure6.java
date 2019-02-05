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

import es.uam.ir.crossvalidation.CrossValidation;
import es.uam.ir.datagenerator.MapNegativeRatingsTo0;
import es.uam.ir.datagenerator.SplitSeenAndNoSeen;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.SimpleFastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import es.uam.eps.ir.ranksys.metrics.RecommendationMetric;
import es.uam.eps.ir.ranksys.metrics.basic.NDCG;
import es.uam.eps.ir.ranksys.metrics.basic.Precision;
import es.uam.eps.ir.ranksys.metrics.rel.BinaryRelevanceModel;
import es.uam.eps.ir.ranksys.nn.user.UserNeighborhoodRecommender;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.TopKUserNeighborhood;
import es.uam.eps.ir.ranksys.nn.user.neighborhood.UserNeighborhood;
import es.uam.eps.ir.ranksys.nn.user.sim.UserSimilarity;
import es.uam.eps.ir.ranksys.nn.user.sim.VectorCosineUserSimilarity;
import es.uam.eps.ir.ranksys.rec.Recommender;
import es.uam.eps.ir.ranksys.rec.runner.Filters;
import static org.ranksys.formats.parsing.Parsers.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.ranksys.formats.index.ItemsReader;
import org.ranksys.formats.index.UsersReader;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;
import es.uam.ir.ranksys.fast.preference.ConcatFastPreferenceData;
import es.uam.ir.ranksys.nn.user.NormUserNeighborhoodRecommenderWithMinimum;
import es.uam.ir.util.Timer;

/**
 * Method to generate figure 6 of the paper:
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
public class Figure6 {

    private static final int NFOLDS = 5;
    private static final int NREPS = 10;
    private static final boolean DEBUG = false;
    

    /**
     * Method to generate figure 6 of the paper:
     *
     * R. Cañamares, P. Castells. Should I Follow the Crowd? A Probabilistic
     * Analysis of the Effectiveness of Popularity in Recommender Systems. 41st
     * Annual International ACM SIGIR Conference on Research and Development in
     * Information Retrieval (SIGIR 2018). Ann Arbor, Michigan, USA, July 2018,
     * pp. 415-424
     *
     * @param out
     * @throws IOException
     */
    public static void run (PrintStream out) throws IOException {
        Timer.start("\nStarting Figure 6 (this one will take just a few minutes)");
        String path = "./";
        String datasetsPath = path + "datasets/";
        String ml1mPath = datasetsPath + "ml1m/";
        String cm100kPath = datasetsPath + "cm100k/";
        out.println("\n------------------ Figure 6 ------------------\n");

        /**
         * --------------------------- MovieLens 1M ---------------------------
         */
        double thresholdML1M = 4;
        System.out.println("  Running kNN on MovieLens 1M... ");
        Map<String, Map<String, Double>> resultsMovieLens1M = processObservedInformation(ml1mPath, thresholdML1M);
        out.println("MovieLens 1M");
        printObservedResults(out, resultsMovieLens1M, "");

        /**
         * --------------------- Crowdsourced 100k dataset ---------------------
         */
        double thresholdCM100k = 3;
        System.out.println("  Running kNN on Crowdsourced 100k dataset... ");

        FastPreferenceData<Long, Long>[] actualDiscoveryData = SplitSeenAndNoSeen.run(cm100kPath);

        Map<String, Map<String, Double>> resultsCM100KActualDiscovery = processObservedAndNonObservedInformation(cm100kPath, thresholdCM100k, actualDiscoveryData[0], actualDiscoveryData[1]);
        for (int rep = 1; rep < NREPS; rep++) {
            Map<String, Map<String, Double>> results = processObservedAndNonObservedInformation(cm100kPath, thresholdCM100k, actualDiscoveryData[0], actualDiscoveryData[1]);
            resultsCM100KActualDiscovery.forEach((recName, values) -> {
                values.replaceAll((metric, value) -> value + results.get(recName).get(metric));
            });
        }
        resultsCM100KActualDiscovery.forEach((recName, values) -> values.replaceAll((metric, value) -> value / NREPS));
        out.println("\nCrowdsourced 100k");
        printObservedAndTrueResults(out, resultsCM100KActualDiscovery, "");
        
    }

    private static Map<String, Map<String, Double>> processObservedInformation(String path, double threshold) throws IOException {

        String dataPath = path + "data.txt";
        String usersPath = path + "users.txt";
        String itemsPath = path + "items.txt";

        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(usersPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemsPath, lp));
        FastPreferenceData<Long, Long> data = SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(dataPath, lp, lp), userIndex, itemIndex);

        Map<String, Map<String, Double>> results = new HashMap<>();

        //Splitting
        printIfDebug("\tRunning cross validation ... ");
        FastPreferenceData[] folds = CrossValidation.crowssValidation(data, NFOLDS);
        timeIfDebug(" ");

        //For each fold
        for (int i = 0; i < NFOLDS; i++) {
            printIfDebug("\tRunning fold " + i + " :\n");

            //Data preparation
            printIfDebug("\t\tPreparing data ...");
            FastPreferenceData<Long, Long> trainData = null;
            FastPreferenceData<Long, Long> testData = folds[i];
            for (int j = 0; j < NFOLDS; j++) {
                if (i == j) {
                    continue;
                }
                if (trainData == null) {
                    trainData = folds[j];
                } else {
                    trainData = new ConcatFastPreferenceData<>(trainData, folds[j]);
                }
            }
            trainData = MapNegativeRatingsTo0.run(trainData, threshold);
            timeIfDebug(" ");

            //Recommenders
            printIfDebug("\t\tPreparing recommenders ...");
            Map<String, Recommender<Long, Long>> recMap = new HashMap<>();

            double alpha = 0.5;
            int q = 1;
            UserSimilarity<Long> sim = new VectorCosineUserSimilarity<>(trainData, alpha, true);

            int kNonNormalized = 80;
            UserNeighborhood<Long> neighborhoodNonNormalized = new TopKUserNeighborhood<>(sim, kNonNormalized);

            int kNormalized = 10;
            int min = 3;
            UserNeighborhood<Long> neighborhoodNormalized = new TopKUserNeighborhood<>(sim, kNormalized);

            recMap.put("Non-normalized user-based kNN", new UserNeighborhoodRecommender<>(trainData, neighborhoodNonNormalized, q));
            recMap.put("Normalized user-based kNN", new NormUserNeighborhoodRecommenderWithMinimum<>(trainData, neighborhoodNormalized, q, min));

            timeIfDebug(" ");

            //Metrics
            printIfDebug("\t\tPreparing metrics ...");
            Map<String, RecommendationMetric<Long, Long>> recMetrics = new HashMap<>();
            BinaryRelevanceModel<Long, Long> binRel = new BinaryRelevanceModel<>(false, testData, threshold);
            recMetrics.put("Observed P@1", new Precision<>(1, binRel));
            recMetrics.put("Observed nDCG@10", new NDCG<>(10, new NDCG.NDCGRelevanceModel<>(false, testData, threshold)));
            timeIfDebug(" ");

            //Recommendation & Evaluation
            Set<Long> targetUsers = trainData.getUsersWithPreferences().collect(Collectors.toSet());
            Function<Long, Predicate<Long>> userFilter = Filters.notInTrain(trainData);
            int maxLength = 10;
            recMap.forEach((recName, recommendation) -> {
                printIfDebug("\t\tRunning " + recName);

                if (!results.containsKey(recName)) {
                    results.put(recName, new HashMap<>());
                }

                targetUsers.stream().parallel()
                        .map(user -> recommendation.getRecommendation(user, maxLength, userFilter.apply(user)))
                        .forEachOrdered(rec -> {
                            recMetrics.forEach((metricName, metric) -> {
                                double value = results.get(recName).getOrDefault(metricName, 0.0) + metric.evaluate(rec) / targetUsers.size();
                                results.get(recName).put(metricName, value);
                            });
                        });
                timeIfDebug(" ");
            });
        }

        results.forEach((recName, values) -> values.replaceAll((metric, value) -> value / NFOLDS));

        return results;
    }

    private static Map<String, Map<String, Double>> processObservedAndNonObservedInformation(String path, double threshold, FastPreferenceData<Long, Long> seenData, FastPreferenceData<Long, Long> noSeenData) throws IOException {

        String usersPath = path + "users.txt";
        String itemsPath = path + "items.txt";

        printIfDebug("\tLoading files ... ");
        FastUserIndex<Long> userIndex = SimpleFastUserIndex.load(UsersReader.read(usersPath, lp));
        FastItemIndex<Long> itemIndex = SimpleFastItemIndex.load(ItemsReader.read(itemsPath, lp));
        FastPreferenceData<Long, Long> allData = new ConcatFastPreferenceData(seenData, noSeenData);
        timeIfDebug(" ");

        Map<String, Map<String, Double>> results = new HashMap<>();

        //Splitting
        printIfDebug("\tRunning cross validation ... ");
        FastPreferenceData[] folds = CrossValidation.crowssValidation(seenData, NFOLDS);
        timeIfDebug(" ");

        //For each fold
        for (int i = 0; i < NFOLDS; i++) {
            printIfDebug("\tRunning fold " + i + " :\n");

            //Data preparation
            printIfDebug("\t\tPreparing data ...");
            FastPreferenceData<Long, Long> trainData = null;
            FastPreferenceData<Long, Long> observedTestData = folds[i];
            FastPreferenceData<Long, Long> trueTestData = new ConcatFastPreferenceData(observedTestData, noSeenData);
            for (int j = 0; j < NFOLDS; j++) {
                if (i == j) {
                    continue;
                }
                if (trainData == null) {
                    trainData = folds[j];
                } else {
                    trainData = new ConcatFastPreferenceData<>(trainData, folds[j]);
                }
            }
            trainData = MapNegativeRatingsTo0.run(trainData, threshold);
            timeIfDebug(" ");

            //Recommenders
            printIfDebug("\t\tPreparing recommenders ...");
            Map<String, Recommender<Long, Long>> recMap = new HashMap<>();

            double alpha = 0.5;
            int q = 1;
            int k = userIndex.numUsers();
            int min = 3;
            UserSimilarity<Long> sim = new VectorCosineUserSimilarity<>(trainData, alpha, true);
            UserNeighborhood<Long> neighborhoodd = new TopKUserNeighborhood<>(sim, k);
            recMap.put("Non-normalized user-based kNN", new UserNeighborhoodRecommender<>(trainData, neighborhoodd, q));
            recMap.put("Normalized user-based kNN", new NormUserNeighborhoodRecommenderWithMinimum<>(trainData, neighborhoodd, q, min));
            timeIfDebug(" ");

            //Metrics
            printIfDebug("\t\tPreparing metrics ...");

            Map<String, RecommendationMetric<Long, Long>> recMetrics = new HashMap<>();

            BinaryRelevanceModel<Long, Long> observedBinRel = new BinaryRelevanceModel<>(false, observedTestData, threshold);
            recMetrics.put("Observed P@1", new Precision<>(1, observedBinRel));
            recMetrics.put("Observed nDCG@10", new NDCG<>(10, new NDCG.NDCGRelevanceModel<>(false, observedTestData, threshold)));

            BinaryRelevanceModel<Long, Long> trueBinRel = new BinaryRelevanceModel<>(false, trueTestData, threshold);
            recMetrics.put("True P@1", new Precision<>(1, trueBinRel));
            recMetrics.put("True nDCG@10", new NDCG<>(10, new NDCG.NDCGRelevanceModel<>(false, trueTestData, threshold)));

            timeIfDebug(" ");

            //Recommendation & Evaluation
            Set<Long> targetUsers = trainData.getUsersWithPreferences().collect(Collectors.toSet());
            Function<Long, Predicate<Long>> userFilter = Filters.notInTrain(trainData);
            int maxLength = 10;
            recMap.forEach((recName, recommendation) -> {
                printIfDebug("\t\tRunning " + recName);

                if (!results.containsKey(recName)) {
                    results.put(recName, new HashMap<>());
                }

                targetUsers.stream().parallel()
                        .map(user -> recommendation.getRecommendation(user, maxLength, userFilter.apply(user)))
                        .forEachOrdered(rec -> {
                            recMetrics.forEach((metricName, metric) -> {
                                double value = results.get(recName).getOrDefault(metricName, 0.0) + metric.evaluate(rec) / targetUsers.size();
                                results.get(recName).put(metricName, value);
                            });
                        });
                timeIfDebug(" ");
            });

        }

        results.forEach((recName, values) -> values.replaceAll((metric, value) -> value / NFOLDS));

        return results;
    }

    private static void printObservedResults(PrintStream out, Map<String, Map<String, Double>> results, String title) throws FileNotFoundException {
        String[] metrics = new String[]{"Observed P@1", "Observed nDCG@10"};
        String[] recs = new String[]{"Non-normalized user-based kNN", "Normalized user-based kNN"};

        printResults(out, results, title, metrics, recs);
    }

    private static void printObservedAndTrueResults(PrintStream out, Map<String, Map<String, Double>> results, String title) throws FileNotFoundException {
        String[] metrics = new String[]{"Observed P@1", "True P@1", "Observed nDCG@10", "True nDCG@10"};
        String[] recs = new String[]{"Non-normalized user-based kNN", "Normalized user-based kNN"};

        printResults(out, results, title, metrics, recs);
    }

    private static void printResults(PrintStream out, Map<String, Map<String, Double>> results, String title, String[] metrics, String[] recs) throws FileNotFoundException {

        out.println(title);
        //Header
        out.print("Recommender");
        for (String metric : metrics) {
            out.print("\t" + metric);
        }
        out.println();

        //Results
        for (String rec : recs) {
            Map<String, Double> recResults = results.get(rec);
            out.print(rec);
            for (String metric : metrics) {
                out.print("\t" + recResults.get(metric));
            }
            out.println();
        }
    }

    private static void printIfDebug(String txt) {
        if (DEBUG) {
            System.out.print(txt);
        }
    }

    private static void timeIfDebug(String txt) {
        if (DEBUG) {
            Timer.done(txt);
        }
    }

}
