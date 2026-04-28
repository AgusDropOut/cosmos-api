package dev.cosmos.api.material;

public enum UniformType {
    FLOAT(1), VEC2(2), VEC3(3), VEC4(4), INT(1);

    private final int count;

    UniformType(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public static UniformType fromString(String type) {
        if (type == null) return null;
        switch (type.toLowerCase()) {
            case "float": return FLOAT;
            case "vec2": return VEC2;
            case "vec3": return VEC3;
            case "vec4": return VEC4;
            case "int": return INT;
            default: return null;
        }
    }
}