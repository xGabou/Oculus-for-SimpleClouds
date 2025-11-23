/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 */
package net.irisshaders.iris.shaderpack.option;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

public class OrderBackedProperties
extends Properties {
    private final transient Map<Object, Object> backing = Object2ObjectMaps.synchronize((Object2ObjectMap)new Object2ObjectLinkedOpenHashMap());

    @Override
    public synchronized Object put(Object key, Object value) {
        this.backing.put(key, value);
        return super.put(key, value);
    }

    @Override
    public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
        this.backing.forEach(action);
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        this.customStore0(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.ISO_8859_1)), comments, true);
    }

    @Override
    public void store(Writer out, String comments) throws IOException {
        this.customStore0(new BufferedWriter(out), comments, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void customStore0(BufferedWriter bw, String comments, boolean escUnicode) throws IOException {
        bw.write("#" + new Date());
        bw.newLine();
        OrderBackedProperties orderBackedProperties = this;
        synchronized (orderBackedProperties) {
            Enumeration e = this.keys();
            while (e.hasMoreElements()) {
                String key = (String)e.nextElement();
                String val = (String)this.get(key);
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
    }
}

