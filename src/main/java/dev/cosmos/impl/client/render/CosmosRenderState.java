package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.cosmos.api.data.TrailDefinition;

public class CosmosRenderState {

    public static void beginBatch() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
    }

    public static void endBatch() {
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    public static void setup(TrailDefinition.RenderState state) {
        if (state == null) {
            restoreToBatchDefault();
            return;
        }

        if ("ADDITIVE".equalsIgnoreCase(state.transparency)) {
            RenderSystem.blendFunc(
                    com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                    com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE
            );
        } else if ("OPAQUE".equalsIgnoreCase(state.transparency)) {
            RenderSystem.disableBlend();
        } else {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        if ("NONE".equalsIgnoreCase(state.depth_test) || "ALWAYS".equalsIgnoreCase(state.depth_test)) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(515);
        }
    }

    public static void restoreToBatchDefault() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
    }
}