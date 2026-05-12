package dev.cosmos.example.item;

import dev.cosmos.example.entity.ExampleFireTrailProjectileEntity;
import dev.cosmos.init.ModEntityTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExampleTrailItem extends Item {
    public ExampleTrailItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            ExampleFireTrailProjectileEntity projectile = new ExampleFireTrailProjectileEntity(ModEntityTypes.TEST_PROJECTILE.get(), level);
            projectile.setPos(player.getX(), player.getEyeY(), player.getZ());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);

            level.addFreshEntity(projectile);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}