#version 330

layout(std140) uniform CorzRoundedTex {
    vec2 uScaledRes;
    vec2 uResolution;
    vec2 uPosition;
    vec2 uSize;
    float uRadius;
};

in vec3 Position;
in vec2 UV0;
in vec4 Color;

out vec2 texCoord0;
out vec4 vertexColor;

void main() {
    gl_Position = vec4(Position.x / uScaledRes.x * 2.0 - 1.0,
                       1.0 - Position.y / uScaledRes.y * 2.0,
                       0.0, 1.0);
    texCoord0 = UV0;
    vertexColor = Color;
}
