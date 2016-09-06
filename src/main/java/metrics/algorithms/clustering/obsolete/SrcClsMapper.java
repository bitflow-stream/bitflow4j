package metrics.algorithms.clustering.obsolete;

import metrics.algorithms.clustering.ClusterConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class should be used by all classes working on evaluation. The correct expected prediction for each class should be trained / configured here once and used by all classes.
 */
public class SrcClsMapper {

    private static Object synchronizationLock = new Object();
    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    private static Map<String, String> srcClsMapping = null;
    private static volatile boolean original = false;

    static {
        //initialize with default values
        srcClsMapping = new HashMap<>();
        srcClsMapping.put("idle", "idle");
        srcClsMapping.put("load", "idle");
//        srcClsMapping.put(ClusterConstants.UNKNOWN_LABEL, ClusterConstants.UNKNOWN_LABEL);
    }

    public static void useOriginal(boolean useOriginal){
        original = useOriginal;
    }

    /**
     * This method returns the correct expected predition for the given src-label
     * @param srcLabel the original source label of the sample
     */
    public static String getCorrectPrediction(String srcLabel) throws IllegalArgumentException{
//        if (srcLabel == null) throw new IllegalArgumentException("label must not be null");
//        @throws @link{IllegalArgumentException} if null label is provided
        if(original) return srcLabel;
        if (srcLabel == null) {
            // return ClusterConstants.UNKNOWN_LABEL;
            return "unknown";
        }
        lock.readLock().lock();
        String correctLabel;
        try{
            correctLabel = srcClsMapping.get(srcLabel);
        }finally{
            lock.readLock().unlock();
        }
        if(correctLabel == null){
            correctLabel = ClusterConstants.NOISE_CLUSTER;
        }
        return correctLabel;
    }

    /**
     * Adds a single src-to-cls mapping. Be careful, as this method will silently replace old mappings.
     * @param src the src-label
     * @param cls the cls-label to predict
     * @throws IllegalArgumentException If a parameter is null
     */
    public static void addMapping(String src, String cls) throws IllegalArgumentException{
        if(src == null || cls == null) throw new IllegalArgumentException("Null arguments provided");
        lock.writeLock().lock();
        try{
            srcClsMapping.put(src, cls);
        }finally{
            lock.writeLock().unlock();
        }
    }

    /**
     * This method can be used to set the source-to-class mapping.
     * @param mapping A map, the key is a class-label and the value the corresponding expected prediction
     * @throws IllegalArgumentException If srcClsMapping is empty
     */
    public static void setMode(Map<String, String> mapping) throws IllegalArgumentException{
        if(mapping == null || mapping.isEmpty()) throw new IllegalArgumentException("empty mapping is not allowed");
        lock.writeLock().lock();
        try{
            srcClsMapping = mapping;
        }finally{
            lock.writeLock().unlock();
        }

    }

    /**
     * Returns a copy of the current mapping.
     * @return a copy of the current mapping.
     */
    public static Map<String, String> getMapping(){
        Map<String, String> result = null;
        lock.readLock().lock();
        try {
            result = new HashMap<>(srcClsMapping);
        } finally{
            lock.readLock().unlock();
        }
        return result;
    }
}
