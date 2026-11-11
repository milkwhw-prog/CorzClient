#version 330

layout(std140) uniform CorzFlame {
    vec2 uScaledRes;
    vec2 uResolution;
    vec4 uPrimary;
    vec4 uSecondary;
    vec4 uParamsA;
    vec4 uParamsB;
    vec4 uParamsC;
};

in vec3 Position;

void main() {
    gl_Position = vec4(Position.x / uScaledRes.x * 2.0 - 1.0,
                       1.0 - Position.y / uScaledRes.y * 2.0,
                       0.0, 1.0);
}
