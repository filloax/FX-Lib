package com.filloax.fxlib.structure

import com.filloax.fxlib.FXLibUtils.resLoc
import com.filloax.fxlib.structure.tracking.FixedStructurePlacement
import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType

object FXLibStructurePlacementTypes {
    val all = mutableMapOf<ResourceLocation, StructurePlacementType<*>>()

    var FIXED = register("fixed", FixedStructurePlacement.CODEC)

    private fun <SP : StructurePlacement?> register(name: String, codec: Codec<SP>): StructurePlacementType<SP> {
        val st = StructurePlacementType { codec }
        all[resLoc(name)] = st
        return st
    }

    fun registerAll(registry: Registry<StructurePlacementType<*>>) {
        all.forEach { Registry.register(registry, it.key, it.value) }
    }
}