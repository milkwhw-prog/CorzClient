#version 330

#moj_import <minecraft:fog.glsl>

layout(std140) uniform CorzWater {
    vec4 uParams;
    vec4 uSun;
    vec4 uMoon;
    vec4 uZenith;
    vec4 uSunCol;
    vec4 uPhaseA;
    vec4 uPhaseB;
    vec4 uPhaseC;
    vec4 uFade;
};

uniform sampler2D Sampler0;

in vec3 vRel;
in vec2 vWave;
in vec3 vTint;
in vec3 vLight;
in float vSky;
in vec2 vFlow;
in float vDepth;
in float vSphere;
in float vCyl;
in float vFadeIn;

out vec4 fragColor;

const float TAU = 6.28318530718;
const float LATTICE = TAU / 256.0;
const float FLOW_SPAN = 4.0;
const float MEAN_CREST = 0.4658;
const float RIPPLE_A = 3.0;
const float RIPPLE_B = 7.0;
const float GLITTER = 9.0;
const vec3 LUMA = vec3(0.2126, 0.7152, 0.0722);

const vec3 WAVES[10] = vec3[10](
    vec3(11.0, 5.0, 0.16),
    vec3(-7.0, 18.0, 0.14),
    vec3(27.0, -14.0, 0.12),
    vec3(9.0, 47.0, 0.10),
    vec3(-64.0, -38.0, 0.085),
    vec3(-109.0, 40.0, 0.07),
    vec3(77.0, -132.0, 0.06),
    vec3(150.0, 150.0, 0.05),
    vec3(-74.0, -277.0, 0.04),
    vec3(364.0, 64.0, 0.032)
);

float wavePhase(int i) {
    if (i < 4) return uPhaseA[i];
    if (i < 8) return uPhaseB[i - 4];
    return uPhaseC[i - 8];
}

vec4 waveField(vec2 p, float footprint) {
    vec2 g = vec2(0.0);
    float h = 0.0;
    float lost = 0.0;
    for (int i = 0; i < 10; i++) {
        vec2 k = WAVES[i].xy * LATTICE;
        float kl = length(k);
        float lambda = TAU / kl;
        float lod = 1.0 - smoothstep(0.05, 0.24, footprint / lambda);
        float s = WAVES[i].z;
        lost += s * s * (1.0 - lod);
        if (lod <= 0.0) continue;
        float th = dot(k, p) - wavePhase(i);
        float e = exp(sin(th) - 1.0);
        g += (k / kl) * (s * e * cos(th) * lod);
        h += (s / kl) * (e - MEAN_CREST) * lod;
    }
    return vec4(g, lost, h);
}

vec3 toLinear(vec3 c) {
    return pow(max(c, vec3(0.0)), vec3(2.2));
}

vec3 toDisplay(vec3 c) {
    return pow(max(c, vec3(0.0)), vec3(1.0 / 2.2));
}

float hash13(vec3 p) {
    p = fract(p * vec3(443.8975, 397.2973, 491.1871));
    p += dot(p, p.yxz + 19.19);
    return fract((p.x + p.y) * p.z);
}

float hashLattice(vec2 i, float period) {
    i = mod(i, period);
    vec3 p = fract(vec3(i.xyx) * 0.1031);
    p += dot(p, p.yzx + 33.33);
    return fract((p.x + p.y) * p.z);
}

vec2 noiseSlope(vec2 x, float period) {
    vec2 i = floor(x);
    vec2 f = x - i;
    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    vec2 du = 30.0 * f * f * (f * (f - 2.0) + 1.0);
    float a = hashLattice(i, period);
    float b = hashLattice(i + vec2(1.0, 0.0), period);
    float c = hashLattice(i + vec2(0.0, 1.0), period);
    float d = hashLattice(i + vec2(1.0, 1.0), period);
    float k4 = a - b - c + d;
    return du * (vec2(b - a, c - a) + k4 * u.yx);
}

vec3 ripples(vec2 p, float t, float footprint) {
    float la = 1.0 - smoothstep(0.10, 0.40, footprint * RIPPLE_A);
    float lb = 1.0 - smoothstep(0.10, 0.40, footprint * RIPPLE_B);
    vec2 g = vec2(0.0);
    if (la > 0.0) {
        vec2 o = vec2(1.0, 1.0) * (256.0 * RIPPLE_A / 3600.0) * t;
        g += noiseSlope(p * RIPPLE_A + o, 256.0 * RIPPLE_A) * (RIPPLE_A * 0.011 * la);
    }
    if (lb > 0.0) {
        vec2 o = vec2(-1.0, 1.0) * (256.0 * RIPPLE_B / 3600.0) * t;
        g += noiseSlope(p * RIPPLE_B + o, 256.0 * RIPPLE_B) * (RIPPLE_B * 0.0048 * lb);
    }
    float lost = 0.0014 * (1.0 - la) + 0.0012 * (1.0 - lb);
    return vec3(g, lost);
}

float glitter(vec2 p, float t, float footprint) {
    vec2 c = p * GLITTER;
    vec2 ic = floor(c);
    float h1 = hashLattice(ic, 256.0 * GLITTER);
    float h2 = hashLattice(ic + vec2(17.0, 31.0), 256.0 * GLITTER);
    vec2 off = vec2(h1, h2) - 0.5;
    float d = length(c - ic - 0.5 - off * 0.55);
    float dotShape = 1.0 - smoothstep(0.05, 0.26, d);
    float tw = 0.5 + 0.5 * sin(t * (2.1 + 3.4 * h1) + h2 * TAU);
    tw *= tw;
    tw *= tw;
    float lod = 1.0 - smoothstep(0.10, 0.35, footprint * GLITTER);
    return mix(0.035, dotShape * tw, lod);
}

vec2 skyMapUv(vec3 r) {
    vec3 s = vec3(-r.x, max(r.y, 0.0), r.z);
    float sum = abs(s.x) + s.y + abs(s.z);
    vec2 p = s.xz / max(sum, 1.0e-5);
    return vec2(p.x + p.y, p.x - p.y) * 0.5 + 0.5;
}

vec3 vanillaSky(vec3 r) {
    float up = clamp(r.y, 0.0, 1.0);
    return mix(FogColor.rgb, uZenith.rgb, smoothstep(0.0, 0.55, sqrt(up)));
}

vec3 reflectedStars(vec3 r) {
    vec3 p = r * 210.0;
    vec3 ip = floor(p);
    float present = hash13(ip);
    if (present < 0.982) return vec3(0.0);
    vec3 off = vec3(hash13(ip + 3.1), hash13(ip + 7.7), hash13(ip + 11.3)) - 0.5;
    float d = length(fract(p) - 0.5 - off * 0.6);
    float core = 1.0 - smoothstep(0.0, 0.22, d);
    float bright = pow(hash13(ip + 5.5), 3.0);
    return vec3(0.82, 0.88, 1.0) * core * bright * 1.6;
}

vec3 skyward(vec3 v, vec3 n) {
    vec3 r = reflect(v, n);
    r.y = max(r.y, 0.0);
    return normalize(r + vec3(0.0, 1.0e-4, 0.0));
}

void main() {
    vec3 V = normalize(vRel);
    float down = -V.y;
    vec2 fw = fwidth(vWave);
    float footprint = max(fw.x, fw.y);
    float strength = uParams.y;
    float t = uParams.w;

    vec4 w;
    if (dot(vFlow, vFlow) > 0.0004) {
        float p0 = uPhaseC.z;
        float p1 = fract(p0 + 0.5);
        vec2 drift = vFlow * FLOW_SPAN;
        vec4 w0 = waveField(vWave - drift * p0, footprint);
        vec4 w1 = waveField(vWave - drift * p1, footprint);
        float a0 = 1.0 - abs(2.0 * p0 - 1.0);
        w = w0 * a0 + w1 * (1.0 - a0);
        w.xy *= 1.25;
    } else {
        w = waveField(vWave, footprint);
    }
    vec3 rip = ripples(vWave, t, footprint);

    vec2 slope = w.xy * strength;
    vec2 fine = slope + rip.xy * strength;
    float rough = clamp((w.z + rip.z) * strength * 30.0 + 0.03, 0.0, 1.0);
    vec3 N = normalize(vec3(-slope.x, 1.0, -slope.y));
    vec3 Nf = normalize(vec3(-fine.x, 1.0, -fine.y));
    float NdV = clamp(dot(Nf, -V), 0.0, 1.0);
    vec3 R = skyward(V, Nf);
    vec3 Rm = skyward(V, N);

    float refl = uParams.x;
    float fres = 0.02 + 0.98 * pow(1.0 - NdV, 5.0);
    float skyVis = smoothstep(0.30, 0.95, vSky);
    float rain = uFade.w;
    float Rw = clamp(fres * refl * 1.3, 0.0, 0.97) * mix(0.35, 1.0, skyVis) * (1.0 - 0.25 * rain);

    vec3 sky = vanillaSky(R);
    vec3 glint = vec3(0.0);
    float custom = uZenith.a;
    if (custom > 0.0) {
        vec3 mapped = texture(Sampler0, skyMapUv(R)).rgb;
        float luma = dot(mapped, vec3(0.299, 0.587, 0.114));
        glint += mapped * smoothstep(0.55, 1.0, luma) * 0.5 * (1.0 - rough) * custom;
        sky = mix(sky, mapped, custom);
    }
    sky *= mix(0.25, 1.0, skyVis);

    float sharp = mix(2600.0, 240.0, rough);
    float lobe = mix(150.0, 45.0, rough);
    float sparkle = glitter(vWave, t, footprint) * (0.25 + 0.75 * strength);
    float sunF = max(dot(R, uSun.xyz), 0.0);
    float sunM = max(dot(Rm, uSun.xyz), 0.0);
    float sunPath = pow(sunF, lobe) * (0.10 + 7.0 * sparkle);
    float sunSpark = pow(sunF, sharp) * (sharp / 2600.0) * 3.0;
    float sunGlare = pow(sunM, 36.0) * 0.06;
    glint += uSunCol.rgb * (sunPath + sunSpark + sunGlare) * uSun.w * (1.0 - custom);
    float moonF = max(dot(R, uMoon.xyz), 0.0);
    float moonM = max(dot(Rm, uMoon.xyz), 0.0);
    float moonPath = pow(moonF, lobe * 1.2) * (0.16 + 6.0 * sparkle);
    float moonSpark = pow(moonF, sharp) * (sharp / 2600.0) * 2.0;
    float moonGlare = pow(moonM, 48.0) * 0.05;
    glint += vec3(0.74, 0.82, 1.0) * (moonPath + moonSpark + moonGlare) * uMoon.w * (1.0 - custom);
    if (uSunCol.a > 0.01 && custom < 1.0) glint += reflectedStars(R) * uSunCol.a * (1.0 - custom);

    float clarity = uParams.z;
    float sigma = mix(0.55, 0.03, clarity);
    float cosT = sqrt(max(1.0 - (1.0 - NdV * NdV) / 1.7689, 0.09));
    float path = vDepth * mix(1.0 / cosT, 1.0 / max(down, 0.08), 0.5);
    float T = exp(-sigma * path * (1.0 + 0.35 * path));

    float crest = clamp(w.w * strength * 1.8, -1.0, 1.0);
    vec3 lightLin = toLinear(vLight);
    float lightY = dot(lightLin, LUMA);
    vec3 light = mix(vec3(lightY), lightLin, 0.25) * mix(0.42, 1.0, smoothstep(0.05, 0.45, lightY));
    vec3 tint = mix(vec3(dot(vTint, LUMA)), vTint, 0.85);
    vec3 bodyLin = light * toLinear(tint) * 0.17 * (1.0 + 0.30 * crest);

    float a = 1.0 - (1.0 - Rw) * T;
    vec3 L = toLinear(sky) * Rw + bodyLin * ((1.0 - Rw) * (1.0 - T))
           + toLinear(glint * (skyVis * (1.0 - rain) * refl));
    vec3 rgb = toDisplay(L / max(a, 1.0e-3)) * a;
    rgb += (hash13(vec3(gl_FragCoord.xy, 5.0)) - 0.5) / 255.0 * a;

    float fogV = total_fog_value(vSphere, vCyl, FogEnvironmentalStart, FogEnvironmentalEnd,
                                 FogRenderDistanceStart, FogRenderDistanceEnd) * FogColor.a;
    float edge = 1.0 - smoothstep(uFade.x, uFade.y, length(vRel.xz));
    float face = smoothstep(0.0, 0.004, down);
    float k = (1.0 - fogV) * edge * face * uFade.z * vFadeIn;

    fragColor = vec4(max(rgb, vec3(0.0)), a) * k;
}
