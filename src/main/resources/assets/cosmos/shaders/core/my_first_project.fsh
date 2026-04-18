#version 150

in vec2 vUv;
in vec4 vertexColor;
out vec4 fragColor;

uniform float CosmosTime;

float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);
}

void main() {
    vec3 node_color_1776126026265 = vec3(1.000, 0.040, 0.000);
    float node_noise_1776126275599 = random(vUv * 10.0);
    vec3 node_multiply_1776126285421 = node_color_1776126026265 * node_noise_1776126275599;
    fragColor = vec4(vec3(node_multiply_1776126285421), 1.0);
}