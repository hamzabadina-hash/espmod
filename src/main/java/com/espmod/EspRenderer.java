package com.espmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class EspRenderer {

    public static void drawBox(Camera camera, BlockPos pos,
                                float r, float g, float b) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getBufferBuilders() == null) return;

            VertexConsumerProvider.Immediate immediate =
                    client.getBufferBuilders().getEntityVertexConsumers();

            double camX = camera.getPos().x;
            double camY = camera.getPos().y;
            double camZ = camera.getPos().z;

            MatrixStack matrices = new MatrixStack();

            // Apply inverse camera rotation so box stays fixed in world space
            Quaternionf rotation = camera.getRotation();
            Quaternionf inverse = new Quaternionf(rotation).conjugate();
            matrices.multiply(inverse);

            // Translate to block position relative to camera
            matrices.translate(
                (float)(pos.getX() - camX),
                (float)(pos.getY() - camY),
                (float)(pos.getZ() - camZ)
            );

            VertexConsumer buffer = immediate.getBuffer(RenderLayer.getLines());

            drawLine(buffer, matrices, 0,0,0, 1,0,0, r,g,b);
            drawLine(buffer, matrices, 1,0,0, 1,1,0, r,g,b);
            drawLine(buffer, matrices, 1,1,0, 0,1,0, r,g,b);
            drawLine(buffer, matrices, 0,1,0, 0,0,0, r,g,b);
            drawLine(buffer, matrices, 0,0,1, 1,0,1, r,g,b);
            drawLine(buffer, matrices, 1,0,1, 1,1,1, r,g,b);
            drawLine(buffer, matrices, 1,1,1, 0,1,1, r,g,b);
            drawLine(buffer, matrices, 0,1,1, 0,0,1, r,g,b);
            drawLine(buffer, matrices, 0,0,0, 0,0,1, r,g,b);
            drawLine(buffer, matrices, 1,0,0, 1,0,1, r,g,b);
            drawLine(buffer, matrices, 1,1,0, 1,1,1, r,g,b);
            drawLine(buffer, matrices, 0,1,0, 0,1,1, r,g,b);

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
