package dev.cosmos.api.material;

import com.mojang.blaze3d.shaders.Uniform;
import dev.cosmos.api.data.MaterialDefinition;
import dev.cosmos.impl.data.handler.MaterialDataHandler;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;


/**
 * Represents a dynamic, mutable instance of a loaded Cosmos Material (.mat.csm.json).
 * <p>
 * This class allows developers to modify shader uniforms (variables exposed in the JSON)
 * on a per-entity, per-tick basis.
 * <p>
 * <b>Important:</b> Uniform mutations are inherently client-side operations. While the
 * API allows setting values on the logical server, it is highly recommended to perform
 * intensive uniform updates (like color interpolation) on the client to save network bandwidth.
 */
public class CosmosMaterialInstance {
    private final ResourceLocation materialId;
    private final Map<String, UniformType> expectedParameters = new HashMap<>();
    private final MaterialDefinition definition;
    private final Map<String, Object> values = new HashMap<>();

    public CosmosMaterialInstance(ResourceLocation materialId) {
        this.materialId = materialId;

        this.definition = MaterialDataHandler.MATERIALS.get(materialId);
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

    /**
     * Sets a floating-point uniform value for the shader.
     * <p>
     * <b>Important:</b> You may need to add a {@code u_} prefix to the name
     * due to shader naming conventions.
     *
     * @param name  The exact name of the uniform as defined in the material's JSON "exposed_parameters".
     * @param value The float value to pass to the shader.
     * @return This instance for method chaining.
     * @throws IllegalArgumentException If the uniform name is not exposed in the JSON, or if the declared type is not FLOAT.
     */
    public CosmosMaterialInstance setFloat(String name, float value) {
        validateType(name, UniformType.FLOAT);
        values.put(name, value);
        return this;
    }


    /**
     * Sets a 3-component vector uniform value for the shader, typically used for RGB colors or 3D coordinates.
     * <p>
     * <b>Important:</b> You may need to add a {@code u_} prefix to the name
     * due to shader naming conventions.
     *
     *
     * @param name The exact name of the uniform as defined in the material's JSON "exposed_parameters".
     * @param r    The first component (X or Red).
     * @param g    The second component (Y or Green).
     * @param b    The third component (Z or Blue).
     * @return This instance for method chaining.
     * @throws IllegalArgumentException If the uniform name is not exposed in the JSON, or if the declared type is not VEC3.
     */
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
     * Pushes the current uniform values to the active GPU shader instance.
     * This is invoked automatically by the internal Cosmos renderers every frame.
     *
     * @param shader The active shader instance to receive the uniform data.
     */
    public void applyTo(ShaderInstance shader) {

        if (shader == null) return;


        if (definition.config != null && definition.config.renderState != null) {
            Uniform alphaUniform = shader.getUniform("u_alphaCutoff");
            if (alphaUniform != null) {
                alphaUniform.set(definition.config.renderState.alphaCutoff);
            }
        }

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

    public MaterialDefinition getDefinition() {
        return this.definition;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public ResourceLocation getMaterialId() {
        return materialId;
    }
}