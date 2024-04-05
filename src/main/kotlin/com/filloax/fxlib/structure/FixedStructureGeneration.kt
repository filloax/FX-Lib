package com.filloax.fxlib.structure

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.levelgen.structure.Structure


/**
 * Allows registering structures to spawn at a fixed location, rotation, etc.,
 * to be extra sure register them on server started
 */
interface FixedStructureGeneration {
    companion object {
        fun get(): FixedStructureGeneration = FixedStructureGenerationImpl
    }

    /**
     * Register a structure to be spawned at the specified pos.
     *
     * For best behavior, do this at server start.
     */
    fun register(
        level: ServerLevel,
        id: String, pos: BlockPos,
        structureId: ResourceLocation,
        rotation: Rotation = Rotation.NONE,
        force: Boolean = false,
    )

    /**
     * Register a structure to be spawned at the specified pos.
     *
     * For best behavior, do this at server start.
     */
    fun register(
        level: ServerLevel,
        id: String, pos: BlockPos,
        structureKey: ResourceKey<Structure>,
        rotation: Rotation = Rotation.NONE,
        force: Boolean = false,
    ) = register(level, id, pos, structureKey.location(), rotation, force)

    /**
     * Register a structure to be spawned at the specified pos.
     *
     * For best behavior, do this at server start.
     */
    fun register(
        level: ServerLevel,
        id: String, pos: BlockPos,
        structure: Holder<Structure>,
        rotation: Rotation = Rotation.NONE,
        force: Boolean = false,
    ) = register(level, id, pos, structure.unwrapKey().orElseThrow(), rotation, force)

    /**
     * Returns true/false if structure is queued and was/was not spawned,
     * null if not in queue
     */
    fun spawnedQueuedStructure(structureSpawnId: String): Boolean?
}