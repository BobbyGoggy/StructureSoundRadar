package com.client.mod;

import com.client.mod.config.Config;
import com.client.mod.feature.SoundRadar;
import com.client.mod.feature.StructureScanner;
import com.client.mod.ui.ModMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.lwjgl.glfw.GLFW;

public class ClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.LAST.register(StructureScanner::onRenderWorld);
        HudRenderCallback.EVENT.register(SoundRadar::onRenderHud);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() != null) {
                boolean isPressed = GLFW.glfwGetKey(client.getWindow().getHandle(), Config.menuKey) == GLFW.GLFW_PRESS;
                if (isPressed && client.currentScreen == null) {
                    client.setScreen(new ModMenuScreen());
                }
            }
        });
    }
}
