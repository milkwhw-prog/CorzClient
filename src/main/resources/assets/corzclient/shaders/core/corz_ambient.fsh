#version 330

layout(std140) uniform CorzAmbient {
    vec2 uScaledRes;
    vec2 uResolution;
    vec4 uRect;
    vec4 uShape;
    vec4 uBlobPos[6];
    vec4 uBlobCol[6];
};

out vec4 fragColor;

float boxSDF(vec2 p, vec2 halfSize, float r) {
    vec2 q = abs(p) - halfSize + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

float gradientNoise(vec2 p) {
    return fract(52.9829189 * fract(dot(p, vec2(0.06711056, 0.00583715))));
}

void main() {
    vec2 p = vec2(gl_FragCoord.x, uResolution.y - gl_FragCoord.y);

    vec2 halfSize = uRect.zw * 0.5;
    float d = boxSDF(p - (uRect.xy + halfSize), halfSize, uShape.x);
    float mask = 1.0 - smoothstep(-max(uShape.y, 1.0), 0.0, d);

    vec3 sum = vec3(0.0);
    float total = 0.0;
    float keep = 1.0;
    for (int i = 0; i < 6; i++) {
        vec4 blob = uBlobPos[i];
        vec2 v = (p - blob.xy) / max(blob.z, 1.0);
        float g = blob.w * exp(-dot(v, v));
        sum += uBlobCol[i].rgb * g;
        total += g;
        keep *= 1.0 - clamp(g, 0.0, 1.0);
    }

    float a = clamp((1.0 - keep) * mask * uShape.z, 0.0, 1.0);
    vec3 col = pow(max(sum / max(total, 1.0e-6), vec3(0.0)), vec3(1.0 / 2.2));

    float n = (gradientNoise(gl_FragCoord.xy) - 0.5) * uShape.w / 255.0;
    n *= step(1.0e-4, a);

    fragColor = vec4(col * a + vec3(n), a);
}
