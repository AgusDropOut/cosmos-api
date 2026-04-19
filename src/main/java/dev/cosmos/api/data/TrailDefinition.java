package dev.cosmos.api.data;

import com.google.gson.annotations.SerializedName;
import dev.cosmos.Cosmos;
import dev.cosmos.util.math.CosmosExpressionParser;
import dev.cosmos.util.math.MathExpression;

public class TrailDefinition {
    public String namespace;
    public String id;
    public String type;
    public Config config;
    public transient MathExpression compiledWidth;

    public static class Config {
        @SerializedName("history_segments")
        public int historySegments;
        @SerializedName("width_curve")
        public String widthCurve;
        @SerializedName("orbit_offset")
        public String orbitOffset;
        @SerializedName("material_id")
        public String materialId;
        public RenderState render_state;
    }

    public static class RenderState {
        public String transparency; // e.g., "ADDITIVE", "TRANSLUCENT", "OPAQUE"
        public String depth_test;   // e.g., "LEQUAL", "NONE"
    }

    public void compileExpressions() {
        if (this.config != null && this.config.widthCurve != null && !this.config.widthCurve.isEmpty()) {
            try {
                this.compiledWidth = CosmosExpressionParser.parse(this.config.widthCurve);
            } catch (Exception e) {
                Cosmos.LOGGER.error("Cosmos API: Failed to compile width_curve: '{}'", this.config.widthCurve, e);
                this.compiledWidth = (t, v) -> 0.4f; // Fallback
            }
        } else {
            this.compiledWidth = (t, v) -> 0.4f; // Default
        }
    }



}