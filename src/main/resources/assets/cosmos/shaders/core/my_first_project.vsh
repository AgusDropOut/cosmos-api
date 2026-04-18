#version 150
in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float CosmosTime;

out vec2 vUv;
out vec4 vertexColor;



void main() {
    vUv = UV0;
    vertexColor = Color;
    float node_time_1776515970715 = abs(sin((CosmosTime * 1000.0) * 1.0));

        vec3 displacedPosition = Position * node_time_1776515970715 + vec3(0.0);
        gl_Position = ProjMat * ModelViewMat * vec4(displacedPosition, 1.0);
      
}