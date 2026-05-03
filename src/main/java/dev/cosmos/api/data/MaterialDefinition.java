// dev/cosmos/api/data/MaterialDefinition.java
package dev.cosmos.api.data;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class MaterialDefinition {
    public String namespace;
    public String id;
    public String type;
    public Config config;

    public static class Config {
        @SerializedName("render_state")
        public RenderState renderState;

        @SerializedName("exposed_parameters")
        public Map<String, String> exposedParameters;
    }

    // ADD THIS NEW CLASS
    public static class RenderState {
        @SerializedName("blend_mode")
        public String blendMode = "OPAQUE";

        @SerializedName("cull_mode")
        public String cullMode = "BACK";

        @SerializedName("depth_test")
        public String depthTest = "LEQUAL";

        @SerializedName("depth_write")
        public boolean depthWrite = true;

        @SerializedName("alpha_cutoff")
        public float alphaCutoff = 0.0f;
    }
}