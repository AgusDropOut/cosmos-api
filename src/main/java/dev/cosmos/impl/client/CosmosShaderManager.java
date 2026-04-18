package dev.cosmos.impl.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.cosmos.Cosmos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Cosmos.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CosmosShaderManager {

    private static final Gson GSON = new Gson();
    public static final Map<ResourceLocation, ShaderInstance> SHADERS = new HashMap<>();

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {

        System.out.println("Registering Cosmos Shaders...");
        SHADERS.clear();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();


        Map<ResourceLocation, Resource> materialFiles = resourceManager.listResources("cosmos_data",
                location -> location.getPath().endsWith(".mat.csm.json"));

        for (Map.Entry<ResourceLocation, Resource> entry : materialFiles.entrySet()) {
            try (InputStream stream = entry.getValue().open();
                 InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {


                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (!json.has("type") || !json.get("type").getAsString().equals("cosmos:material")) continue;

                String namespace = json.get("namespace").getAsString();
                String id = json.get("id").getAsString();
                ResourceLocation shaderLocation = new ResourceLocation(namespace, id);
                ResourceProvider virtualProvider = createVirtualShaderProvider(event.getResourceProvider(), namespace, id);

                System.out.println("Registering Cosmos Shader: " + shaderLocation);


                event.registerShader(
                        new ShaderInstance(virtualProvider, shaderLocation, DefaultVertexFormat.POSITION_COLOR_TEX),
                        shaderInstance -> {
                            SHADERS.put(shaderLocation, shaderInstance);
                            Cosmos.LOGGER.info("Successfully compiled and registered Cosmos Shader: {}", shaderLocation);
                        }
                );

            } catch (Exception e) {
                Cosmos.LOGGER.error("Failed to auto-register Cosmos shader from file: {}", entry.getKey(), e);
            }
        }
    }

    private static ResourceProvider createVirtualShaderProvider(ResourceProvider fallback, String namespace, String shaderId) {
        return location -> {
            if (location.getPath().endsWith(".json") && location.getPath().startsWith("shaders/core/")) {
                String fullShaderPath = namespace + ":" + shaderId;

                String dummyJson = """
                    {
                        "blend": {"func": "add", "srcrgb": "srcalpha", "dstrgb": "1-srcalpha"},
                        "vertex": "%s",
                        "fragment": "%s",
                        "attributes": ["Position", "Color", "UV0"],
                        "samplers": [{"name": "Sampler0"}]
                    }
                    """.formatted(fullShaderPath, fullShaderPath);

                return Optional.of(new Resource(null, () -> new ByteArrayInputStream(dummyJson.getBytes(StandardCharsets.UTF_8))));
            }
            return fallback.getResource(location);
        };
    }
}