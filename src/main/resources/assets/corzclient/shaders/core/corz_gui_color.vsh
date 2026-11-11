#version 330

layout(std140) uniform CorzGui {
    vec2 uScaledRes;
};

in vec3 Position;
in vec4 Color;

out vec4 vColor;

void main() {
    gl_Position = vec4(Position.x / uScaledRes.x * 2.0 - 1.0,
                       1.0 - Position.y / uScaledRes.y * 2.0,
                       0.0, 1.0);
    vColor = Color;
}
