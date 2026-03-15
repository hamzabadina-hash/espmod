package com.espmod;

import com.mojang.blaze3d.systems.RenderSystem;
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

            double camX = camera.getPos().x;
            double camY = camera.getPos().y;
            double camZ = camera.getPos().z;

            // Offset to center the box on the block
            double x = pos.getX() - camX;
            double y = pos.getY() - camY;
            double z = pos.getZ() - camZ;

            MatrixStack matrices = new MatrixStack();

            VertexConsumer buffer = immediate.getBuffer(RenderLayer.getLines());

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
                                  double x1, double y1, double z1,
                                  double x2, double y2, double z2,
                                  float r, float g, float b) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        double dx = x2-x1, dy = y2-y1, dz = z2-z1;
        double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len == 0) len = 1;
        buffer.vertex(matrix, (float)x1, (float)y1, (float)z1)
              .color(r, g, b, 1.0f)
              .normal((float)(dx/len), (float)(dy/len), (float)(dz/len));
        buffer.vertex(matrix, (float)x2, (float)y2, (float)z2)
              .color(r, g, b, 1.0f)
              .normal((float)(dx/len), (float)(dy/len), (float)(dz/len));
    }
}
