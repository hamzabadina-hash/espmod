package com.espmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;

public class EspRenderer {

    public static void drawBox(Camera camera, BlockPos pos,
                                float r, float g, float b) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getBufferBuilders() == null) return;

            VertexConsumerProvider.Immediate immediate =
                    client.getBufferBuilders().getEntityVertexConsumers();

            // Use exact block position relative to camera
            float x = (float)(pos.getX() - camera.getPos().x);
            float y = (float)(pos.getY() - camera.getPos().y);
            float z = (float)(pos.getZ() - camera.getPos().z);

            MatrixStack matrices = new MatrixStack();
            VertexConsumer buffer = immediate.getBuffer(RenderLayer.getLines());

            // Draw exact 1x1x1 box at block position
            drawLine(buffer, matrices, x,   y,   z,   x+1, y,   z,   r,g,b);
            drawLine(buffer, matrices, x+1, y,   z,   x+1, y+1, z,   r,g,b);
            drawLine(buffer, matrices, x+1, y+1, z,   x,   y+1, z,   r,g,b);
            drawLine(buffer, matrices, x,   y+1, z,   x,   y,   z,   r,g,b);
            drawLine(buffer, matrices, x,   y,   z+1, x+1, y,   z+1, r,g,b);
            drawLine(buffer, matrices, x+1, y,   z+1, x+1, y+1, z+1, r,g,b);
            drawLine(buffer, matrices, x+1, y+1, z+1, x,   y+1, z+1, r,g,b);
            drawLine(buffer, matrices, x,   y+1, z+1, x,   y,   z+1, r,g,b);
            drawLine(buffer, matrices, x,   y,   z,   x,   y,   z+1, r,g,b);
            drawLine(buffer, matrices, x+1, y,   z,   x+1, y,   z+1, r,g,b);
            drawLine(buffer, matrices, x+1, y+1, z,   x+1, y+1, z+1, r,g,b);
            drawLine(buffer, matrices, x,   y+1, z,   x,   y+1, z+1, r,g,b);

            immediate.draw(RenderLayer.getLines());

        } catch (Exception ignored) {}
    }

    private static void drawLine(VertexConsumer buffer, MatrixStack matrices,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float r, float g, float b) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float dx = x2-x1, dy = y2-y1, dz = z2-z1;
        float len = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len == 0) len = 1;
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, 1.0f).normal(dx/len, dy/len, dz/len);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, 1.0f).normal(dx/len, dy/len, dz/len);
    }
}
