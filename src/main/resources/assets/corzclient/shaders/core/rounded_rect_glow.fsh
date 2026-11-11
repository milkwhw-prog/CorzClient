#version 330

layout(std140) uniform CorzRoundedRectGlow {
    vec2 uScaledRes;
    vec2 uResolution;
    vec2 uPosition;
    vec2 uSize;
    float uRadius;
    float uGlowSize;
    vec4 uColor;
    float uGlowStrength;
    int uFill;
    vec4 uClip;
    float uClipRadius;
    float uClipFade;
};

out vec4 fragColor;

float roundedBoxSDF(vec2 centerPosition, vec2 halfSize, float r) {
    return length(max(abs(centerPosition) - halfSize + r, 0.0)) - r;
}

float shapeSDF(vec2 centerPosition, vec2 halfSize, float r) {
    vec2 q = abs(centerPosition) - halfSize + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 pos = vec2(uPosition.x, uResolution.y - uPosition.y);

    vec2 halfSize = uSize * 0.5;

    float d = shapeSDF(vec2(gl_FragCoord.x - halfSize.x, gl_FragCoord.y + halfSize.y) - pos, halfSize, uRadius);
    float inside = clamp(0.5 - d, 0.0, 1.0);

    float glow = 0.0;
    if (uGlowSize > 0.0) {
        float t = clamp(1.0 - max(d, 0.0) / uGlowSize, 0.0, 1.0);
        glow = pow(t, 1.5) * uGlowStrength;
    }

    float a = uColor.a * (uFill == 1 ? max(inside, glow) : glow * (1.0 - inside));
    if (uClipFade > 0.0) {
        vec2 p = vec2(gl_FragCoord.x, uResolution.y - gl_FragCoord.y);
        vec2 ch = uClip.zw * 0.5;
        float dc = roundedBoxSDF(p - (uClip.xy + ch), ch, uClipRadius);
        a *= 1.0 - smoothstep(-uClipFade, 0.0, dc);
    }
    fragColor = vec4(uColor.rgb, a);
}
