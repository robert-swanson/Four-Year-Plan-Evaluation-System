package api;

import preferences.specification.FullSpecification;

import java.util.HashMap;
import java.util.TreeMap;

public class SpecificationCache {
//    private HashMap<SpecificationID, FullSpecification> cache;
    private final TreeMap<SpecificationID, FullSpecification> cache;
    private final HashMap<String, SpecificationID> versionCache;


    private final int MAX_SIZE = 10000;
    private final int EVICT_UNTIL = 5000;

    private static class AgedID {
        final SpecificationID id;
        final int age;
        static int newestLong = Integer.MIN_VALUE;

        public AgedID(SpecificationID id) {
            this.id = id;
            this.age = newestLong;
            newestLong++;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AgedID agedID = (AgedID) o;
            return age == agedID.age;
        }

        @Override
        public int hashCode() {
            return age;
        }
    }

    public SpecificationCache() {
        cache = new TreeMap<>();
        versionCache = new HashMap<>();
    }

    public FullSpecification get(SpecificationID id) {
        if (isCached(id)) {
            id.hits++;
            cache.put(id, cache.remove(id));
            return cache.get(id);
        } else {
            return null;
        }
    }


    public boolean isCached(SpecificationID id) {
        return cache.containsKey(id);
    }

    public void add(SpecificationID id, FullSpecification specification) {
        if (isCached(id)) return;

        if (versionCache.containsKey(id.identifier)) {
            SpecificationID oldVersion = versionCache.get(id.identifier);
            cache.remove(oldVersion);
        } else if (cache.size() >= MAX_SIZE) {
            evictOldestCacheItem();
        }
        versionCache.put(id.identifier, id);
        cache.put(id, specification);
    }


    private void evictOldestCacheItem() {
        while (cache.size() >= EVICT_UNTIL) {
            cache.remove(cache.firstKey());
        }
        cache.keySet().forEach(specificationID -> specificationID.hits=0);
    }
}
