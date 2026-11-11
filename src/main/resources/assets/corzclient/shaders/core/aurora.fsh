#version 150

in vec2 texCoord;
out vec4 fragColor;

layout(std140) uniform ShaderFogData {
    vec2 uResolution;
    vec2 uCameraDir;
    vec3 uColor;
    float uTime;
    float uAlpha;
    float uSpeed;
    float uScale;
    float uIntensity;
    float uFov;
    float uSaturnSize;
    float uRingTilt;
    float uGalaxy;
    vec4 uSkyMap;
    mat4 uInvViewProj;
};

mat3 rotX(float a) {
    float c = cos(a), s = sin(a);
    return mat3(1.0, 0.0, 0.0, 0.0, c, s, 0.0, -s, c);
}

mat3 rotY(float a) {
    float c = cos(a), s = sin(a);
    return mat3(c, 0.0, s, 0.0, 1.0, 0.0, -s, 0.0, c);
}

vec3 corzSkyRay(vec3 legacy) {
    vec2 uv = gl_FragCoord.xy / uResolution;
    if (uSkyMap.x > 0.5) {
        vec2 o = uv * 2.0 - 1.0;
        vec2 p = vec2(o.x + o.y, o.x - o.y) * 0.5;
        return normalize(vec3(p.x, max(1.0 - abs(p.x) - abs(p.y), 0.0), p.y));
    }
    if (uSkyMap.y > 0.5) {
        vec4 w = uInvViewProj * vec4(uv * 2.0 - 1.0, 1.0, 1.0);
        vec3 d = normalize(w.xyz / w.w);
        return vec3(-d.x, d.y, d.z);
    }
    return legacy;
}

const float SPACING = 2.7;
const int LAYERS = 32;
const float BASE_ALT = 1.0;
const float DEPTH_ALT = 1.35;
const mat2 CURTAIN_ROT = mat2(0.935897, 0.352274, -0.352274, 0.935897);

float hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

float hash13(vec3 p) {
    p = fract(p * vec3(443.8975, 397.2973, 491.1871));
    p += dot(p, p.yxz + 19.19);
    return fract((p.x + p.y) * p.z);
}

vec3 hash33(vec3 p) {
    return vec3(hash13(p), hash13(p + 19.7), hash13(p + 37.3));
}

float noise1(float x) {
    float i = floor(x);
    float f = fract(x);
    float u = f * f * (3.0 - 2.0 * f);
    return mix(hash11(i), hash11(i + 1.0), u);
}

vec3 aces(vec3 x) {
    const float a = 2.51, b = 0.03, c = 2.43, d = 0.59, e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

vec3 starLayer(vec3 rd, float scale, float thresh, float rad, float t) {
    vec3 p = rd * scale;
    vec3 ip = floor(p);
    vec3 fp = fract(p) - 0.5;
    float present = hash13(ip * 1.07 + 0.5);
    if (present <= thresh) return vec3(0.0);
    vec3 off = (hash33(ip + 2.3) - 0.5) * 0.7;
    float d = length(fp - off);
    float bright = pow(hash13(ip + 5.5), 3.5) * 1.7;
    float core = 1.0 - smoothstep(0.0, rad, d);
    float halo = (1.0 - smoothstep(0.0, rad * 4.0, d)) * 0.18;
    float tw = 0.62 + 0.38 * sin(t * (1.3 + present * 2.1) + present * 41.0);
    float temp = hash13(ip + 13.7);
    vec3 col = mix(vec3(0.66, 0.78, 1.0), vec3(1.0, 0.95, 0.88), smoothstep(0.1, 0.6, temp));
    col = mix(col, vec3(1.0, 0.80, 0.58), smoothstep(0.75, 1.0, temp));
    return col * (core + halo) * bright * tw;
}

vec3 auroraColor(float f) {
    vec3 fringe = vec3(0.62, 0.20, 0.72);
    vec3 green = vec3(0.16, 1.00, 0.42);
    vec3 teal = vec3(0.04, 0.82, 0.74);
    vec3 violet = mix(vec3(0.40, 0.22, 0.98), uColor, 0.28);
    vec3 crown = vec3(0.66, 0.12, 0.42);
    vec3 c = mix(fringe, green, smoothstep(0.0, 0.05, f));
    c = mix(c, teal, smoothstep(0.16, 0.40, f));
    c = mix(c, violet, smoothstep(0.40, 0.70, f));
    return mix(c, crown, smoothstep(0.74, 1.0, f));
}

float rayLod(float fp, float freq) {
    return 1.0 - smoothstep(0.28, 0.75, fp * freq);
}

float curtain(vec2 q, float k, float t, float stepW, float fp, float fpFine, float f, out float glow) {
    float seed = k * 17.13 + 3.7;
    float x = q.x;
    float fold = 0.80 * sin(x * 0.21 + t * 0.050 + seed)
               + 0.28 * sin(x * 0.57 - t * 0.083 + seed * 1.9)
               + 0.84 * (noise1(x * 0.33 + t * 0.040 + seed * 5.3) - 0.5);
    float d = q.y - k * SPACING - fold;
    float w = 0.034 + 0.026 * noise1(x * 0.9 + seed * 2.1 + t * 0.07);
    float we = max(w, stepW);
    float sheet = exp(-(d * d) / (we * we)) * (w / we);
    glow = exp(-(d * d) * 2.6);
    if (sheet < 0.002) return 0.0;
    float l1 = rayLod(fp, 8.0);
    float l2 = rayLod(fpFine, 21.0);
    float l3 = rayLod(fpFine, 47.0);
    float streak = 0.53;
    if (l1 > 0.0) streak = mix(0.53, smoothstep(0.30, 0.84, noise1(x * 8.0 + t * 0.35 + seed * 3.3)), l1);
    float fine2 = 0.62;
    if (l2 > 0.0) fine2 = mix(0.62, 0.30 + 0.70 * noise1(x * 21.0 - t * 0.90 + seed * 7.7), l2);
    float fine3 = 0.80;
    if (l3 > 0.0) fine3 = mix(0.80, 0.52 + 0.48 * noise1(x * 47.0 + t * 1.60 + seed * 1.3), l3);
    float rays = 0.03 + 1.30 * streak * streak * fine2 * fine3;
    float surge = 0.07 + 0.93 * smoothstep(0.28, 0.86, noise1(x * 0.16 - t * 0.10 + seed * 4.1));
    float presence = smoothstep(0.22, 0.64, hash11(k * 3.17 + 0.71));
    float tall = smoothstep(0.15, 0.85, noise1(x * 2.3 + seed * 2.7 - t * 0.12));
    float prof = smoothstep(0.0, 0.03, f) * exp(-f * mix(5.2, 2.3, tall));
    return sheet * rays * surge * presence * prof;
}

vec3 aurora(vec3 rd, float t, float dither, float sk, float fpx) {
    float el = rd.y;
    if (el < 0.012) return vec3(0.0);
    vec2 dir = rd.xz / el;
    mat2 rot = CURTAIN_ROT;
    vec2 qd = rot * dir;
    float stepBase = mix(abs(qd.y), length(qd), 0.3) * DEPTH_ALT / float(LAYERS) * sk * 0.9;
    vec3 acc = vec3(0.0);
    vec3 haze = vec3(0.0);
    for (int i = 0; i < LAYERS; i++) {
        float u = (float(i) + dither) / float(LAYERS);
        float su = sqrt(u);
        float f = u * su;
        float span = 1.5 * su;
        float alt = BASE_ALT + f * DEPTH_ALT;
        vec2 q = rot * (dir * alt * sk) + vec2(t * 0.006, t * 0.011);
        float cell = q.y / SPACING;
        float k0 = floor(cell + 0.5);
        float k1 = k0 + (cell - k0 > 0.0 ? 1.0 : -1.0);
        float g0, g1;
        float stepW = stepBase * max(span, 0.2);
        float fp = fpx * alt * sk;
        float stepX = abs(qd.x) * DEPTH_ALT * span / float(LAYERS) * sk;
        float fpBroad = max(fp, stepX * 0.6);
        float fpFine = max(fp, stepX);
        float e = curtain(q, k0, t, stepW, fpBroad, fpFine, f, g0) + curtain(q, k1, t, stepW, fpBroad, fpFine, f, g1);
        vec3 c = auroraColor(f);
        acc += c * (e * span);
        haze += c * ((g0 + g1) * exp(-f * 2.2) * span);
    }
    float horizonFade = smoothstep(0.012, 0.16, el);
    return (acc * 3.0 + haze * 0.009) * horizonFade;
}

vec3 nightSky(vec3 rd, float t) {
    float el = rd.y;
    float up = clamp(el, 0.0, 1.0);
    vec3 zenith = vec3(0.006, 0.010, 0.030);
    vec3 horizon = vec3(0.030, 0.052, 0.078);
    vec3 col = mix(horizon, zenith, pow(up, 0.55));
    float band = exp(-abs(el - 0.035) / 0.055);
    col += vec3(0.035, 0.090, 0.070) * band * 0.55;
    col += vec3(0.020, 0.030, 0.060) * exp(-max(el, 0.0) / 0.22) * 0.5;
    if (el < 0.0) {
        col = mix(horizon * 0.55, vec3(0.004, 0.006, 0.010), smoothstep(0.0, 0.35, -el));
        return col;
    }
    vec3 st = starLayer(rd, 120.0, 0.955, 0.060, t) + starLayer(rd, 280.0, 0.90, 0.055, t * 1.2) * 0.8
            + starLayer(rd, 560.0, 0.86, 0.050, t * 1.4) * 0.55;
    st *= smoothstep(0.0, 0.10, el);
    float milky = pow(1.0 - abs(dot(rd, normalize(vec3(0.42, 1.0, -0.30)))), 6.0);
    col += vec3(0.020, 0.024, 0.036) * milky;
    return col + st;
}

void main() {
    vec2 uv = gl_FragCoord.xy / uResolution;
    vec2 sp = uv * 2.0 - 1.0;
    float aspect = uResolution.x / max(uResolution.y, 1.0);

    float tanV = tan(radians(uFov) * 0.5);
    vec3 rayV = normalize(vec3(sp.x * tanV * aspect, sp.y * tanV, 1.0));
    vec3 rd = normalize(corzSkyRay(rotY(uCameraDir.x) * rotX(uCameraDir.y) * rayV));

    float t = uTime * uSpeed;
    float energy = clamp(uIntensity / 0.01, 0.2, 6.0);
    float sk = clamp(uScale / 5.0, 0.3, 3.0);
    float dither = hash13(vec3(gl_FragCoord.xy, 19.0));

    vec2 lodQ = CURTAIN_ROT * (rd.xz / max(rd.y, 0.03));
    float fpx = min(fwidth(lodQ.x), 1.0);

    vec3 col = nightSky(rd, t);
    vec3 a = vec3(1.0) - exp(-aurora(rd, t, dither, sk, fpx) * energy * 1.1);
    col += a;
    float lum = dot(a, vec3(0.2126, 0.7152, 0.0722));
    col += vec3(0.02, 0.07, 0.05) * clamp(lum * 0.6, 0.0, 0.4) * (1.0 - smoothstep(0.0, 0.5, rd.y));

    col = aces(col * 1.15);
    col = mix(col, vec3(0.012, 0.020, 0.038), (1.0 - smoothstep(-0.01, 0.09, rd.y)) * 0.92);
    col += (hash13(vec3(gl_FragCoord.xy, 7.0)) - 0.5) / 255.0;

    fragColor = vec4(col, uAlpha);
}
