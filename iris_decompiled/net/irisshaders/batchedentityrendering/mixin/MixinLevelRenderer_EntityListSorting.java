/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Slice
 */
package net.irisshaders.batchedentityrendering.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value={LevelRenderer.class}, priority=999)
public class MixinLevelRenderer_EntityListSorting {
    @Shadow
    private ClientLevel f_109465_;

    @ModifyVariable(method={"renderLevel"}, at=@At(value="INVOKE_ASSIGN", target="Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"), slice=@Slice(from=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"), to=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z")), allow=1)
    private Iterator<Entity> batchedentityrendering$sortEntityList(Iterator<Entity> iterator) {
        this.f_109465_.m_46473_().m_6180_("sortEntityList");
        HashMap sortedEntities = new HashMap();
        ArrayList entities = new ArrayList();
        iterator.forEachRemaining(entity -> sortedEntities.computeIfAbsent(entity.m_6095_(), entityType -> new ArrayList(32)).add(entity));
        sortedEntities.values().forEach(entities::addAll);
        this.f_109465_.m_46473_().m_7238_();
        return entities.iterator();
    }
}

