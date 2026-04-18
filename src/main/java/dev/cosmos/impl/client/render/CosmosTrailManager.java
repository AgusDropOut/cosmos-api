package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.cosmos.api.data.TrailDefinition;
import dev.cosmos.impl.client.CosmosShaderManager;
import dev.cosmos.impl.data.CosmosDataManager;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CosmosTrailManager {

    private static final List<TrailData> ACTIVE_TRAILS = new ArrayList<>();


    public static void submitTrail(ResourceLocation trailId, List<Vec3> history) {
        if (history.size() < 2) return;
        ACTIVE_TRAILS.add(new TrailData(trailId, new ArrayList<>(history)));
    }


    public static void renderAllAndClear(PoseStack poseStack, Matrix4f projectionMatrix, Camera camera) {
        if (ACTIVE_TRAILS.isEmpty()) return;

        Vec3 cameraPos = camera.getPosition();
        float time = (System.currentTimeMillis() % 100000L) / 1000.0F;


        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );




        Matrix4f matrix = poseStack.last().pose();
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();


        for (TrailData data : ACTIVE_TRAILS) {
            TrailDefinition trailDef = CosmosDataManager.TRAILS.get(data.trailId);
            if (trailDef == null) continue;

            ResourceLocation materialId = new ResourceLocation(trailDef.config.materialId);
            ShaderInstance shader = CosmosShaderManager.SHADERS.get(materialId);
            if (shader == null) continue;

            RenderSystem.setShader(() -> shader);
            if (shader.getUniform("CosmosTime") != null) {
                shader.getUniform("CosmosTime").set(time);
            }

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

            int size = data.history.size();
            float vStep = 2.0F / (size - 1);
            float baseWidth = 0.4f;

            for (int i = 0; i < size - 1; i++) {
                Vec3 current = data.history.get(i);
                Vec3 next = data.history.get(i + 1);

                Vector3f dir = new Vector3f((float) (next.x - current.x), (float) (next.y - current.y), (float) (next.z - current.z));
                if (dir.lengthSquared() < 0.0001f) continue;
                dir.normalize();

                Vector3f toCameraCurrent = new Vector3f((float) (cameraPos.x - current.x), (float) (cameraPos.y - current.y), (float) (cameraPos.z - current.z)).normalize();
                Vector3f rightCurrent = new Vector3f();
                dir.cross(toCameraCurrent, rightCurrent);
                rightCurrent.normalize().mul(baseWidth);

                Vector3f toCameraNext = new Vector3f((float) (cameraPos.x - next.x), (float) (cameraPos.y - next.y), (float) (cameraPos.z - next.z)).normalize();
                Vector3f rightNext = new Vector3f();
                dir.cross(toCameraNext, rightNext);
                rightNext.normalize().mul(baseWidth);

                float v1 = 1.0F - (i * vStep);
                float v2 = 1.0F - ((i + 1) * vStep);


                float cx = (float) (current.x - cameraPos.x);
                float cy = (float) (current.y - cameraPos.y);
                float cz = (float) (current.z - cameraPos.z);

                float nx = (float) (next.x - cameraPos.x);
                float ny = (float) (next.y - cameraPos.y);
                float nz = (float) (next.z - cameraPos.z);

                float alpha = 1.0F - ((float) i / size);

                buffer.vertex(matrix, cx - rightCurrent.x, cy - rightCurrent.y, cz - rightCurrent.z).color(1f, 1f, 1f, alpha).uv(0, v1).endVertex();
                buffer.vertex(matrix, cx + rightCurrent.x, cy + rightCurrent.y, cz + rightCurrent.z).color(1f, 1f, 1f, alpha).uv(1, v1).endVertex();
                buffer.vertex(matrix, nx + rightNext.x, ny + rightNext.y, nz + rightNext.z).color(1f, 1f, 1f, alpha).uv(1, v2).endVertex();
                buffer.vertex(matrix, nx - rightNext.x, ny - rightNext.y, nz - rightNext.z).color(1f, 1f, 1f, alpha).uv(0, v2).endVertex();
            }
            tess.end();
        }




        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        ACTIVE_TRAILS.clear();
    }

    private static class TrailData {
        ResourceLocation trailId;
        List<Vec3> history;

        TrailData(ResourceLocation trailId, List<Vec3> history) {
            this.trailId = trailId;
            this.history = history;
        }
    }
}