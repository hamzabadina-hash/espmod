package com.espmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EspModClient implements ClientModInitializer {

    public static final String MOD_ID = "espmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyBinding toggleKey;
    public static boolean espEnabled = false;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.espmod.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.espmod"
        ));

        WorldRenderEvents.LAST.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();

            while (toggleKey.wasPressed()) {
                espEnabled = !espEnabled;
                if (client.player != null) {
                    client.player.sendMessage(
                        net.minecraft.text.Text.literal(
                            espEnabled ? "[ESP] ON" : "[ESP] OFF"
                        ).formatted(espEnabled ?
                            net.minecraft.util.Formatting.GREEN :
                            net.minecraft.util.Formatting.RED),
                        true
                    );
                }
            }

            if (!espEnabled) return;
            if (client.player == null || client.world == null) return;

            ClientWorld world = client.world;
            Camera camera = context.camera();
            Matrix4f modelViewMatrix = context.matrixStack().peek().getPositionMatrix();

            GL11.glDisable(GL11.GL_DEPTH_TEST);

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
                                color = new float[]{1.0f, 0.3f, 0.0f};
                            } else {
                                color = new float[]{1.0f, 1.0f, 1.0f};
                            }
                        } else if (be instanceof MobSpawnerBlockEntity) {
                            color = new float[]{1.0f, 0.0f, 0.0f};
                        } else if (be instanceof ShulkerBoxBlockEntity shulker) {
                            if (hasValuableItem(shulker)) {
                                color = new float[]{1.0f, 0.3f, 0.0f};
                            } else {
                                color = new float[]{0.5f, 0.0f, 1.0f};
                            }
                        }

                        if (color != null) {
                            EspRenderer.drawBox(camera, pos, color[0], color[1], color[2], modelViewMatrix);
                        }
                    }
                }
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
        });

        LOGGER.info("ESP Mod loaded! Press Z to toggle.");
    }

    private boolean hasValuableItem(net.minecraft.inventory.Inventory inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.isOf(Items.DIAMOND_HELMET) ||
                stack.isOf(Items.DIAMOND_CHESTPLATE) ||
                stack.isOf(Items.DIAMOND_LEGGINGS) ||
                stack.isOf(Items.DIAMOND_BOOTS) ||
                stack.isOf(Items.DIAMOND_SWORD) ||
                stack.isOf(Items.DIAMOND_PICKAXE) ||
                stack.isOf(Items.DIAMOND_AXE) ||
                stack.isOf(Items.DIAMOND_SHOVEL) ||
                stack.isOf(Items.DIAMOND_HOE) ||
                stack.isOf(Items.DIAMOND) ||
                stack.isOf(Items.NETHERITE_HELMET) ||
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
