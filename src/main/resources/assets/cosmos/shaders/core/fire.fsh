#version 150

in vec2 vUv;
in vec4 vertexColor;
out vec4 fragColor;

uniform float CosmosTime;




float hash12(vec2 p) {
    vec3 p3  = fract(vec3(p.xyx) * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}
float noise2D(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash12(i), hash12(i + vec2(1.0, 0.0)), f.x),
               mix(hash12(i + vec2(0.0, 1.0)), hash12(i + vec2(1.0, 1.0)), f.x), f.y);
}
float fbm2D(vec2 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += a * noise2D(p); p *= 2.0; a *= 0.5;
    }
    return v;
}


float hash(vec3 p) {
    p  = fract(p * .1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
}
float noise3D(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix( hash(i + vec3(0,0,0)), hash(i + vec3(1,0,0)), f.x),
                   mix( hash(i + vec3(0,1,0)), hash(i + vec3(1,1,0)), f.x), f.y),
               mix(mix( hash(i + vec3(0,0,1)), hash(i + vec3(1,0,1)), f.x),
                   mix( hash(i + vec3(0,1,1)), hash(i + vec3(1,1,1)), f.x), f.y), f.z);
}
float ridge3D(vec3 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        float n = 1.0 - abs(noise3D(p) - 0.5) * 2.0;
        v += a * n;
        p *= 2.0;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec3 node_c_blk = vec3(1.000, 0.000, 0.000);
    vec3 node_c_red = vec3(0.600, 0.000, 0.000);
    vec2 node_uv = vUv;

    vec2 node_split_in = node_uv;
    float node_split_x = node_split_in.x;
    float node_split_y = node_split_in.y;
    float node_uv_x_05 = node_split_x - 0.5;
    float node_uv_y_25 = node_split_y * 2.5;
    float node_time = (CosmosTime * 1000.0) * 1.0;
    float node_t_5 = node_time * 5.0;
    float node_sub_1 = node_uv_y_25 - node_t_5;
    float node_sin_1 = sin(node_sub_1);
    float node_mac1 = node_sin_1 * 0.15;
    float node_uv_y_15 = node_split_y * 1.5;
    float node_t_3 = node_time * 3.0;
    float node_add_1 = node_uv_y_15 + node_t_3;
    float node_cos_1 = cos(node_add_1);
    float node_mac2 = node_cos_1 * 0.1;
    float node_macro = node_mac1 + node_mac2;
    float node_lx_1 = node_uv_x_05 + node_macro;
    float node_uv_y_5 = node_split_y * 5.0;
    float node_t_15 = node_time * 15.0;
    vec2 node_pack_1 = vec2(node_uv_y_5, node_t_15);
    float node_fbm_1 = fbm2D(node_pack_1 * 1.0);
    float node_sub_2 = node_fbm_1 - 0.5;
    float node_micro = node_sub_2 * 0.2;
    float node_local_x = node_lx_1 + node_micro;
    float node_local_dist = abs(node_local_x);
    float node_bc_1 = node_local_dist * -22.0;
    float node_beam_core = exp(node_bc_1);
    float node_np_x = node_local_x * 7.0;
    float node_np_y = node_uv_y_15 - node_t_15;
    float node_np_z = node_time * 1.5;
    vec3 node_pack_2 = vec3(node_np_x, node_np_y, node_np_z);
    float node_ridge = ridge3D(node_pack_2);
    float node_ridge_pow = pow(node_ridge, 2.5);
    float node_int_1 = node_beam_core * node_ridge_pow;
    float node_int_2 = node_int_1 * 4.5;
    float node_en_x = node_local_x * 3.0;
    float node_t_8 = node_time * 8.0;
    float node_en_y = node_uv_y_15 - node_t_8;
    vec2 node_pack_3 = vec2(node_en_x, node_en_y);
    float node_fbm_2 = fbm2D(node_pack_3 * 1.0);
    float node_erosion = smoothstep(0.35, 0.8, node_fbm_2);
    float node_ero_25 = node_erosion * 2.5;
    float node_int_3 = node_int_2 - node_ero_25;
    float node_int_4 = max(node_int_3, 0.0);
    float node_vy_abs = abs(node_split_y);
    float node_v_fade = smoothstep(1.0, 0.6, node_vy_abs);
    float node_int_fin = node_int_4 * node_v_fade;
    float node_t1 = smoothstep(0.0, 0.1, node_int_fin);
    vec3 node_m1 = mix(node_c_blk, node_c_red, clamp(node_t1, 0.0, 1.0));
    vec3 node_c_org = vec3(0.090, 0.330, 0.000);
    float node_t2 = smoothstep(0.1, 0.3, node_int_fin);
    vec3 node_m2 = mix(node_m1, node_c_org, clamp(node_t2, 0.0, 1.0));
    vec3 node_c_yel = vec3(0.120, 0.800, 0.000);
    float node_t3 = smoothstep(0.3, 0.7, node_int_fin);
    vec3 node_m3 = mix(node_m2, node_c_yel, clamp(node_t3, 0.0, 1.0));
    vec3 node_c_wht = vec3(0.000, 1.000, 0.900);
    float node_t4 = smoothstep(0.7, 1.0, node_int_fin);
    vec3 node_m4 = mix(node_m3, node_c_wht, clamp(node_t4, 0.0, 1.0));
    vec3 node_fe_1 = node_m4 * 3.5;
    float node_bm_1 = node_local_dist * -6.0;
    float node_bm_2 = exp(node_bm_1);
    float node_bm_5 = node_bm_2 * node_v_fade;
    float node_bm_3 = node_erosion * 0.5;
    float node_bm_4 = 1.0 - node_bm_3;
    float node_bm_fin = node_bm_5 * node_bm_4;
    vec3 node_bc_col_1 = node_c_org + node_bm_fin;
    vec3 node_bc_col_2 = node_bc_col_1 * 0.7;
    vec3 node_fe_fin = node_fe_1 + node_bc_col_2;
    float node_al_1 = smoothstep(0.01, 0.15, node_int_fin);
    float node_al_2 = node_bm_fin * 0.4;
    float node_al_3 = node_al_1 + node_al_2;
    float node_al_fin = min(node_al_3, 1.0);
    fragColor = vec4(vec3(node_fe_fin), node_al_fin);
}