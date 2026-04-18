package dev.cosmos.api.data;

import com.google.gson.annotations.SerializedName;

public class TrailDefinition {
    public String namespace;
    public String id;
    public String type;
    public Config config;

    public static class Config {
        @SerializedName("history_segments")
        public int historySegments;
        @SerializedName("width_curve")
        public String widthCurve;
        @SerializedName("orbit_offset")
        public String orbitOffset;
        @SerializedName("material_id")
        public String materialId;
    }
}