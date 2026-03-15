package com.espmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
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
        LOGGER.info("ESP Mod loaded! Press Z to toggle.");
    }
}
