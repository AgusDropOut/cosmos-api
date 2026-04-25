package dev.cosmos.impl.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.cosmos.api.entity.CosmosTrailState;
import dev.cosmos.api.entity.ICosmosTrail;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity; // THE FIX IS HERE!
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CosmosTrailRenderer<T extends Entity & ICosmosTrail> extends EntityRenderer<T> {

    public CosmosTrailRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {


        CosmosTrailState state = entity.getTrailState();

        if (state.getHistory().isEmpty()) return;

        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();


        double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        //  Build the history using the current lerped head + the saved state history
        List<Vec3> rawHistory = new ArrayList<>();
        rawHistory.add(new Vec3(lerpX, lerpY, lerpZ));
        rawHistory.addAll(state.getHistory());

        float relX = (float) (lerpX - cameraPos.x());
        float relY = (float) (lerpY - cameraPos.y());
        float relZ = (float) (lerpZ - cameraPos.z());


        PoseStack viewStack = new PoseStack();
        viewStack.last().pose().set(poseStack.last().pose());
        viewStack.translate(-relX, -relY, -relZ);
        Matrix4f pureCameraMatrix = viewStack.last().pose();

        // Submit
        CosmosTrailManager.submitTrail(state.getTrailId(), rawHistory, cameraPos, pureCameraMatrix);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}