/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.renderer.text;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

public class Font {
    public AbstractTexture texture;
    private final int height;
    private final float scale;
    private final float ascent;
    private final Int2ObjectOpenHashMap<CharData> charMap = new Int2ObjectOpenHashMap<>();
    private static final int size = 81920;

    private static int[] extraCodepoints = new int[0];

    public static void loadCharset() {
        try (InputStream in = Font.class.getResourceAsStream("/assets/" + MeteorClient.MOD_ID + "/fonts/charset.txt")) {
            if (in == null) {
                MeteorClient.LOG.warn("charset.txt not found, CJK support disabled.");
                extraCodepoints = new int[0];
                return;
            }
            String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            extraCodepoints = s.codePoints().distinct().sorted().toArray();
            MeteorClient.LOG.info("Loaded {} extra codepoints for CJK.", extraCodepoints.length);
        } catch (Exception e) {
            MeteorClient.LOG.error("Failed to load charset.txt", e);
            extraCodepoints = new int[0];
        }
    }

    public Font(ByteBuffer buffer, int height) {
        this.height = height;

        // Initialize font
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTruetype.stbtt_InitFont(fontInfo, buffer);

        // Allocate buffers
        ByteBuffer bitmap = BufferUtils.createByteBuffer(size * size);
        STBTTPackedchar.Buffer[] cdata = {
            STBTTPackedchar.create(95),                      // Basic Latin
            STBTTPackedchar.create(96),                      // Latin 1 Supplement
            STBTTPackedchar.create(128),                     // Latin Extended-A
            STBTTPackedchar.create(144),                     // Greek and Coptic
            STBTTPackedchar.create(256),                     // Cyrillic
            STBTTPackedchar.create(1),                       // infinity symbol
            STBTTPackedchar.create(extraCodepoints.length)   // CJK
        };


        IntBuffer cpBuf = null;
        if (extraCodepoints.length > 0) {
            cpBuf = BufferUtils.createIntBuffer(extraCodepoints.length);
            cpBuf.put(extraCodepoints).flip();
        }

        // create and initialise packing context
        STBTTPackContext packContext = STBTTPackContext.create();
        STBTruetype.stbtt_PackBegin(packContext, bitmap, size, size, 0, 1);

        // create the pack range, populate with the specific packing ranges
        STBTTPackRange.Buffer packRange = STBTTPackRange.create(cdata.length);
        packRange.put(STBTTPackRange.create().set(height, 32,   null, 95,  cdata[0], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 160,  null, 96,  cdata[1], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 256,  null, 128, cdata[2], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 880,  null, 144, cdata[3], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 1024, null, 256, cdata[4], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 8734, null, 1,   cdata[5], (byte) 2, (byte) 2));
        if (cpBuf != null) {
            packRange.put(STBTTPackRange.create().set(height, 0, cpBuf,
                extraCodepoints.length, cdata[6], (byte) 2, (byte) 2));
        } else {
            packRange.put(STBTTPackRange.create().set(height, 0, null, 0, cdata[6], (byte) 2, (byte) 2));
        }
        packRange.flip();

        // write and finish
        STBTruetype.stbtt_PackFontRanges(packContext, buffer, 0, packRange);
        STBTruetype.stbtt_PackEnd(packContext);

        // Create texture object and get font scale
        texture = new ByteTexture(size, size, bitmap, ByteTexture.Format.A, ByteTexture.Filter.Linear, ByteTexture.Filter.Linear);
        scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, height);

        // Get font vertical ascent
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ascent = stack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(fontInfo, ascent, null, null);
            this.ascent = ascent.get(0);
        }

        for (int i = 0; i < 6; i++) {
            STBTTPackedchar.Buffer cbuf = cdata[i];
            int offset = packRange.get(i).first_unicode_codepoint_in_range();
            for (int j = 0; j < cbuf.capacity(); j++) {
                charMap.put(j + offset, toCharData(cbuf.get(j)));
            }
        }

        if (cpBuf != null && cdata[6].capacity() > 0) {
            STBTTPackedchar.Buffer cbuf = cdata[6];
            cpBuf.rewind();
            for (int j = 0; j < cbuf.capacity(); j++) {
                int cp = cpBuf.get(j);
                charMap.put(cp, toCharData(cbuf.get(j)));
            }
        }
    }

    private CharData toCharData(STBTTPackedchar pc) {
        float ipw = 1f / size;
        float iph = 1f / size;
        return new CharData(
            pc.xoff(),
            pc.yoff(),
            pc.xoff2(),
            pc.yoff2(),
            pc.x0() * ipw,
            pc.y0() * iph,
            pc.x1() * ipw,
            pc.y1() * iph,
            pc.xadvance()
        );
    }

    public double getWidth(String string, int length) {
        double width = 0;
        for (int i = 0; i < length; i++) {
            int cp = string.charAt(i);
            CharData c = charMap.get(cp);
            if (c == null) c = charMap.get(32);
            width += c.xAdvance;
        }
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double render(Mesh mesh, String string, double x, double y, Color color, double scale) {
        y += ascent * this.scale * scale;
        for (int i = 0; i < string.length(); i++) {
            int cp = string.charAt(i);
            CharData c = charMap.get(cp);
            if (c == null) c = charMap.get(32);
            mesh.quad(
                mesh.vec2(x + c.x0 * scale, y + c.y0 * scale).vec2(c.u0, c.v0).color(color).next(),
                mesh.vec2(x + c.x0 * scale, y + c.y1 * scale).vec2(c.u0, c.v1).color(color).next(),
                mesh.vec2(x + c.x1 * scale, y + c.y1 * scale).vec2(c.u1, c.v1).color(color).next(),
                mesh.vec2(x + c.x1 * scale, y + c.y0 * scale).vec2(c.u1, c.v0).color(color).next()
            );
            x += c.xAdvance * scale;
        }
        return x;
    }

    private record CharData(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, float xAdvance) {}
}
