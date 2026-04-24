package dev.cosmos.api.data;

import com.google.gson.annotations.SerializedName;
import dev.cosmos.Cosmos;
import dev.cosmos.util.math.CosmosExpressionParser;
import dev.cosmos.util.math.MathExpression;

public class BeamDefinition {
    public String namespace;
    public String id;
    public String type;
    public Config config;

    // --- COMPILED EXPRESSIONS ---
    public transient MathExpression compiledRadius;
    public transient MathExpression compiledOffsetX;
    public transient MathExpression compiledOffsetY;
    public transient MathExpression compiledOffsetZ;

    public static class Config {
        @SerializedName("radial_segments")
        public int radialSegments;
        @SerializedName("length_segments")
        public int lengthSegments;
        @SerializedName("radius_curve")
        public String radiusCurve;
        @SerializedName("offset_x")
        public String offsetX;
        @SerializedName("offset_y")
        public String offsetY;
        @SerializedName("offset_z")
        public String offsetZ;
        @SerializedName("material_id")
        public String materialId;
        public TrailDefinition.RenderState render_state;
    }

    public void compileExpressions() {
        this.compiledRadius = safeCompile(this.config.radiusCurve, 0.5f);
        this.compiledOffsetX = safeCompile(this.config.offsetX, 0.0f);
        this.compiledOffsetY = safeCompile(this.config.offsetY, 0.0f);
        this.compiledOffsetZ = safeCompile(this.config.offsetZ, 0.0f);
    }

    private MathExpression safeCompile(String expression, float fallback) {
        if (expression != null && !expression.trim().isEmpty()) {
            try {
                return CosmosExpressionParser.parse(expression);
            } catch (Exception e) {
                Cosmos.LOGGER.error("Cosmos API: Failed to compile beam expression: '{}'", expression, e);
            }
        }
        return (t, v) -> fallback;
    }
}