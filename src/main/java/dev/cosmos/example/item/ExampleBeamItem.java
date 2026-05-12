package dev.cosmos.example.item;


import dev.cosmos.example.entity.ExampleFireBeamEntity;
import dev.cosmos.init.ModEntityTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ExampleBeamItem extends Item {

    public ExampleBeamItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);


        player.startUsingItem(hand);

        if (!level.isClientSide) {

            ExampleFireBeamEntity beam = new ExampleFireBeamEntity(ModEntityTypes.BEAM_ENTITY.get(),level, player, 50f);
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