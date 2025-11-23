/*
 * Decompiled with CFR 0.152.
 */
package io.github.douira.glsl_transformer.ast.data;

import io.github.douira.glsl_transformer.util.LRUCache;
import java.util.function.Function;
import java.util.function.Supplier;
import oculus.org.antlr.v4.runtime.ParserRuleContext;

public class TypedTreeCache<V>
extends LRUCache<CacheKey, V> {
    private static final int defaultCacheSize = 400;

    public TypedTreeCache(int maxSize, float loadFactor) {
        super(maxSize, loadFactor);
    }

    public TypedTreeCache(int maxSize) {
        super(maxSize);
    }

    public TypedTreeCache() {
        super(400);
    }

    public V cachedGet(String str, Class<? extends ParserRuleContext> ruleType, Supplier<V> supplier) {
        return super.cachedGet(new CacheKey(str, ruleType), supplier);
    }

    public V cachedGetHydrateHit(String str, Class<? extends ParserRuleContext> ruleType, Supplier<V> supplier, Function<V, V> hydrator) {
        return super.cachedGetHydrateHit(new CacheKey(str, ruleType), supplier, hydrator);
    }

    public static class CacheKey {
        final String input;
        final Class<? extends ParserRuleContext> ruleType;

        public CacheKey(String input, Class<? extends ParserRuleContext> ruleType) {
            this.input = input;
            this.ruleType = ruleType;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.input == null ? 0 : this.input.hashCode());
            result = 31 * result + (this.ruleType == null ? 0 : this.ruleType.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey)obj;
            if (this.input == null ? other.input != null : !this.input.equals(other.input)) {
                return false;
            }
            return !(this.ruleType == null ? other.ruleType != null : !this.ruleType.equals(other.ruleType));
        }
    }
}

