package com.espmod.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    // Logic moved to EspModClient using WorldRenderEvents
}
