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

uniform sampler2D Sampler0;

in vec2 vUv;
in vec2 vPos;

out vec4 fragColor;

float boxSDF(vec2 p, vec2 halfSize, float r) {
    vec2 q = abs(p) - halfSize + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

float gradientNoise(vec2 p) {
    return fract(52.9829189 * fract(dot(p, vec2(0.06711056, 0.00583715))));
}

void main() {
    float mode = uShape.y;
    float dither = (gradientNoise(gl_FragCoord.xy) - 0.5) / 255.0;

    if (mode > 1.5 && mode < 2.5) {
        vec2 v = (vUv - 0.5) * vec2(1.0, 0.82);
        float vig = smoothstep(0.22, 0.78, length(v));
        float a = clamp(uMisc.y + uMisc.z * vig + dither * step(1.0e-4, uMisc.z), 0.0, 1.0);
        fragColor = vec4(0.0, 0.0, 0.0, a * uFx.x);
        return;
    }

    vec2 center = (uWin.xy + uWin.zw) * 0.5;
    vec2 halfSize = (uWin.zw - uWin.xy) * 0.5;
    float d = boxSDF(vPos - center, halfSize, uShape.x);
    float upp = max(length(vec2(dFdx(vPos.x), dFdy(vPos.x))), length(vec2(dFdx(vPos.y), dFdy(vPos.y))));
    upp = max(upp, 1.0e-5);
    float dpx = d / upp;

    if (mode > 2.5) {
        float cover = clamp(0.5 - dpx, 0.0, 1.0) * uFx.x;
        if (cover <= 0.0) discard;
        vec3 back = textureLod(Sampler0, gl_FragCoord.xy / max(uUv.xy, vec2(1.0)), 0.0).rgb;
        fragColor = vec4(back * cover, cover);
        return;
    }

    if (mode > 0.5) {
        float spread = max(uMisc.z, 1.0);
        float ds = boxSDF(vPos - center - vec2(0.0, uMisc.w), halfSize, uShape.x);
        float fall = 1.0 - smoothstep(-spread * 0.35, spread, ds);
        float shadow = uMisc.y * fall * fall;
        float halo = uGlow.a * exp(-max(d, 0.0) / max(spread * 0.42, 1.0));
        float outside = clamp(dpx + 0.5, 0.0, 1.0);
        float a = clamp(shadow + dither, 0.0, 1.0) * outside * uFx.x;
        vec3 light = uGlow.rgb * halo * outside * uFx.x;
        if (a <= 0.0 && max(light.r, max(light.g, light.b)) <= 0.0) discard;
        fragColor = vec4(light, a);
        return;
    }

    vec4 c;
    if (uMisc.x > 0.5) {
        c = texelFetch(Sampler0, ivec2(gl_FragCoord.xy), 0);
    } else {
        vec2 dx = dFdx(vUv);
        vec2 dy = dFdy(vUv);
        vec2 ts = vec2(textureSize(Sampler0, 0));
        float footprint = max(length(dx * ts), length(dy * ts));
        float k = clamp((footprint - 1.0) * 2.5, 0.0, 1.0);
        vec2 wx = dx * 0.85;
        vec2 wy = dy * 0.85;
        dx *= k;
        dy *= k;
        c = 0.25 * (textureLod(Sampler0, vUv + 0.125 * dx + 0.375 * dy, 0.0)
                  + textureLod(Sampler0, vUv - 0.375 * dx + 0.125 * dy, 0.0)
                  + textureLod(Sampler0, vUv - 0.125 * dx - 0.375 * dy, 0.0)
                  + textureLod(Sampler0, vUv + 0.375 * dx - 0.125 * dy, 0.0));
        float crisp = 0.55 * clamp((footprint - 1.03) * 5.0, 0.0, 1.0) * (1.0 - clamp((footprint - 1.9) * 2.0, 0.0, 1.0));
        if (crisp > 0.0) {
            vec3 wide = 0.25 * (textureLod(Sampler0, vUv + wx + wy, 0.0).rgb
                              + textureLod(Sampler0, vUv - wx + wy, 0.0).rgb
                              + textureLod(Sampler0, vUv + wx - wy, 0.0).rgb
                              + textureLod(Sampler0, vUv - wx - wy, 0.0).rgb);
            c.rgb = clamp(c.rgb + (c.rgb - wide) * crisp, vec3(0.0), vec3(1.0));
        }
    }

    float inside = clamp(1.0 - dpx, 0.0, 1.0);
    float body = clamp(0.5 - dpx, 0.0, 1.0);
    float edge = min(min(vPos.x - uRect.x, uRect.z - vPos.x), min(vPos.y - uRect.y, uRect.w - vPos.y));
    float feather = clamp(edge / max(uShape.w, 1.0e-3), 0.0, 1.0);
    feather = feather * feather * (3.0 - 2.0 * feather);
    float mask = mix(feather, inside, uShape.z);
    if (uMisc.x > 0.5) mask = 1.0;

    vec2 l = (vPos - uWin.xy) / max(uWin.zw - uWin.xy, vec2(1.0e-3));
    float lit = clamp(0.5 - 0.62 * (l.y - 0.5) - 0.38 * (l.x - 0.5), 0.0, 1.0);
    float rimLine = 1.0 - smoothstep(0.0, 1.35, abs(dpx + 0.75));
    float rim = uFx.y * rimLine * (0.22 + 0.78 * lit * lit);

    float s = l.x + (1.0 - l.y) * 0.42;
    float t = (s - uFx.z) / max(uFx.w, 1.0e-3);
    float shine = uTint.a * (0.75 * exp(-t * t) + 0.35 * exp(-t * t * 0.07)) * body;

    float sheen = uFx.y * 0.16 * pow(clamp(1.0 - l.x * 0.9 - l.y * 0.55, 0.0, 1.0), 2.2) * body;

    vec4 o = c * mask;
    o.rgb += uTint.rgb * (rim + shine + sheen);
    o.a = clamp(o.a + (rim + shine * 0.35) * 0.5, 0.0, 1.0);
    o *= uFx.x;
    if (o.a <= 0.0 && max(o.r, max(o.g, o.b)) <= 0.0) discard;
    fragColor = o;
}
