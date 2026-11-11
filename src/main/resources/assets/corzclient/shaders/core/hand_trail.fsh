#version 150

in vec2 texCoord;
in vec2 texelSize;

out vec4 fragColor;

uniform sampler2D PrevTrailSampler;
uniform sampler2D SceneSampler;
uniform sampler2D MaskSampler;

layout(std140) uniform HandTrailData {
    vec4 resolution;
    vec4 glowColor;
    vec4 settings;
    vec4 settings2;
    vec4 reserved;
};

float sampleMask(vec2 uv) {
    return texture(MaskSampler, clamp(uv, vec2(0.0), vec2(1.0))).r;
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));

    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;

    for (int i = 0; i < 4; i++) {
        v += noise(p) * a;
        p = p * 2.04 + vec2(19.17, 7.31);
        a *= 0.5;
    }

    return v;
}

vec3 vividColor(vec3 color) {
    float peak = max(max(color.r, color.g), color.b);
    if (peak < 0.05) return glowColor.rgb;

    vec3 vivid = color / max(peak, 0.20);
    return clamp(mix(color, vivid, 0.36) * 1.05, 0.0, 1.0);
}

void addSource(vec2 sourceUv, float weight, inout vec3 color, inout float alpha) {
    float mask = sampleMask(sourceUv);
    if (mask <= 0.001) return;

    vec3 sceneColor = texture(SceneSampler, clamp(sourceUv, vec2(0.0), vec2(1.0))).rgb;
    float sourceWeight = mask * weight;
    color += vividColor(sceneColor) * sourceWeight;
    alpha += sourceWeight;
}

void main() {
    float topDistance = (resolution.y - gl_FragCoord.y) / max(resolution.y, 1.0);
    float topEdgeFade = smoothstep(0.012, 0.085, topDistance);
    float time = resolution.z;
    float intensity = clamp(resolution.w, 0.0, 1.5);
    float speed = clamp(settings.y, 0.35, 2.4);
    float length = clamp(settings.z, 0.1, 1.0);
    float softness = clamp(settings.w, 0.55, 2.0);
    float blurRadius = clamp(settings2.x, 0.45, 2.5);
    float smoke = clamp(settings2.y, 0.0, 0.8);
    float activity = clamp(settings2.z, 0.0, 1.0);
    float fadeSetting = clamp(settings2.w, 0.55, 0.96);
    float slash = clamp(reserved.x, 0.0, 1.0);
    float slashDirection = reserved.y < 0.0 ? -1.0 : 1.0;
    float slashReturn = smoothstep(0.20, 0.78, 1.0 - slash);
    vec2 slashAxis = normalize(mix(vec2(0.88 * slashDirection, -0.48),
                                   vec2(-0.58 * slashDirection, 0.24),
                                   slashReturn));
    vec2 slashNormal = vec2(-slashAxis.y, slashAxis.x);

    float n = fbm(texCoord * vec2(34.0, 29.0) + vec2(time * 0.13 * speed, -time * 0.10 * speed));
    vec2 curl = vec2(
        fbm(texCoord * 28.0 + vec2(time * 0.20 * speed, 3.1)),
        fbm(texCoord * 31.0 + vec2(8.4, -time * 0.17 * speed))
    ) - 0.5;

    vec2 prevUv = texCoord + curl * texelSize * (1.9 + blurRadius * 1.35);
    prevUv += slashAxis * texelSize * slash * (7.0 + blurRadius * 7.0);
    vec4 prev = texture(PrevTrailSampler, clamp(prevUv, vec2(0.0), vec2(1.0)));

    float fade = mix(max(0.52, fadeSetting - 0.13), fadeSetting, min(softness, 1.8) / 1.8);
    fade = mix(fade - activity * 0.025, 0.91, slash * 0.35);
    prev.rgb *= fade;
    prev.a *= fade;
    prev *= topEdgeFade;
    if (prev.a < 0.006) {
        prev = vec4(0.0);
    }

    vec3 sourceColor = vec3(0.0);
    float sourceAlpha = 0.0;

    float spread = 2.2 + blurRadius * 2.85 + length * 7.5;
    for (int i = 0; i < 18; i++) {
        float fi = float(i);
        float angle = fi * 2.399963 + time * (0.18 + speed * 0.09) + n * 2.6;
        vec2 dir = vec2(cos(angle), sin(angle));
        float dist = 1.1 + fi * spread / 7.2;
        vec2 sourceUv = texCoord - dir * texelSize * dist - curl * texelSize * (2.0 + fi * 0.26);
        float weight = (18.0 - fi) / 18.0;
        addSource(sourceUv, weight, sourceColor, sourceAlpha);
    }

    if (slash > 0.001) {
        float slashLength = 7.5 + blurRadius * 6.0 + length * 12.0;
        for (int i = 0; i < 26; i++) {
            float t = float(i) / 25.0;
            float arc = sin(t * 3.1415927);
            float curve = t - 0.45;
            vec2 slashOffset = slashAxis * texelSize * ((t - 0.18) * slashLength * 8.0);
            slashOffset += slashNormal * texelSize * ((curve * curve * 30.0 - 4.5) * arc);
            slashOffset += curl * texelSize * (1.4 + t * 2.0);
            vec2 sourceUv = texCoord - slashOffset * (0.72 + slash * 0.42);
            float weight = slash * arc * (1.25 - t * 0.55) * (0.85 + activity * 0.45);
            addSource(sourceUv, weight, sourceColor, sourceAlpha);
        }
    }

    float body = sampleMask(texCoord);
    float outside = 1.0 - body * 0.97;
    float wisps = mix(0.45, 1.16, fbm(texCoord * 104.0 + vec2(-time * 0.34 * speed, time * 0.23 * speed)));

    float newAlpha = smoothstep(0.028, 0.22, sourceAlpha / 4.2);
    newAlpha *= outside * wisps;
    newAlpha *= (0.20 + intensity * 0.22 + smoke * 0.30 + activity * 0.08 + slash * 0.18);
    newAlpha *= topEdgeFade;

    vec3 newColor = sourceAlpha > 0.001 ? sourceColor / sourceAlpha : glowColor.rgb;
    vec3 slashColor = clamp(mix(newColor, glowColor.rgb, 0.24) * (1.0 + slash * 0.35), 0.0, 1.0);
    newColor = mix(newColor, slashColor, slash * 0.55);
    vec3 outColor = mix(prev.rgb, newColor, clamp(newAlpha * (2.9 + slash * 1.4), 0.0, 0.86));
    float outAlpha = clamp(prev.a + newAlpha * (1.0 - prev.a), 0.0, 0.64 + slash * 0.10);
    if (outAlpha < 0.005) {
        outColor = vec3(0.0);
        outAlpha = 0.0;
    }

    fragColor = vec4(outColor, outAlpha);
}
