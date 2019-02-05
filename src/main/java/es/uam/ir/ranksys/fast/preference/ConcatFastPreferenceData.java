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

package es.uam.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.IdxPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.ranksys.core.util.iterators.StreamDoubleIterator;
import org.ranksys.core.util.iterators.StreamIntIterator;

/**
 * Concatenation of two FastPreferenceData's
 *
 * @author Pablo Castells
 * @author Rocío Cañamares
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class ConcatFastPreferenceData<U, I> implements FastPreferenceData<U, I> {

    private final FastPreferenceData<U, I> d1;
    private final FastPreferenceData<U, I> d2;

    /**
     * Constructor.
     *
     * @param d1 recommender data
     * @param d2 recommender data
     */
    public ConcatFastPreferenceData(FastPreferenceData<U, I> d1, FastPreferenceData<U, I> d2) {
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public boolean containsUser(U u) {
        return d1.containsUser(u) || d2.containsUser(u);
    }

    @Override
    public int numUsers() {
        return (int) getAllUsers().count();
    }

    @Override
    public int numUsers(I i) {
        return d1.numUsers(i) + d2.numUsers(i);
    }

    @Override
    public boolean containsItem(I i) {
        return d1.containsItem(i) || d2.containsItem(i);
    }

    @Override
    public int numItems() {
        return (int) getAllItems().count();
    }

    @Override
    public int numItems(U u) {
        return d1.numItems(u) + d2.numItems(u);
    }

    @Override
    public int numPreferences() {
        return d1.numPreferences() + d2.numPreferences();
    }

    @Override
    public Stream<U> getAllUsers() {
        return Stream.concat(d1.getAllUsers(), d2.getAllUsers()).distinct();
    }

    @Override
    public Stream<I> getAllItems() {
        return Stream.concat(d1.getAllItems(), d2.getAllItems()).distinct();
    }

    @Override
    public int numUsersWithPreferences() {
        return (int) getUsersWithPreferences().count();
    }

    @Override
    public int numItemsWithPreferences() {
        return (int) getItemsWithPreferences().count();
    }

    @Override
    public int numUsers(int iidx) {
        return d1.numUsers(iidx) + d2.numUsers(iidx);
    }

    @Override
    public int numItems(int uidx) {
        return d1.numItems(uidx) + d2.numItems(uidx);
    }

    @Override
    public Stream<U> getUsersWithPreferences() {
        return Stream.concat(d1.getUsersWithPreferences(), d2.getUsersWithPreferences()).distinct();
    }

    @Override
    public Stream<I> getItemsWithPreferences() {
        return Stream.concat(d1.getItemsWithPreferences(), d2.getItemsWithPreferences()).distinct();
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return IntStream.concat(d1.getUidxWithPreferences(), d2.getUidxWithPreferences()).distinct();
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return IntStream.concat(d1.getIidxWithPreferences(), d2.getIidxWithPreferences()).distinct();
    }

    @Override
    public Stream<? extends IdxPref> getUidxPreferences(int uidx) {
        return Stream.concat(d1.getUidxPreferences(uidx), d2.getUidxPreferences(uidx));
    }

    @Override
    public Stream<? extends IdxPref> getIidxPreferences(int iidx) {
        return Stream.concat(d1.getIidxPreferences(iidx), d2.getIidxPreferences(iidx));
    }

    @Override
    public IntIterator getUidxIidxs(int uidx) {
        return new StreamIntIterator(getUidxPreferences(uidx).mapToInt(pref -> pref.v1));
    }

    @Override
    public DoubleIterator getUidxVs(int uidx) {
        return new StreamDoubleIterator(getUidxPreferences(uidx).mapToDouble(pref -> pref.v2));
    }

    @Override
    public IntIterator getIidxUidxs(int iidx) {
        return new StreamIntIterator(getIidxPreferences(iidx).mapToInt(pref -> pref.v1));
    }

    @Override
    public DoubleIterator getIidxVs(int iidx) {
        return new StreamDoubleIterator(getIidxPreferences(iidx).mapToDouble(pref -> pref.v2));
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return false;
    }

    @Override
    public Stream<? extends IdPref<I>> getUserPreferences(U u) {
        return Stream.concat(d1.getUserPreferences(u), d2.getUserPreferences(u));
    }

    @Override
    public Stream<? extends IdPref<U>> getItemPreferences(I i) {
        return Stream.concat(d1.getItemPreferences(i), d2.getItemPreferences(i));
    }

    @Override
    public int user2uidx(U u) {
        return d1.user2uidx(u);
    }

    @Override
    public U uidx2user(int uidx) {
        return d1.uidx2user(uidx);
    }

    @Override
    public int item2iidx(I i) {
        return d1.item2iidx(i);
    }

    @Override
    public I iidx2item(int iidx) {
        return d1.iidx2item(iidx);
    }

}
