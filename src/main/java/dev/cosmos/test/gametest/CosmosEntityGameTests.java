package dev.cosmos.test.gametest;

import dev.cosmos.api.entity.builtin.PlayerCrosshairBeamEntity;
import dev.cosmos.example.entity.ExampleFireBeamEntity;
import dev.cosmos.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("cosmos")
public class CosmosEntityGameTests {


    @GameTest(templateNamespace = "minecraft", template = "empty")
    @PrefixGameTestTemplate(false)
    public void testBeamDiscardsWithoutOwner(GameTestHelper helper) {


        PlayerCrosshairBeamEntity beam = new ExampleFireBeamEntity(ModEntityTypes.BEAM_ENTITY.get(), helper.getLevel());


        BlockPos absolutePos = helper.absolutePos(new BlockPos(1, 2, 1));
        beam.setPos(absolutePos.getX() + 0.5, absolutePos.getY(), absolutePos.getZ() + 0.5);


        helper.getLevel().addFreshEntity(beam);


        helper.runAfterDelay(5, () -> {


            if (beam.isAlive()) {
                helper.fail("Beam should have discarded itself without an owner!");
            } else {
                helper.succeed();
            }
        });
    }
}