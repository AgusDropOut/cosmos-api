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

import java.util.ArrayList;
import java.util.List;

public class CosmosTestProjectileRenderer extends EntityRenderer<CosmosTestProjectile> {

    private static final ResourceLocation TRAIL_ID = new ResourceLocation("cosmos", "my_first_project");

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

        double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        List<Vec3> rawHistory = new ArrayList<>();
        rawHistory.add(new Vec3(lerpX, lerpY, lerpZ));
        rawHistory.addAll(entity.history);


        CosmosTrailManager.submitTrail(TRAIL_ID, rawHistory);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CosmosTestProjectile entity) {
        return null;
    }
}