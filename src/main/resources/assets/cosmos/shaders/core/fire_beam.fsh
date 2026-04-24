#version 150

in vec2 vUv;
in vec4 vertexColor;
out vec4 fragColor;

uniform float CosmosTime;




float plasma_hash(vec3 p) {
    p  = fract(p * .1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
}
float plasma_noise3D(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix( plasma_hash(i + vec3(0,0,0)), plasma_hash(i + vec3(1,0,0)), f.x),
                   mix( plasma_hash(i + vec3(0,1,0)), plasma_hash(i + vec3(1,1,0)), f.x), f.y),
               mix(mix( plasma_hash(i + vec3(0,0,1)), plasma_hash(i + vec3(1,0,1)), f.x),
                   mix( plasma_hash(i + vec3(0,1,1)), plasma_hash(i + vec3(1,1,1)), f.x), f.y), f.z);
}
float atomic_fbm3D(vec3 p, int octaves) {
    float v = 0.0, a = 0.5;
    mat3 rot = mat3(0.00, 0.80, 0.60, -0.80, 0.36, -0.48, -0.60, -0.48, 0.64);
    for (int i = 0; i < octaves; i++) {
        v += a * plasma_noise3D(p);
        p = rot * p * 2.0;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec3 node_c_red = vec3(0.6, 0.0, 0.0);
    vec3 node_c_org = vec3(1.0, 0.4, 0.0);
    vec2 node_uv = vUv;

        vec2 node_split_in = node_uv;
        float node_split_x = node_split_in.x;
        float node_split_y = node_split_in.y;
      
    float node_nx = node_split_x * 9.0;
    float node_ny_scale = node_split_y * 2.0;
    float node_time = CosmosTime * 1.0;
    float node_ny_speed = node_time * 6.0;
    float node_ny_scroll = node_ny_scale - node_ny_speed;
    float node_nz_boil = node_time / 4.0;
    vec3 node_n_pack = vec3(node_nx, node_ny_scroll, node_nz_boil);
    float node_fbm = atomic_fbm3D(node_n_pack, int(2.0));
    float node_f_sharp = pow(node_fbm, 2.0);
    float node_f_boost = node_f_sharp * 3.0;
    vec3 node_mix_base = mix(node_c_red, node_c_org, clamp(node_f_boost, 0.0, 1.0));
    vec3 node_c_yel = vec3(1.0, 0.9, 0.1);
    float node_core_mask = node_f_boost - 1.0;
    vec3 node_mix_core = mix(node_mix_base, node_c_yel, clamp(node_core_mask, 0.0, 1.0));
    vec3 node_glow = node_mix_core * 2.5;
    float node_alpha_cut = smoothstep(0.1, 0.8, node_f_boost);
    fragColor = vec4(vec3(node_glow), node_alpha_cut);
}