package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.cosmos.api.entity.ICosmosBeam;
import dev.cosmos.api.entity.CosmosBeamState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;


public class CosmosBeamRenderer<T extends Entity & ICosmosBeam> extends EntityRenderer<T> {

    public CosmosBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();


        CosmosBeamState state = entity.getBeamState();

        double startX = Mth.lerp(partialTick, entity.xo, entity.getX());
        double startY = Mth.lerp(partialTick, entity.yo, entity.getY());
        double startZ = Mth.lerp(partialTick, entity.zo, entity.getZ());
        Vec3 finalStart = new Vec3(startX, startY, startZ);

        Vec3 finalEndpoint;
        if (!state.clientPreviousEndpoint.equals(Vec3.ZERO)) {
            double endX = Mth.lerp(partialTick, state.clientPreviousEndpoint.x, state.clientCurrentEndpoint.x);
            double endY = Mth.lerp(partialTick, state.clientPreviousEndpoint.y, state.clientCurrentEndpoint.y);
            double endZ = Mth.lerp(partialTick, state.clientPreviousEndpoint.z, state.clientCurrentEndpoint.z);
            finalEndpoint = new Vec3(endX, endY, endZ);
        } else {
            finalEndpoint = state.clientCurrentEndpoint;
        }

        float relX = (float) (startX - cameraPos.x());
        float relY = (float) (startY - cameraPos.y());
        float relZ = (float) (startZ - cameraPos.z());

        PoseStack viewStack = new PoseStack();
        viewStack.last().pose().set(poseStack.last().pose());
        viewStack.translate(-relX, -relY, -relZ);
        Matrix4f pureCameraMatrix = viewStack.last().pose();

        //  Submit
        for(CosmosBeamState.BeamLayer layer : state.getLayers()) {
            CosmosBeamManager.submitBeam(layer.beamId, layer.material, finalStart, finalEndpoint, cameraPos, pureCameraMatrix);
        }

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(T entity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}