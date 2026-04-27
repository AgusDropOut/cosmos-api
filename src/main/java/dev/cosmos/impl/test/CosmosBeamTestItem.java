package dev.cosmos.impl.test;


import dev.cosmos.init.ModEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class CosmosBeamTestItem extends Item {

    public CosmosBeamTestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);


        player.startUsingItem(hand);

        if (!level.isClientSide) {

            CosmosBeamEntity beam = new CosmosBeamEntity(ModEntityTypes.BEAM_ENTITY.get(),level, player);
            level.addFreshEntity(beam);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {

    }

    @Override
    public int getUseDuration(ItemStack stack) {

        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
}