#version 330

layout(std140) uniform CorzRoundedRect {
    vec2 uScaledRes;
    vec2 uResolution;
    vec2 uPosition;
    vec2 uSize;
    float uRadius;
    float uStroke;
    vec4 uColor;
};

in vec3 Position;

void main() {
    gl_Position = vec4(Position.x / uScaledRes.x * 2.0 - 1.0,
                       1.0 - Position.y / uScaledRes.y * 2.0,
                       0.0, 1.0);
}
