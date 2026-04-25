package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.cosmos.api.data.TrailDefinition;
import dev.cosmos.impl.client.CosmosShaderManager;
import dev.cosmos.impl.data.handler.TrailDataHandler;
import dev.cosmos.util.CosmosSplineHelper;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CosmosTrailManager {
    private static final List<TrailData> ACTIVE_TRAILS = new ArrayList<>();

    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void submitTrail(ResourceLocation trailId, List<Vec3> history, Vec3 cameraPos, Matrix4f cameraMatrix) {
        if (history.size() < 2) return;

        if (ACTIVE_TRAILS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(cameraMatrix);
        }

        ACTIVE_TRAILS.add(new TrailData(trailId, new ArrayList<>(history), cameraPos));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_TRAILS.isEmpty()) return;


        float timeSeconds = (System.currentTimeMillis() % 100000L) / 1000.0f;

        Matrix4f currentProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        PoseStack rsStack = RenderSystem.getModelViewStack();
        rsStack.pushPose();

        RenderSystem.setProjectionMatrix(savedProjection, VertexSorting.DISTANCE_TO_ORIGIN);
        rsStack.setIdentity();
        rsStack.mulPoseMatrix(savedModelView);
        RenderSystem.applyModelViewMatrix();

        CosmosRenderState.beginBatch();



        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        Matrix4f IDENTITY = new Matrix4f();

        for (TrailData data : ACTIVE_TRAILS) {
            TrailDefinition trailDef = TrailDataHandler.TRAILS.get(data.trailId);

            if (trailDef == null) continue;


            ShaderInstance shader = CosmosShaderManager.SHADERS.get(new ResourceLocation(trailDef.config.materialId));
            if (shader == null) continue;

            RenderSystem.setShader(() -> shader);
            if (shader.getUniform("CosmosTime") != null) {
                shader.getUniform("CosmosTime").set(timeSeconds); // Pass seconds
            }

            CosmosRenderState.setup(trailDef.config.render_state);

            //RenderSystem.setShaderTexture(0, net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS);

            List<Vec3> smoothHistory = generateSmoothHistory(data.history);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            renderTrailGeometry(buffer, IDENTITY, smoothHistory, data.cameraPos, trailDef, timeSeconds);
            tess.end();

            CosmosRenderState.restoreToBatchDefault();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        CosmosRenderState.endBatch();
        ACTIVE_TRAILS.clear();
    }

    private static List<Vec3> generateSmoothHistory(List<Vec3> raw) {
        List<Vec3> points = new ArrayList<>();
        points.add(raw.get(0));
        points.addAll(raw);
        points.add(raw.get(raw.size() - 1));

        List<Vec3> smooth = new ArrayList<>();
        int subdivisions = 4;
        for (int i = 1; i < points.size() - 2; i++) {
            for (int j = 0; j < subdivisions; j++) {
                smooth.add(CosmosSplineHelper.catmullRom(points.get(i - 1), points.get(i), points.get(i + 1), points.get(i + 2), (double) j / subdivisions));
            }
        }
        smooth.add(raw.get(raw.size() - 1));
        return smooth;
    }

    private static float evaluateWidth(TrailDefinition def, float time, float progress) {
        if (def.compiledWidth == null) return 1.0f;
        return def.compiledWidth.evaluate(time, progress);
    }

    private static Vec3 applyOrbitOffset(TrailDefinition def, Vec3 basePos, float time, float progress) {
        if (def.compiledOffsetX == null || def.compiledOffsetY == null || def.compiledOffsetZ == null) {
            return basePos;
        }
        float ox = def.compiledOffsetX.evaluate(time, progress);
        float oy = def.compiledOffsetY.evaluate(time, progress);
        float oz = def.compiledOffsetZ.evaluate(time, progress);
        return basePos.add(ox, oy, oz);
    }

    private static void renderTrailGeometry(BufferBuilder buffer, Matrix4f identity, List<Vec3> smoothHistory, Vec3 cameraPos, TrailDefinition trailDef, float timeSeconds) {
        int size = smoothHistory.size();


        float trailDurationSecs = trailDef.config.historySegments * 0.05f;

        for (int i = 0; i < size - 1; i++) {
            float v1 = 1.0F - ((float) i / size);
            float v2 = 1.0F - ((float) (i + 1) / size);

            float time1 = timeSeconds - ((1.0F - v1) * trailDurationSecs);
            float time2 = timeSeconds - ((1.0F - v2) * trailDurationSecs);


            Vec3 curr = applyOrbitOffset(trailDef, smoothHistory.get(i), time1, v1);
            Vec3 next = applyOrbitOffset(trailDef, smoothHistory.get(i + 1), time2, v2);

            Vector3f dir = new Vector3f((float) (next.x - curr.x), (float) (next.y - curr.y), (float) (next.z - curr.z));
            if (dir.lengthSquared() < 0.0001f) continue;
            dir.normalize();

            Vector3f toCam = new Vector3f((float) (cameraPos.x - curr.x), (float) (cameraPos.y - curr.y), (float) (cameraPos.z - curr.z)).normalize();


            float width1 = evaluateWidth(trailDef, time1, v1);
            float width2 = evaluateWidth(trailDef, time2, v2);

            Vector3f side1 = new Vector3f();
            dir.cross(toCam, side1);
            side1.normalize().mul(width1);

            Vector3f side2 = new Vector3f();
            dir.cross(toCam, side2);
            side2.normalize().mul(width2);

            float alpha = 1.0F - ((float) i / size);

            float cx = (float) (curr.x - cameraPos.x);
            float cy = (float) (curr.y - cameraPos.y);
            float cz = (float) (curr.z - cameraPos.z);

            float nx = (float) (next.x - cameraPos.x);
            float ny = (float) (next.y - cameraPos.y);
            float nz = (float) (next.z - cameraPos.z);

            buffer.vertex(identity, cx - side1.x, cy - side1.y, cz - side1.z).color(1f, 1f, 1f, alpha).uv(0, v1).endVertex();
            buffer.vertex(identity, cx + side1.x, cy + side1.y, cz + side1.z).color(1f, 1f, 1f, alpha).uv(1, v1).endVertex();
            buffer.vertex(identity, nx + side2.x, ny + side2.y, nz + side2.z).color(1f, 1f, 1f, alpha).uv(1, v2).endVertex();
            buffer.vertex(identity, nx - side2.x, ny - side2.y, nz - side2.z).color(1f, 1f, 1f, alpha).uv(0, v2).endVertex();
        }
    }

    private static class TrailData {
        ResourceLocation trailId;
        List<Vec3> history;
        Vec3 cameraPos;

        TrailData(ResourceLocation id, List<Vec3> h, Vec3 cam) {
            this.trailId = id;
            this.history = h;
            this.cameraPos = cam;
        }
    }
}