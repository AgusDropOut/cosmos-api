package dev.cosmos.api.entity;

import dev.cosmos.Cosmos;
import dev.cosmos.api.material.CosmosMaterialInstance;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class AbstractLayaredBuilderTest {

    @Test
    public void testAutoResolveMaterial_ShouldBeSkipped(){

        AbstractLayeredBuilder<CosmosBeamState.Builder, CosmosBeamState> builder = new CosmosBeamState.Builder();
        AbstractLayeredBuilder<CosmosBeamState.Builder, CosmosBeamState> spy = Mockito.spy(builder);
        CosmosMaterialInstance mockMat = Mockito.mock(CosmosMaterialInstance.class);


        try {
            CosmosBeamState beam = spy.addLayer(new ResourceLocation(Cosmos.MODID,"valid_beam"), mockMat).build();
        } catch (IllegalStateException e) {

        }

        verify(spy , never()).autoResolveMaterial(any());
    }

    @Test
    public void testAutoResolveMaterial_ShouldBeCalled(){

        AbstractLayeredBuilder<CosmosBeamState.Builder, CosmosBeamState> builder = new CosmosBeamState.Builder();
        AbstractLayeredBuilder<CosmosBeamState.Builder, CosmosBeamState> spy = Mockito.spy(builder);


        try{
            CosmosBeamState beam = spy.addLayer(new ResourceLocation(Cosmos.MODID,"valid_beam")).build();
        }catch (IllegalStateException e){

        }

        verify(spy , Mockito.times(1)).autoResolveMaterial(any());
    }


}
