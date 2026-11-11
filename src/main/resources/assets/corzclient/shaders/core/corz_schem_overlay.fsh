#version 330

layout(std140) uniform CorzGhostData {
    vec4  uTint;
    float uFadeStart;
    float uFadeEnd;
    float uMinAlpha;
    float uAlphaCutoff;
};

in vec4 vertexColor;
in float camDist;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor * vec4(uTint.rgb, 1.0);

    float fade = mix(uMinAlpha, 1.0, smoothstep(uFadeStart, uFadeEnd, camDist));
    color.a *= fade;

    if (color.a < 0.004) {
        discard;
    }

    fragColor = color;
}
