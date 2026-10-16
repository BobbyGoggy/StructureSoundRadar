package com.client.mod.feature;

import com.client.mod.config.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class StructureScanner {

    public static void onRenderWorld(WorldRenderContext context) {
        if (!Config.structureScannerEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        BlockPos playerPos = client.player.getBlockPos();
        ChunkPos playerChunk = new ChunkPos(playerPos);

        int radius = 2;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int cx = playerChunk.x + x;
                int cz = playerChunk.z + z;

                double minX = cx << 4;
                double minZ = cz << 4;
                double maxX = minX + 16;
                double maxZ = minZ + 16;
                double minY = client.world.getBottomY();
                double maxY = client.world.getTopY();

                Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
                drawBoxOutline(matrices, box, 0.0f, 1.0f, 1.0f, 0.4f);
            }
        }

        matrices.pop();
    }

    private static void drawBoxOutline(MatrixStack matrices, Box box, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        WorldRenderer.drawBox(matrices, buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
