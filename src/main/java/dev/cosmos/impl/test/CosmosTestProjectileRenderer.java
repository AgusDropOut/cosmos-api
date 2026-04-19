package dev.cosmos.impl.test;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.cosmos.impl.client.render.CosmosTrailManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CosmosTestProjectileRenderer extends EntityRenderer<CosmosTestProjectile> {

    private static final ResourceLocation TRAIL_ID = new ResourceLocation("cosmos", "fire");

    public CosmosTestProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(CosmosTestProjectile entity, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(CosmosTestProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.history.isEmpty()) return;

        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();

        double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        List<Vec3> rawHistory = new ArrayList<>();
        rawHistory.add(new Vec3(lerpX, lerpY, lerpZ));
        rawHistory.addAll(entity.history);


        float relX = (float) (lerpX - cameraPos.x());
        float relY = (float) (lerpY - cameraPos.y());
        float relZ = (float) (lerpZ - cameraPos.z());


        PoseStack viewStack = new PoseStack();
        viewStack.last().pose().set(poseStack.last().pose());
        viewStack.translate(-relX, -relY, -relZ);

        Matrix4f pureCameraMatrix = viewStack.last().pose();


        CosmosTrailManager.submitTrail(TRAIL_ID, rawHistory, cameraPos, pureCameraMatrix);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CosmosTestProjectile entity) {
        return null;
    }
}