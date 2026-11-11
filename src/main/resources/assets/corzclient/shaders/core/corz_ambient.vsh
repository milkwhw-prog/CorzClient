#version 330

layout(std140) uniform CorzAmbient {
    vec2 uScaledRes;
    vec2 uResolution;
    vec4 uRect;
    vec4 uShape;
    vec4 uBlobPos[6];
    vec4 uBlobCol[6];
};

in vec3 Position;

void main() {
    gl_Position = vec4(Position.x / uScaledRes.x * 2.0 - 1.0,
                       1.0 - Position.y / uScaledRes.y * 2.0,
                       0.0, 1.0);
}
