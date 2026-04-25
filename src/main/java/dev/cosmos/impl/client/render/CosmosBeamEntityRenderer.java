package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.cosmos.impl.entity.CosmosBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CosmosBeamEntityRenderer extends EntityRenderer<CosmosBeamEntity> {

    public CosmosBeamEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CosmosBeamEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();


        double startX = Mth.lerp(partialTick, entity.xo, entity.getX());
        double startY = Mth.lerp(partialTick, entity.yo, entity.getY());
        double startZ = Mth.lerp(partialTick, entity.zo, entity.getZ());
        Vec3 finalStart = new Vec3(startX, startY, startZ);

        // MODULAR ENDPOINT LERPING STRATEGY
        boolean enableEndpointLerp = true;
        Vec3 finalEndpoint;

        if (enableEndpointLerp && !entity.clientPreviousEndpoint.equals(Vec3.ZERO)) {
            double endX = Mth.lerp(partialTick, entity.clientPreviousEndpoint.x, entity.clientCurrentEndpoint.x);
            double endY = Mth.lerp(partialTick, entity.clientPreviousEndpoint.y, entity.clientCurrentEndpoint.y);
            double endZ = Mth.lerp(partialTick, entity.clientPreviousEndpoint.z, entity.clientCurrentEndpoint.z);
            finalEndpoint = new Vec3(endX, endY, endZ);
        } else {
            // Fallback to raw snapped server data
            finalEndpoint = entity.getEndPoint();
        }


        float relX = (float) (startX - cameraPos.x());
        float relY = (float) (startY - cameraPos.y());
        float relZ = (float) (startZ - cameraPos.z());

        PoseStack viewStack = new PoseStack();
        viewStack.last().pose().set(poseStack.last().pose());
        viewStack.translate(-relX, -relY, -relZ);
        Matrix4f pureCameraMatrix = viewStack.last().pose();


        CosmosBeamManager.submitBeam(
                entity.getBeamId(),
                finalStart,
                finalEndpoint,
                cameraPos,
                pureCameraMatrix
        );

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(CosmosBeamEntity entity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(CosmosBeamEntity entity) {
        return null;
    }
}