package com.espmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

public class EspRenderer {

    // Render layer with depth test ALWAYS passing = xray
    private static final RenderLayer XRAY_LAYER = RenderLayer.of(
        "esp_xray",
        VertexFormats.LINES,
        VertexFormat.DrawMode.LINES,
        1536,
        false,
        false,
        RenderLayer.MultiPhaseParameters.builder()
            .program(RenderPhase.LINES_PROGRAM)
            .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2.0)))
            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
            .transparency(RenderPhase.NO_TRANSPARENCY)
            .target(RenderPhase.MAIN_TARGET)
            .writeMaskState(RenderPhase.COLOR_MASK)
            .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
            .cull(RenderPhase.DISABLE_CULLING)
            .build(false)
    );

    public static void drawBox(Camera camera, BlockPos pos,
                                float r, float g, float b,
                                Matrix4f modelViewMatrix) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getBufferBuilders() == null) return;

            VertexConsumerProvider.Immediate immediate =
                    client.getBufferBuilders().getEntityVertexConsumers();

            double camX = camera.getPos().x;
            double camY = camera.getPos().y;
            double camZ = camera.getPos().z;

            Matrix4f matrix = new Matrix4f(modelViewMatrix);
            matrix.translate(
                (float)(pos.getX() - camX),
                (float)(pos.getY() - camY),
                (float)(pos.getZ() - camZ)
            );

            VertexConsumer buffer = immediate.getBuffer(XRAY_LAYER);

            line(buffer, matrix, 0,0,0, 1,0,0, r,g,b);
            line(buffer, matrix, 1,0,0, 1,1,0, r,g,b);
            line(buffer, matrix, 1,1,0, 0,1,0, r,g,b);
            line(buffer, matrix, 0,1,0, 0,0,0, r,g,b);
            line(buffer, matrix, 0,0,1, 1,0,1, r,g,b);
            line(buffer, matrix, 1,0,1, 1,1,1, r,g,b);
            line(buffer, matrix, 1,1,1, 0,1,1, r,g,b);
            line(buffer, matrix, 0,1,1, 0,0,1, r,g,b);
            line(buffer, matrix, 0,0,0, 0,0,1, r,g,b);
            line(buffer, matrix, 1,0,0, 1,0,1, r,g,b);
            line(buffer, matrix, 1,1,0, 1,1,1, r,g,b);
            line(buffer, matrix, 0,1,0, 0,1,1, r,g,b);

            immediate.draw(XRAY_LAYER);

        } catch (Exception ignored) {}
    }

    private static void line(VertexConsumer buffer, Matrix4f matrix,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float r, float g, float b) {
        float dx = x2-x1, dy = y2-y1, dz = z2-z1;
        float len = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (len == 0) len = 1;
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, 1.0f).normal(dx/len, dy/len, dz/len);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, 1.0f).normal(dx/len, dy/len, dz/len);
    }
}
