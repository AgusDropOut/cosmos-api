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

        // Calculate exact interpolated position
        double lerpX = Mth.lerp(partialTick, entity.xo, entity.getX());
        double lerpY = Mth.lerp(partialTick, entity.yo, entity.getY());
        double lerpZ = Mth.lerp(partialTick, entity.zo, entity.getZ());

        // Calculate relative distance from camera
        float relX = (float) (lerpX - cameraPos.x());
        float relY = (float) (lerpY - cameraPos.y());
        float relZ = (float) (lerpZ - cameraPos.z());

        // 3Extract the Pure Camera Matrix
        PoseStack viewStack = new PoseStack();
        viewStack.last().pose().set(poseStack.last().pose());
        viewStack.translate(-relX, -relY, -relZ);
        Matrix4f pureCameraMatrix = viewStack.last().pose();

        //  Submit to Manager
        CosmosBeamManager.submitBeam(
                entity.getBeamId(),
                entity.position(),
                entity.getEndPoint(),
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