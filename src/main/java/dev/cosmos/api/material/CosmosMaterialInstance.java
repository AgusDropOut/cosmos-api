package dev.cosmos.api.material;

import com.mojang.blaze3d.shaders.Uniform;
import dev.cosmos.api.data.MaterialDefinition;
import dev.cosmos.impl.data.handler.MaterialDataHandler;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class CosmosMaterialInstance {
    private final ResourceLocation materialId;
    private final Map<String, UniformType> expectedParameters = new HashMap<>();


    private final Map<String, Object> values = new HashMap<>();

    public CosmosMaterialInstance(ResourceLocation materialId) {
        this.materialId = materialId;

        MaterialDefinition definition = MaterialDataHandler.MATERIALS.get(materialId);
        if (definition == null) {
            throw new IllegalArgumentException("Cosmos API: Material '" + materialId + "' does not exist!");
        }

        if (definition.config != null && definition.config.exposedParameters != null) {
            definition.config.exposedParameters.forEach((name, typeString) -> {
                UniformType type = UniformType.fromString(typeString);
                if (type != null) expectedParameters.put(name, type);
            });
        }
    }

    // Type-Safe Setters
    public CosmosMaterialInstance setFloat(String name, float value) {
        validateType(name, UniformType.FLOAT);
        values.put(name, value);
        return this;
    }

    public CosmosMaterialInstance setVec3(String name, float r, float g, float b) {
        validateType(name, UniformType.VEC3);
        values.put(name, new Vector3f(r, g, b));
        return this;
    }

    private void validateType(String name, UniformType expectedType) {
        UniformType actualType = expectedParameters.get(name);
        if (actualType == null) {
            throw new IllegalArgumentException("Uniform '" + name + "' is not exposed in material " + materialId);
        }
        if (actualType != expectedType) {
            throw new IllegalArgumentException("Type mismatch for '" + name + "'. Expected " + actualType + " but tried to set " + expectedType);
        }
    }

    /**
     * Called every frame by the renderer to push these values to the GPU.
     */
    public void applyTo(ShaderInstance shader) {
        if (shader == null) return;

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();


            Uniform uniform = shader.getUniform(name);

            if (uniform != null) {

                if (value instanceof Float f) {
                    uniform.set(f);
                } else if (value instanceof org.joml.Vector3f v3) {
                    uniform.set(v3);
                } else if (value instanceof org.joml.Vector2f v2) {
                    throw new UnsupportedOperationException("Vector2f is not currently supported as a uniform type. Please use Vector3f with a default value for the unused component.");
                }

            }
        }
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public ResourceLocation getMaterialId() {
        return materialId;
    }
}