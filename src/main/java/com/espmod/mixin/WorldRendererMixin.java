package com.espmod.mixin;

import com.espmod.EspModClient;
import com.espmod.EspRenderer;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void onRenderTail(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        while (EspModClient.toggleKey.wasPressed()) {
            EspModClient.espEnabled = !EspModClient.espEnabled;
            if (client.player != null) {
                client.player.sendMessage(
                    net.minecraft.text.Text.literal(
                        EspModClient.espEnabled ? "[ESP] ON" : "[ESP] OFF"
                    ).formatted(EspModClient.espEnabled ?
                        net.minecraft.util.Formatting.GREEN :
                        net.minecraft.util.Formatting.RED),
                    true
                );
            }
        }

        if (!EspModClient.espEnabled) return;
        if (client.player == null || client.world == null) return;

        ClientWorld world = client.world;
        Camera camera = client.gameRenderer.getCamera();

        BlockPos playerPos = client.player.getBlockPos();
        int renderDist = 8;

        for (int cx = -renderDist; cx <= renderDist; cx++) {
            for (int cz = -renderDist; cz <= renderDist; cz++) {
                int chunkX = (playerPos.getX() >> 4) + cx;
                int chunkZ = (playerPos.getZ() >> 4) + cz;
                WorldChunk chunk = world.getChunk(chunkX, chunkZ);
                if (chunk == null) continue;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    BlockPos pos = be.getPos();
                    float[] color = null;

                    if (be instanceof ChestBlockEntity chest) {
                        if (hasValuableItem(chest)) {
                            color = new float[]{1.0f, 0.84f, 0.0f}; // Gold
                        } else {
                            color = new float[]{0.0f, 1.0f, 1.0f}; // Cyan
                        }
                    } else if (be instanceof MobSpawnerBlockEntity) {
                        color = new float[]{1.0f, 0.0f, 0.0f}; // Red
                    } else if (be instanceof ShulkerBoxBlockEntity shulker) {
                        if (hasValuableItem(shulker)) {
                            color = new float[]{1.0f, 0.84f, 0.0f}; // Gold
                        } else {
                            color = new float[]{0.6f, 0.0f, 0.8f}; // Purple
                        }
                    }

                    if (color != null) {
                        EspRenderer.drawBox(camera, pos, color[0], color[1], color[2]);
                    }
                }
            }
        }
    }

    private boolean hasValuableItem(net.minecraft.inventory.Inventory inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;

            // Check by item ID - works for ALL items including enchanted ones
            String id = net.minecraft.registry.Registries.ITEM
                    .getId(stack.getItem()).toString();

            // Diamond armor + tools
            if (stack.isOf(Items.DIAMOND_HELMET) ||
                stack.isOf(Items.DIAMOND_CHESTPLATE) ||
                stack.isOf(Items.DIAMOND_LEGGINGS) ||
                stack.isOf(Items.DIAMOND_BOOTS) ||
                stack.isOf(Items.DIAMOND_SWORD) ||
                stack.isOf(Items.DIAMOND_PICKAXE) ||
                stack.isOf(Items.DIAMOND_AXE) ||
                stack.isOf(Items.DIAMOND_SHOVEL) ||
                stack.isOf(Items.DIAMOND_HOE) ||
                stack.isOf(Items.DIAMOND)) {
                return true;
            }

            // Netherite armor + tools
            if (stack.isOf(Items.NETHERITE_HELMET) ||
                stack.isOf(Items.NETHERITE_CHESTPLATE) ||
                stack.isOf(Items.NETHERITE_LEGGINGS) ||
                stack.isOf(Items.NETHERITE_BOOTS) ||
                stack.isOf(Items.NETHERITE_SWORD) ||
                stack.isOf(Items.NETHERITE_PICKAXE) ||
                stack.isOf(Items.NETHERITE_AXE) ||
                stack.isOf(Items.NETHERITE_SHOVEL) ||
                stack.isOf(Items.NETHERITE_HOE) ||
                stack.isOf(Items.NETHERITE_INGOT) ||
                stack.isOf(Items.NETHERITE_SCRAP)) {
                return true;
            }
        }
        return false;
    }
}
