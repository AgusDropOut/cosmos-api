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

    public static class RenderState {
        public String transparency;
        @SerializedName("depth_test")
        public String depthTest;
    }
}