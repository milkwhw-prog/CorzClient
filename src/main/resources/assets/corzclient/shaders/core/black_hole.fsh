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

const float R_IN = 3.0;
const float R_OUT = 11.0;
const float B_CRIT = 2.598;
const int STEPS = 32;

float hash13(vec3 p) {
    p = fract(p * vec3(443.8975, 397.2973, 491.1871));
    p += dot(p, p.yxz + 19.19);
    return fract((p.x + p.y) * p.z);
}

vec3 hash33(vec3 p) {
    return vec3(hash13(p), hash13(p + 19.7), hash13(p + 37.3));
}

float noise3(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash13(i + vec3(0.0, 0.0, 0.0)), hash13(i + vec3(1.0, 0.0, 0.0)), f.x),
            mix(hash13(i + vec3(0.0, 1.0, 0.0)), hash13(i + vec3(1.0, 1.0, 0.0)), f.x), f.y),
        mix(mix(hash13(i + vec3(0.0, 0.0, 1.0)), hash13(i + vec3(1.0, 0.0, 1.0)), f.x),
            mix(hash13(i + vec3(0.0, 1.0, 1.0)), hash13(i + vec3(1.0, 1.0, 1.0)), f.x), f.y),
        f.z);
}

const mat3 M3 = mat3( 0.00,  0.80,  0.60,
                     -0.80,  0.36, -0.48,
                     -0.60, -0.48,  0.64);

float fbm3(vec3 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 3; ++i) {
        v += a * noise3(p);
        p = M3 * p * 2.03;
        a *= 0.5;
    }
    return v / 0.875;
}

vec3 aces(vec3 x) {
    const float a = 2.51, b = 0.03, c = 2.43, d = 0.59, e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

vec3 rotAxis(vec3 v, vec3 k, float a) {
    float c = cos(a), s = sin(a);
    return v * c + cross(k, v) * s + k * dot(k, v) * (1.0 - c);
}

vec3 bend(vec3 v, vec3 toward, float a) {
    return normalize(v * cos(a) + toward * sin(a));
}

float farField(float s, float b) {
    float q = b * b + s * s;
    return s * (2.0 * s * s + 3.0 * b * b) / (2.0 * b * q * sqrt(q));
}

vec3 starLayer(vec3 rd, float scale, float thresh, float rad, float t) {
    vec3 p = rd * scale;
    vec3 ip = floor(p);
    vec3 fp = fract(p) - 0.5;
    float present = hash13(ip * 1.07 + 0.5);
    if (present <= thresh) return vec3(0.0);
    vec3 off = (hash33(ip + 2.3) - 0.5) * 0.7;
    float d = length(fp - off);
    float bright = pow(hash13(ip + 5.5), 3.5) * 1.6;
    float core = 1.0 - smoothstep(0.0, rad, d);
    float halo = (1.0 - smoothstep(0.0, rad * 4.5, d)) * 0.2;
    float tw = 0.70 + 0.30 * sin(t * (1.1 + present * 1.7) + present * 37.0);
    float temp = hash13(ip + 13.7);
    vec3 col = mix(vec3(0.64, 0.76, 1.0), vec3(1.0, 0.96, 0.90), smoothstep(0.1, 0.55, temp));
    col = mix(col, vec3(1.0, 0.74, 0.48), smoothstep(0.72, 1.0, temp));
    return col * (core + halo) * bright * tw;
}

vec3 deepSpace(vec3 d, float t, float sharp) {
    vec3 gN = normalize(vec3(0.35, 1.0, -0.25));
    float band = pow(1.0 - abs(dot(d, gN)), 5.0);
    float n1 = fbm3(d * 2.4 + vec3(0.0, t * 0.004, 0.0));
    float n2 = fbm3(d * 5.3 + vec3(7.3, 1.1, -t * 0.003));
    float dens = pow(clamp(n1 * 0.8 + n2 * 0.4 - 0.30, 0.0, 1.0), 1.8) * (0.30 + 1.2 * band);
    vec3 neb = mix(vec3(0.10, 0.06, 0.20), vec3(0.24, 0.12, 0.08), smoothstep(0.35, 0.75, n2));
    neb = mix(neb, neb * uColor * 1.8, 0.16);
    vec3 col = vec3(0.0025, 0.0030, 0.0060) + neb * dens * 0.42 + vec3(0.05, 0.05, 0.07) * band * 0.10;
    vec3 st = starLayer(d, 110.0, 0.940, 0.060, t)
            + starLayer(d, 250.0, 0.880, 0.055, t * 1.2) * 0.85
            + starLayer(d, 520.0, 0.840, 0.050, t * 1.4) * 0.60;
    return col + mix(vec3(0.010, 0.011, 0.016), st, sharp);
}

vec3 diskTemp(float x) {
    vec3 c = mix(vec3(0.90, 0.16, 0.02), vec3(1.00, 0.52, 0.14), smoothstep(0.15, 0.55, x));
    c = mix(c, vec3(1.00, 0.86, 0.66), smoothstep(0.55, 0.95, x));
    return mix(c, vec3(0.82, 0.88, 1.00), smoothstep(1.05, 1.60, x));
}

void diskHit(vec3 P, vec3 v, vec3 n, vec3 e1, vec3 e2, float t, float image, inout vec3 col, inout float a) {
    float r = length(P);
    float edge = smoothstep(R_IN, R_IN + 0.5, r) * (1.0 - smoothstep(R_OUT * 0.55, R_OUT, r));
    if (edge <= 0.0) return;
    float ang = atan(dot(P, e2), dot(P, e1));
    float omega = sqrt(0.5 / (r * r * r));
    float spin = ang - t * omega * 2.4;
    vec2 cs = vec2(cos(spin), sin(spin));
    float detail = image < 0.5 ? 1.0 : 0.35;
    float n1 = fbm3(vec3(cs * 1.6, r * 0.62));
    float n2 = noise3(vec3(cs * 4.0, r * 1.45 + 3.0));
    float dens = smoothstep(0.14, 0.88, mix(n1, 0.66 * n1 + 0.34 * n2, detail));
    float u = R_IN / r;
    float temp = pow(u, 0.75) * pow(max(1.0 - sqrt(u), 0.0), 0.25) / 0.488;
    vec3 orbit = normalize(cross(n, P));
    float beta = sqrt(0.5 / r);
    float gamma = inversesqrt(1.0 - beta * beta);
    float dop = 1.0 / (gamma * (1.0 - beta * dot(orbit, -normalize(v))));
    float g = dop * sqrt(max(1.0 - 1.0 / r, 0.0));
    float lum = pow(g, 3.0) * (0.25 + 0.75 * temp);
    vec3 c = diskTemp(temp * g) * lum * (0.40 + 1.15 * dens) * 1.7;
    float al = clamp((0.30 + 0.85 * dens) * edge, 0.0, 0.97);
    col += (1.0 - a) * c * al;
    a += (1.0 - a) * al;
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
    float sizeK = clamp(uScale / 5.0, 0.4, 2.5);
    float dist = 30.0 / sizeK;

    vec3 f = normalize(vec3(0.0, 0.37, 0.93));
    vec3 cam = -f * dist;
    float tca = dist * dot(f, rd);
    vec3 closest = cam + rd * tca;
    float b0 = max(length(closest), 1.0e-4);
    vec3 toward = -closest / b0;

    vec3 right = normalize(cross(f, vec3(0.0, 1.0, 0.0)));
    vec3 up = cross(right, f);
    vec3 n = normalize(up * cos(0.15) - f * sin(0.15));
    n = rotAxis(n, f, 0.21);
    vec3 e1 = normalize(cross(n, f));
    vec3 e2 = cross(n, e1);

    float bw = fwidth(b0);

    vec3 diskCol = vec3(0.0);
    float diskA = 0.0;
    float captured = 0.0;
    vec3 outDir = rd;
    float rm = min(16.0, dist * 0.9);

    if (b0 >= rm || tca <= 0.0) {
        float alpha = 1.0 / b0 + farField(tca, b0);
        outDir = bend(rd, toward, min(alpha, 1.5));
    } else {
        float hc = sqrt(rm * rm - b0 * b0);
        float pre = farField(tca, b0) - farField(hc, b0);
        vec3 x = cam + rd * (tca - hc);
        vec3 v = bend(rd, toward, pre);
        vec3 hv = cross(x, v);
        float h2 = dot(hv, hv);
        bool exited = false;
        float crossings = 0.0;
        float r0 = length(x);
        vec3 acc = -1.5 * h2 * x / (r0 * r0 * r0 * r0 * r0);
        for (int i = 0; i < STEPS; i++) {
            float r = length(x);
            if (r < 1.0) {
                captured = 1.0;
                break;
            }
            float dt = clamp(0.30 * (r - 1.0), 0.05, 2.4);
            vec3 x2 = x + v * dt + acc * (0.5 * dt * dt);
            float r2 = max(length(x2), 0.5);
            vec3 acc2 = -1.5 * h2 * x2 / (r2 * r2 * r2 * r2 * r2);
            vec3 v2 = v + (acc + acc2) * (0.5 * dt);
            float y1 = dot(x, n);
            float y2 = dot(x2, n);
            if (y1 * y2 < 0.0 && diskA < 0.985) {
                vec3 P = mix(x, x2, y1 / (y1 - y2));
                float rp = length(P);
                if (rp > R_IN && rp < R_OUT) diskHit(P, mix(v, v2, 0.5), n, e1, e2, t, crossings, diskCol, diskA);
                crossings += 1.0;
            }
            x = x2;
            v = v2;
            acc = acc2;
            if (dot(x, v) > 0.0 && dot(x, x) > rm * rm) {
                exited = true;
                break;
            }
        }
        vec3 vd = normalize(v);
        if (captured < 0.5 && !exited && length(x) < 2.2) captured = 1.0;
        if (captured < 0.5) {
            float along = dot(x, vd);
            vec3 cl = x - vd * along;
            float be = max(length(cl), 1.0e-4);
            float post = 1.0 / be - farField(along, be);
            outDir = bend(vd, -cl / be, clamp(post, 0.0, 1.0));
        } else {
            outDir = vd;
        }
    }

    float lens = length(fwidth(outDir));
    float sharp = 1.0 - smoothstep(0.0015, 0.012, lens);
    float shadow = smoothstep(B_CRIT - bw, B_CRIT + bw, b0);
    vec3 bg = deepSpace(outDir, t, sharp) * (1.0 - captured) * shadow;

    vec3 col = diskCol + (1.0 - diskA) * bg;

    float rw = max(0.035, bw * 1.25);
    float rd0 = (b0 - B_CRIT * 1.015) / rw;
    vec3 ringSide = normalize(rd - f * dot(rd, f));
    float ringDoppler = 0.55 + 0.45 * dot(ringSide, normalize(cross(n, f)));
    float ring = exp(-rd0 * rd0) * (0.035 / rw) * ringDoppler;
    float ringGlow = exp(-max(b0 - B_CRIT, 0.0) / 0.55) * shadow;
    col += diskTemp(1.05) * (ring * 1.5 + ringGlow * 0.10) * (1.0 - 0.7 * diskA);

    float halo = exp(-abs(b0 - 5.5) / 4.0) * 0.05;
    col += diskTemp(0.85) * halo * smoothstep(B_CRIT, B_CRIT * 1.6, b0);

    col *= energy;
    col = aces(col * 1.05);
    col += (hash13(vec3(gl_FragCoord.xy, 3.0)) - 0.5) / 255.0;

    fragColor = vec4(col, uAlpha);
}
