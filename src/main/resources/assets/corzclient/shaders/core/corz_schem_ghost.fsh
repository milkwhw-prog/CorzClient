#version 330

layout(std140) uniform CorzGhostData {
    vec4  uTint;
    float uFadeStart;
    float uFadeEnd;
    float uMinAlpha;
    float uAlphaCutoff;
};

uniform sampler2D Sampler0;

in vec4 vertexColor;
in vec2 texCoord0;
in float camDist;

out vec4 fragColor;

void main() {
    vec4 texel = texture(Sampler0, texCoord0);

    if (texel.a < uAlphaCutoff) {
        discard;
    }

    vec4 color = texel * vertexColor * uTint;

    float fade = mix(uMinAlpha, 1.0, smoothstep(uFadeStart, uFadeEnd, camDist));
    color.a *= fade;

    if (color.a < 0.004) {
        discard;
    }

    fragColor = color;
}
