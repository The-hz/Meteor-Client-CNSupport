/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer.text;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class VanillaTextRenderer implements TextRenderer {
    public static final VanillaTextRenderer INSTANCE = new VanillaTextRenderer();

    private final BufferAllocator buffer = new BufferAllocator(2048);
    private final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(buffer);

    public double scale = 2;
    public boolean scaleIndividually;

    private boolean building;
    private double alpha = 1;

    // 缓存渲染请求，等 end() 时拿到正确的矩阵再画
    private record QueuedText(String text, float x, float y, int color, boolean shadow) {}
    private final List<QueuedText> textQueue = new ArrayList<>();

    private VanillaTextRenderer() {
        // Use INSTANCE
    }

    @Override
    public void setAlpha(double a) {
        alpha = a;
    }

    @Override
    public double getWidth(String text, int length, boolean shadow) {
        if (text.isEmpty()) return 0;

        if (length != text.length()) text = text.substring(0, length);
        return (mc.textRenderer.getWidth(text) + (shadow ? 1 : 0)) * scale;
    }

    @Override
    public double getHeight(boolean shadow) {
        return (mc.textRenderer.fontHeight + (shadow ? 1 : 0)) * scale;
    }

    @Override
    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (building) throw new RuntimeException("VanillaTextRenderer.begin() called twice");

        this.scale = scale * 2;
        this.building = true;
        textQueue.clear();
    }

    @Override
    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin();

        x += 0.5 * scale;
        y += 0.5 * scale;

        // 计算带透明度的颜色，但不立即绘制，放入队列
        int packedColor = new Color(color.r, color.g, color.b, (int) (((double) color.a / 255 * alpha) * 255)).getPacked();
        textQueue.add(new QueuedText(text, (float) (x / scale), (float) (y / scale), packedColor, shadow));

        double width = mc.textRenderer.getWidth(text) + (shadow ? 1 : 0);

        if (!wasBuilding) end(null);
        return (x / scale + width - 1) * scale;
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void end(MatrixStack matrices) {
        if (!building) throw new RuntimeException("VanillaTextRenderer.end() called without calling begin()");

        Matrix4f baseMatrix = matrices != null ? matrices.peek().getPositionMatrix() : new Matrix4f();

        Matrix4f finalMatrix = new Matrix4f(baseMatrix);
        finalMatrix.scale((float) scale, (float) scale, 1);

        RenderSystem.disableDepthTest();

        for (QueuedText qt : textQueue) {
            mc.textRenderer.draw(qt.text(), qt.x(), qt.y(), qt.color(), qt.shadow(), finalMatrix, immediate, TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        }

        textQueue.clear();
        immediate.draw();

        RenderSystem.enableDepthTest();

        this.scale = 2;
        this.building = false;
    }
}
