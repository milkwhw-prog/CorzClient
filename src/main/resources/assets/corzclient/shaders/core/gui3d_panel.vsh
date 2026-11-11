#version 330

layout(std140) uniform CorzGui3dPanel {
    mat4 uMvp;
    vec4 uRect;
    vec4 uUv;
    vec4 uWin;
    vec4 uFx;
    vec4 uTint;
    vec4 uShape;
    vec4 uMisc;
    vec4 uGlow;
};

out vec2 vUv;
out vec2 vPos;

void main() {
    vec2 corners[6] = vec2[](
        vec2(0.0, 0.0), vec2(0.0, 1.0), vec2(1.0, 1.0),
        vec2(0.0, 0.0), vec2(1.0, 1.0), vec2(1.0, 0.0)
    );
    vec2 q = corners[gl_VertexID];
    vec2 p = mix(uRect.xy, uRect.zw, q);
    gl_Position = uMvp * vec4(p, 0.0, 1.0);
    vUv = mix(uUv.xy, uUv.zw, q);
    vPos = p;
}
