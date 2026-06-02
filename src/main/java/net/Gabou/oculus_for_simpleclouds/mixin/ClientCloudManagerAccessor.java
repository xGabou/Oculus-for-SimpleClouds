package net.Gabou.oculus_for_simpleclouds.mixin;

import dev.nonamecrackers2.simpleclouds.client.world.ClientCloudManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClientCloudManager.class, remap = false)
public interface ClientCloudManagerAccessor {

    @Accessor("receivedSync")
    void oculus_for_simpleclouds$setReceivedSync(boolean value);

    @Accessor("receivedSync")
    boolean oculus_for_simpleclouds$hasReceivedSync();
}
