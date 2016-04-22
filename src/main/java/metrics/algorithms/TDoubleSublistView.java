package metrics.algorithms;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;

import java.util.Collection;
import java.util.Random;

/**
 * Created by anton on 4/22/16.
 */
public class TDoubleSublistView implements TDoubleList {

    private final TDoubleList list;
    private final int start;
    private final int end;

    public TDoubleSublistView(TDoubleList list, int start, int end) {
        this.list = list;
        this.start = start;
        this.end = end;
    }

    @Override
    public double getNoEntryValue() {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean add(double val) {
        return false;
    }

    @Override
    public void add(double[] vals) {

    }

    @Override
    public void add(double[] vals, int offset, int length) {

    }

    @Override
    public void insert(int offset, double value) {

    }

    @Override
    public void insert(int offset, double[] values) {

    }

    @Override
    public void insert(int offset, double[] values, int valOffset, int len) {

    }

    @Override
    public double get(int offset) {
        return 0;
    }

    @Override
    public double set(int offset, double val) {
        return 0;
    }

    @Override
    public void set(int offset, double[] values) {

    }

    @Override
    public void set(int offset, double[] values, int valOffset, int length) {

    }

    @Override
    public double replace(int offset, double val) {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean remove(double value) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean containsAll(TDoubleCollection collection) {
        return false;
    }

    @Override
    public boolean containsAll(double[] array) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        return false;
    }

    @Override
    public boolean addAll(TDoubleCollection collection) {
        return false;
    }

    @Override
    public boolean addAll(double[] array) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(TDoubleCollection collection) {
        return false;
    }

    @Override
    public boolean retainAll(double[] array) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean removeAll(TDoubleCollection collection) {
        return false;
    }

    @Override
    public boolean removeAll(double[] array) {
        return false;
    }

    @Override
    public double removeAt(int offset) {
        return 0;
    }

    @Override
    public void remove(int offset, int length) {

    }

    @Override
    public void transformValues(TDoubleFunction function) {

    }

    @Override
    public void reverse() {

    }

    @Override
    public void reverse(int from, int to) {

    }

    @Override
    public void shuffle(Random rand) {

    }

    @Override
    public TDoubleList subList(int begin, int end) {
        return null;
    }

    @Override
    public double[] toArray() {
        return new double[0];
    }

    @Override
    public double[] toArray(int offset, int len) {
        return new double[0];
    }

    @Override
    public double[] toArray(double[] dest) {
        return new double[0];
    }

    @Override
    public double[] toArray(double[] dest, int offset, int len) {
        return new double[0];
    }

    @Override
    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
        return new double[0];
    }

    @Override
    public boolean forEach(TDoubleProcedure procedure) {
        return false;
    }

    @Override
    public boolean forEachDescending(TDoubleProcedure procedure) {
        return false;
    }

    @Override
    public void sort() {

    }

    @Override
    public void sort(int fromIndex, int toIndex) {

    }

    @Override
    public void fill(double val) {

    }

    @Override
    public void fill(int fromIndex, int toIndex, double val) {

    }

    @Override
    public int binarySearch(double value) {
        return 0;
    }

    @Override
    public int binarySearch(double value, int fromIndex, int toIndex) {
        return 0;
    }

    @Override
    public int indexOf(double value) {
        return 0;
    }

    @Override
    public int indexOf(int offset, double value) {
        return 0;
    }

    @Override
    public int lastIndexOf(double value) {
        return 0;
    }

    @Override
    public int lastIndexOf(int offset, double value) {
        return 0;
    }

    @Override
    public boolean contains(double value) {
        return false;
    }

    @Override
    public TDoubleIterator iterator() {
        return null;
    }

    @Override
    public TDoubleList grep(TDoubleProcedure condition) {
        return null;
    }

    @Override
    public TDoubleList inverseGrep(TDoubleProcedure condition) {
        return null;
    }

    @Override
    public double max() {
        return 0;
    }

    @Override
    public double min() {
        return 0;
    }

    @Override
    public double sum() {
        return 0;
    }
}
