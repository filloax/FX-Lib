package com.filloax.fxlib.mixin.structuretrack;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldGenRegion.class)
public interface WorldGenRegionAccessor {
    @Accessor
    ServerLevel getLevel();
}
