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

    // --- COMPILED EXPRESSIONS ---
    public transient MathExpression compiledWidth;
    public transient MathExpression compiledOffsetX;
    public transient MathExpression compiledOffsetY;
    public transient MathExpression compiledOffsetZ;

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
        //  Compile Width
        compileWidth();
        //  Compile Orbit Offset
        compileOrbitOffset();
    }

    private void compileWidth() {
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

    private void compileOrbitOffset() {
        this.compiledOffsetX = (t, v) -> 0.0f;
        this.compiledOffsetY = (t, v) -> 0.0f;
        this.compiledOffsetZ = (t, v) -> 0.0f;

        if (this.config != null && this.config.orbitOffset != null && !this.config.orbitOffset.isEmpty()) {
            String rawOrbit = this.config.orbitOffset.trim();


            if (rawOrbit.startsWith("vec3(") && rawOrbit.endsWith(")")) {
                rawOrbit = rawOrbit.substring(5, rawOrbit.length() - 1);
            }

            // TODO: handle nested expression like pow(a,b)
            String[] parts = splitVectorArguments(rawOrbit);

            if (parts.length == 3) {
                try { this.compiledOffsetX = CosmosExpressionParser.parse(parts[0]); }
                catch (Exception e) { Cosmos.LOGGER.error("Failed to parse offset X: {}", parts[0]); }

                try { this.compiledOffsetY = CosmosExpressionParser.parse(parts[1]); }
                catch (Exception e) { Cosmos.LOGGER.error("Failed to parse offset Y: {}", parts[1]); }

                try { this.compiledOffsetZ = CosmosExpressionParser.parse(parts[2]); }
                catch (Exception e) { Cosmos.LOGGER.error("Failed to parse offset Z: {}", parts[2]); }
            } else {
                Cosmos.LOGGER.warn("Cosmos API: Invalid orbit_offset format (expected 3 arguments): '{}'", rawOrbit);
            }
        }
    }


    /**
     * Safely splits arguments separated by commas, ignoring commas inside nested parentheses.
     * e.g., "max(1.0, 2.0), sin(t), 0.0" -> ["max(1.0, 2.0)", "sin(t)", "0.0"]
     */
    private String[] splitVectorArguments(String input) {
        java.util.List<String> result = new java.util.ArrayList<>();
        int depth = 0;
        int startIndex = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == ',' && depth == 0) {
                result.add(input.substring(startIndex, i).trim());
                startIndex = i + 1;
            }
        }
        result.add(input.substring(startIndex).trim());
        return result.toArray(new String[0]);
    }
}