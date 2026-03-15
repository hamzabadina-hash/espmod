package com.espmod.mixin;

import com.espmod.EspModClient;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(net.minecraft.client.render.RenderTickCounter tickCounter,
                          boolean renderBlockOutline, Camera camera,
                          GameRenderer gameRenderer,
                          LightmapTextureManager lightmapTextureManager,
                          Matrix4f matrix4f, Matrix4f matrix4f2,
                          CallbackInfo ci) {

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
        MatrixStack matrices = new MatrixStack();
        matrices.multiplyPositionMatrix(matrix4f2);

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
                            color = new float[]{1.0f, 0.84f, 0.0f};
                        } else {
                            color = new float[]{0.0f, 1.0f, 1.0f};
                        }
                    } else if (be instanceof MobSpawnerBlockEntity) {
                        color = new float[]{1.0f, 0.0f, 0.0f};
                    } else if (be instanceof ShulkerBoxBlockEntity shulker) {
                        if (hasValuableItem(shulker)) {
                            color = new float[]{1.0f, 0.84f, 0.0f};
                        } else {
                            color = new float[]{0.6f, 0.0f, 0.8f};
                        }
                    }

                    if (color != null) {
                        EspRenderer.drawBox(matrices, camera, pos, color[0], color[1], color[2]);
                    }
                }
            }
        }
    }

    private boolean hasValuableItem(net.minecraft.inventory.Inventory inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.isOf(Items.DIAMOND_HELMET) || stack.isOf(Items.DIAMOND_CHESTPLATE) ||
                stack.isOf(Items.DIAMOND_LEGGINGS) || stack.isOf(Items.DIAMOND_BOOTS) ||
                stack.isOf(Items.NETHERITE_HELMET) || stack.isOf(Items.NETHERITE_CHESTPLATE) ||
                stack.isOf(Items.NETHERITE_LEGGINGS) || stack.isOf(Items.NETHERITE_BOOTS) ||
                stack.isOf(Items.NETHERITE_INGOT) || stack.isOf(Items.DIAMOND) ||
                stack.isOf(Items.DIAMOND_SWORD) || stack.isOf(Items.NETHERITE_SWORD)) {
                return true;
            }
        }
        return false;
    }
}
