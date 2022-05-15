package com.github.theredbrain.dirtyThings.client;

import com.github.theredbrain.dirtyThings.DirtyThings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;

public class DirtyThingsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerColors();
    }

    private void registerColors() {
        ColorProviderRegistry.BLOCK.register(((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D)), DirtyThings.GRASS_SLAB);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5D, 1.0D), DirtyThings.GRASS_SLAB);
        BlockRenderLayerMap.INSTANCE.putBlock(DirtyThings.GRASS_SLAB, RenderLayer.getCutoutMipped());
    }
}
