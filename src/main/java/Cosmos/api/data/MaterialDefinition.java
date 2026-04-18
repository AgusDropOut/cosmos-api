package cosmos.api.data;

import com.google.gson.annotations.SerializedName;

public class MaterialDefinition {
    public String namespace;
    public String id;
    public String type;
    public Config config;

    public static class Config {
        @SerializedName("render_state")
        public RenderState renderState;
    }

    public static class RenderState {
        public String transparency;
        @SerializedName("depth_test")
        public String depthTest;
    }
}