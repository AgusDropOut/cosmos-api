package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.cosmos.api.data.BeamDefinition;
import dev.cosmos.impl.client.CosmosShaderManager;
import dev.cosmos.impl.data.handler.BeamDataHandler;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CosmosBeamManager {
    private static final List<BeamData> ACTIVE_BEAMS = new ArrayList<>();
    private static final Matrix4f savedProjection = new Matrix4f();
    private static final Matrix4f savedModelView = new Matrix4f();

    public static void submitBeam(ResourceLocation beamId, Vec3 start, Vec3 end, Matrix4f cameraMatrix) {
        if (ACTIVE_BEAMS.isEmpty()) {
            savedProjection.set(RenderSystem.getProjectionMatrix());
            savedModelView.set(cameraMatrix);
        }
        ACTIVE_BEAMS.add(new BeamData(beamId, start, end));
    }

    public static void renderAllAndClear() {
        if (ACTIVE_BEAMS.isEmpty()) return;

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

        for (BeamData data : ACTIVE_BEAMS) {
            BeamDefinition def = BeamDataHandler.BEAMS.get(data.beamId);
            if (def == null) continue;

            ShaderInstance shader = CosmosShaderManager.SHADERS.get(new ResourceLocation(def.config.materialId));
            if (shader == null) continue;

            RenderSystem.setShader(() -> shader);
            if (shader.getUniform("CosmosTime") != null) {
                shader.getUniform("CosmosTime").set(timeSeconds);
            }

            CosmosRenderState.setup(def.config.render_state);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            renderBeamGeometry(buffer, IDENTITY, data.start, data.end, def, timeSeconds);
            tess.end();

            CosmosRenderState.restoreToBatchDefault();
        }

        rsStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(currentProj, VertexSorting.ORTHOGRAPHIC_Z);

        CosmosRenderState.endBatch();
        ACTIVE_BEAMS.clear();
    }

    private static void renderBeamGeometry(BufferBuilder buffer, Matrix4f matrix, Vec3 startVec, Vec3 endVec, BeamDefinition def, float time) {
        Vector3f start = new Vector3f((float) startVec.x, (float) startVec.y, (float) startVec.z);
        Vector3f end = new Vector3f((float) endVec.x, (float) endVec.y, (float) endVec.z);

        Vector3f dir = new Vector3f(end).sub(start);
        float length = dir.length();
        if (length < 0.0001f) return;
        dir.normalize();

        Vector3f up = new Vector3f(0, 1, 0);
        if (Math.abs(dir.y) > 0.999f) up.set(1, 0, 0);

        Vector3f right = new Vector3f(up).cross(dir).normalize();
        up.set(dir).cross(right).normalize();

        int lenSegs = Math.max(1, def.config.lengthSegments);
        int radSegs = Math.max(3, def.config.radialSegments);

        Vector3f[][] rings = new Vector3f[lenSegs + 1][radSegs + 1];
        //  Declared as a 3D array to hold the [U, V] pairs
        float[][][] uvs = new float[lenSegs + 1][radSegs + 1][2];

        //  Calculate Vertices
        for (int i = 0; i <= lenSegs; i++) {
            float v = (float) i / lenSegs;
            Vector3f center = new Vector3f(start).lerp(end, v);

            // AST Evaluation!
            float r = def.compiledRadius.evaluate(time, v);
            float dx = def.compiledOffsetX.evaluate(time, v);
            float dy = def.compiledOffsetY.evaluate(time, v);
            float dz = def.compiledOffsetZ.evaluate(time, v);

            center.add(dx, dy, dz);

            for (int j = 0; j <= radSegs; j++) {
                float u = (float) j / radSegs;
                float theta = u * (float) Math.PI * 2.0f;

                float cos = (float) Math.cos(theta);
                float sin = (float) Math.sin(theta);

                Vector3f pos = new Vector3f(center);
                pos.add(new Vector3f(right).mul(cos * r));
                pos.add(new Vector3f(up).mul(sin * r));

                rings[i][j] = pos;
                uvs[i][j][0] = -1.0f + (u * 2.0f);
                uvs[i][j][1] = v;
            }
        }

        //  Draw Quads
        for (int i = 0; i < lenSegs; i++) {
            for (int j = 0; j < radSegs; j++) {
                Vector3f p1 = rings[i][j];
                Vector3f p2 = rings[i + 1][j];
                Vector3f p3 = rings[i + 1][j + 1];
                Vector3f p4 = rings[i][j + 1];


                buffer.vertex(matrix, p1.x(), p1.y(), p1.z()).color(1f, 1f, 1f, 1f).uv(uvs[i][j][0], uvs[i][j][1]).endVertex();
                buffer.vertex(matrix, p2.x(), p2.y(), p2.z()).color(1f, 1f, 1f, 1f).uv(uvs[i + 1][j][0], uvs[i + 1][j][1]).endVertex();
                buffer.vertex(matrix, p3.x(), p3.y(), p3.z()).color(1f, 1f, 1f, 1f).uv(uvs[i + 1][j + 1][0], uvs[i + 1][j + 1][1]).endVertex();
                buffer.vertex(matrix, p4.x(), p4.y(), p4.z()).color(1f, 1f, 1f, 1f).uv(uvs[i][j + 1][0], uvs[i][j + 1][1]).endVertex();
            }
        }
    }

    private static class BeamData {
        ResourceLocation beamId;
        Vec3 start;
        Vec3 end;

        BeamData(ResourceLocation id, Vec3 start, Vec3 end) {
            this.beamId = id;
            this.start = start;
            this.end = end;
        }
    }
}