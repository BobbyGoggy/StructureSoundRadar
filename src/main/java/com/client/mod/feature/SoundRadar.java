package com.client.mod.feature;

import com.client.mod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SoundRadar {
    private static final List<SoundEntry> activeSounds = new ArrayList<>();

    public static void addSound(double x, double y, double z, String soundName) {
        if (!Config.soundRadarEnabled) return;
        synchronized (activeSounds) {
            activeSounds.add(new SoundEntry(x, y, z, System.currentTimeMillis()));
        }
    }

    public static void onRenderHud(DrawContext context, float tickDelta) {
        if (!Config.soundRadarEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        long now = System.currentTimeMillis();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight - 60;

        context.fill(centerX - 100, centerY - 15, centerX + 100, centerY + 15, 0x80000000);
        context.drawCenteredTextWithShadow(client.textRenderer, "🎵 РАДАР ЗВУКОВ", centerX, centerY - 10, 0xFF55FF55);

        synchronized (activeSounds) {
            Iterator<SoundEntry> iterator = activeSounds.iterator();
            while (iterator.hasNext()) {
                SoundEntry entry = iterator.next();
                if (now - entry.timestamp > 2000) {
                    iterator.remove();
                    continue;
                }

                Vec3d playerPos = client.player.getPos();
                Vec3d soundPos = new Vec3d(entry.x, entry.y, entry.z);
                Vec3d dirToSound = soundPos.subtract(playerPos).normalize();

                Vec3d lookDir = client.player.getRotationVec(1.0F);
                double cross = lookDir.z * dirToSound.x - lookDir.x * dirToSound.z;

                int offset = (int) (cross * 80);
                int markerX = centerX + Math.max(-90, Math.min(90, offset));

                context.fill(markerX - 2, centerY + 5, markerX + 2, centerY + 12, 0xFFFF5555);
            }
        }
    }

    private static class SoundEntry {
        double x, y, z;
        long timestamp;

        public SoundEntry(double x, double y, double z, long timestamp) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.timestamp = timestamp;
        }
    }
}
