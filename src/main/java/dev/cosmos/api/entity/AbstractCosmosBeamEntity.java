package dev.cosmos.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


/**
 * The high-level base class for creating JSON-driven beam entities.
 * <p>
 * This class handles the complex rendering synchronization and state management for beams.
 * Implementors should extend this class and configure the visual state via
 * {@link CosmosBeamState#builder()} during initialization or specific game events.
 * * <pre>
 * {@code
 * public class MagicBeam extends AbstractCosmosBeamEntity {
 * public MagicBeam(EntityType<?> type, Level level) {
     *  super(type, level);
     *  this.setBeamState(CosmosBeamState.builder()
     *  .addBeam(new ResourceLocation("modid", "magic_beam"))
     *  .build());
 *  }
 * }
 * }</pre>
 * * @see dev.cosmos.api.entity.CosmosBeamState
 */
public abstract class AbstractCosmosBeamEntity extends Entity implements ICosmosBeam {

    private CosmosBeamState beamState;

    public AbstractCosmosBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    protected abstract CosmosBeamState createDefaultState();

    @Override
    public CosmosBeamState getBeamState() {
        if (this.beamState == null) {
            this.beamState = this.createDefaultState();
        }
        return this.beamState;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.getBeamState().tickLerp(this.getTargetPosition());
        }
    }

    public abstract Vec3 getTargetPosition();
}