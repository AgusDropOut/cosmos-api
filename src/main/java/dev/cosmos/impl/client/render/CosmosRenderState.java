// dev/cosmos/impl/client/render/CosmosRenderState.java
package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.cosmos.api.data.MaterialDefinition;

public class CosmosRenderState {

    public static void beginBatch() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
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


    public static void setup(MaterialDefinition.RenderState state) {
        if (state == null) {
            restoreToBatchDefault();
            return;
        }

        //BLEND MODE
        if ("ADDITIVE".equalsIgnoreCase(state.blendMode)) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        } else if ("MULTIPLY".equalsIgnoreCase(state.blendMode)) {
            RenderSystem.enableBlend();
            // Standard Multiply Blend in OpenGL
            RenderSystem.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ZERO);
        } else if ("OPAQUE".equalsIgnoreCase(state.blendMode)) {
            RenderSystem.disableBlend();
        } else {
            // TRANSLUCENT (Default)
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        //  CULL MODE
        if ("NONE".equalsIgnoreCase(state.cullMode)) {
            RenderSystem.disableCull(); // Double-sided
        } else {
            RenderSystem.enableCull(); // Standard back-face culling
        }

        // DEPTH TEST
        if ("ALWAYS".equalsIgnoreCase(state.depthTest)) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(515);
        }

        // DEPTH WRITE (Z-Buffer)
        RenderSystem.depthMask(state.depthWrite);
    }

    public static void restoreToBatchDefault() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
}