#version 330

layout(std140) uniform CorzSchemModel {
    mat4 uMvp;
    vec4 uParams;
};

in vec3 Position;
in vec2 UV0;
in vec4 Color;

out vec2 texCoord0;
out vec4 vertexColor;

void main() {
    gl_Position = uMvp * vec4(Position, 1.0);
    texCoord0 = UV0;
    vertexColor = Color;
}
