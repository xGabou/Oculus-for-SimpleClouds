/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap
 *  it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 */
package de.odysseus.ithaka.digraph;

import de.odysseus.ithaka.digraph.Digraph;
import de.odysseus.ithaka.digraph.DigraphFactory;
import de.odysseus.ithaka.digraph.Digraphs;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;

public class MapDigraph<V>
implements Digraph<V> {
    private static final int INVALID_WEIGHT = Integer.MIN_VALUE;
    private final VertexMapFactory<V> vertexMapFactory;
    private final EdgeMapFactory<V> edgeMapFactory;
    private final Map<V, Object2IntMap<V>> vertexMap;
    private int edgeCount;

    public MapDigraph() {
        this(null);
    }

    public MapDigraph(Comparator<? super V> comparator) {
        this(comparator, comparator);
    }

    public MapDigraph(Comparator<? super V> vertexComparator, Comparator<? super V> edgeComparator) {
        this(MapDigraph.getDefaultVertexMapFactory(vertexComparator), MapDigraph.getDefaultEdgeMapFactory(edgeComparator));
    }

    public MapDigraph(VertexMapFactory<V> vertexMapFactory, EdgeMapFactory<V> edgeMapFactory) {
        this.vertexMapFactory = vertexMapFactory;
        this.edgeMapFactory = edgeMapFactory;
        this.vertexMap = vertexMapFactory.create();
    }

    public static <V> DigraphFactory<MapDigraph<V>> getDefaultDigraphFactory() {
        return MapDigraph.getMapDigraphFactory(MapDigraph.getDefaultVertexMapFactory(null), MapDigraph.getDefaultEdgeMapFactory(null));
    }

    public static <V> DigraphFactory<MapDigraph<V>> getMapDigraphFactory(VertexMapFactory<V> vertexMapFactory, EdgeMapFactory<V> edgeMapFactory) {
        return () -> new MapDigraph(vertexMapFactory, edgeMapFactory);
    }

    private static <V> VertexMapFactory<V> getDefaultVertexMapFactory(final Comparator<? super V> comparator) {
        return new VertexMapFactory<V>(){

            @Override
            public Map<V, Object2IntMap<V>> create() {
                if (comparator == null) {
                    return new LinkedHashMap(16);
                }
                return new TreeMap(comparator);
            }
        };
    }

    private static <V> EdgeMapFactory<V> getDefaultEdgeMapFactory(final Comparator<? super V> comparator) {
        return new EdgeMapFactory<V>(){

            @Override
            public Object2IntMap<V> create(V ignore) {
                Object map = comparator == null ? new Object2IntLinkedOpenHashMap(16) : new Object2IntAVLTreeMap(comparator);
                map.defaultReturnValue(Integer.MIN_VALUE);
                return map;
            }
        };
    }

    private static <V> Object2IntMap<V> createEmptyMap() {
        return Object2IntMaps.emptyMap();
    }

    @Override
    public boolean add(V vertex) {
        if (!this.vertexMap.containsKey(vertex)) {
            this.vertexMap.put((Object2IntMap<V>)vertex, (Object2IntMap<Object2IntMap<V>>)MapDigraph.createEmptyMap());
            return true;
        }
        return false;
    }

    @Override
    public OptionalInt put(V source, V target, int weight) {
        OptionalInt previous;
        int previousInt;
        if (weight == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Invalid weight " + weight);
        }
        Object2IntMap<V> edgeMap = this.vertexMap.get(source);
        if (edgeMap == null || edgeMap.isEmpty()) {
            edgeMap = this.edgeMapFactory.create(source);
            this.vertexMap.put((Object2IntMap<V>)source, (Object2IntMap<Object2IntMap<V>>)edgeMap);
        }
        if ((previousInt = edgeMap.put(target, weight)) != Integer.MIN_VALUE) {
            previous = OptionalInt.of(previousInt);
        } else {
            previous = OptionalInt.empty();
            this.add(target);
            ++this.edgeCount;
        }
        return previous;
    }

    @Override
    public OptionalInt get(V source, V target) {
        Object2IntMap<V> edgeMap = this.vertexMap.get(source);
        if (edgeMap == null || edgeMap.isEmpty()) {
            return OptionalInt.empty();
        }
        int result = edgeMap.getInt(target);
        return result == Integer.MIN_VALUE ? OptionalInt.empty() : OptionalInt.of(result);
    }

    @Override
    public OptionalInt remove(V source, V target) {
        Object2IntMap<V> edgeMap = this.vertexMap.get(source);
        if (edgeMap == null || !edgeMap.containsKey(target)) {
            return OptionalInt.empty();
        }
        int result = edgeMap.removeInt(target);
        --this.edgeCount;
        if (edgeMap.isEmpty()) {
            this.vertexMap.put((Object2IntMap<V>)source, (Object2IntMap<Object2IntMap<V>>)MapDigraph.createEmptyMap());
        }
        return result == Integer.MIN_VALUE ? OptionalInt.empty() : OptionalInt.of(result);
    }

    @Override
    public boolean remove(V vertex) {
        Object2IntMap<V> edgeMap = this.vertexMap.get(vertex);
        if (edgeMap == null) {
            return false;
        }
        this.edgeCount -= edgeMap.size();
        this.vertexMap.remove(vertex);
        for (V source : this.vertexMap.keySet()) {
            this.remove(source, vertex);
        }
        return true;
    }

    @Override
    public void removeAll(Collection<V> vertices) {
        Object2IntMap<V> edgeMap;
        for (V vertex : vertices) {
            edgeMap = this.vertexMap.get(vertex);
            if (edgeMap == null) continue;
            this.edgeCount -= edgeMap.size();
            this.vertexMap.remove(vertex);
        }
        for (V source : this.vertexMap.keySet()) {
            edgeMap = this.vertexMap.get(source);
            ObjectIterator iterator = edgeMap.keySet().iterator();
            while (iterator.hasNext()) {
                if (!vertices.contains(iterator.next())) continue;
                iterator.remove();
                --this.edgeCount;
            }
            if (!edgeMap.isEmpty()) continue;
            this.vertexMap.put((Object2IntMap<V>)source, (Object2IntMap<Object2IntMap<V>>)MapDigraph.createEmptyMap());
        }
    }

    @Override
    public boolean contains(V source, V target) {
        Object2IntMap<V> edgeMap = this.vertexMap.get(source);
        if (edgeMap == null || edgeMap.isEmpty()) {
            return false;
        }
        return edgeMap.containsKey(target);
    }

    @Override
    public boolean contains(V vertex) {
        return this.vertexMap.containsKey(vertex);
    }

    @Override
    public Iterable<V> vertices() {
        if (this.vertexMap.isEmpty()) {
            return Collections.emptySet();
        }
        return new Iterable<V>(){

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    private final Iterator<V> delegate;
                    V vertex;
                    {
                        this.delegate = MapDigraph.this.vertexMap.keySet().iterator();
                        this.vertex = null;
                    }

                    @Override
                    public boolean hasNext() {
                        return this.delegate.hasNext();
                    }

                    @Override
                    public V next() {
                        this.vertex = this.delegate.next();
                        return this.vertex;
                    }

                    @Override
                    public void remove() {
                        Object2IntMap edgeMap = MapDigraph.this.vertexMap.get(this.vertex);
                        this.delegate.remove();
                        MapDigraph.this.edgeCount -= edgeMap.size();
                        for (Object source : MapDigraph.this.vertexMap.keySet()) {
                            MapDigraph.this.remove(source, this.vertex);
                        }
                    }
                };
            }

            public String toString() {
                return MapDigraph.this.vertexMap.keySet().toString();
            }
        };
    }

    @Override
    public Iterable<V> targets(final V source) {
        final Object2IntMap<V> edgeMap = this.vertexMap.get(source);
        if (edgeMap == null || edgeMap.isEmpty()) {
            return Collections.emptySet();
        }
        return new Iterable<V>(){

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    private final Iterator<V> delegate;
                    {
                        this.delegate = edgeMap.keySet().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.delegate.hasNext();
                    }

                    @Override
                    public V next() {
                        return this.delegate.next();
                    }

                    @Override
                    public void remove() {
                        this.delegate.remove();
                        --MapDigraph.this.edgeCount;
                        if (edgeMap.isEmpty()) {
                            MapDigraph.this.vertexMap.put(source, MapDigraph.createEmptyMap());
                        }
                    }
                };
            }

            public String toString() {
                return edgeMap.keySet().toString();
            }
        };
    }

    @Override
    public int getVertexCount() {
        return this.vertexMap.size();
    }

    @Override
    public int totalWeight() {
        int weight = 0;
        for (V source : this.vertices()) {
            for (V target : this.targets(source)) {
                weight += this.get(source, target).getAsInt();
            }
        }
        return weight;
    }

    @Override
    public int getOutDegree(V vertex) {
        Object2IntMap<V> edgeMap = this.vertexMap.get(vertex);
        if (edgeMap == null) {
            return 0;
        }
        return edgeMap.size();
    }

    @Override
    public int getEdgeCount() {
        return this.edgeCount;
    }

    public DigraphFactory<? extends MapDigraph<V>> getDigraphFactory() {
        return () -> new MapDigraph<V>(this.vertexMapFactory, this.edgeMapFactory);
    }

    @Override
    public MapDigraph<V> reverse() {
        return Digraphs.reverse(this, this.getDigraphFactory());
    }

    @Override
    public MapDigraph<V> subgraph(Set<V> vertices) {
        return Digraphs.subgraph(this, vertices, this.getDigraphFactory());
    }

    @Override
    public boolean isAcyclic() {
        return Digraphs.isAcyclic(this);
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(this.getClass().getName().substring(this.getClass().getName().lastIndexOf(46) + 1));
        b.append("(");
        Iterator<V> vertices = this.vertices().iterator();
        while (vertices.hasNext()) {
            V v = vertices.next();
            b.append(v);
            b.append(this.targets(v));
            if (!vertices.hasNext()) continue;
            b.append(", ");
            if (b.length() <= 1000) continue;
            b.append("...");
            break;
        }
        b.append(")");
        return b.toString();
    }

    public static interface VertexMapFactory<V> {
        public Map<V, Object2IntMap<V>> create();
    }

    public static interface EdgeMapFactory<V> {
        public Object2IntMap<V> create(V var1);
    }
}

