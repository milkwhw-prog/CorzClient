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

mat3 rotX(float a) {
    float c = cos(a), s = sin(a);
    return mat3(1.0, 0.0, 0.0, 0.0, c, s, 0.0, -s, c);
}
mat3 rotY(float a) {
    float c = cos(a), s = sin(a);
    return mat3(c, 0.0, s, 0.0, 1.0, 0.0, -s, 0.0, c);
}
vec3 rotAxis(vec3 v, vec3 k, float a) {
    float c = cos(a), s = sin(a);
    return v * c + cross(k, v) * s + k * dot(k, v) * (1.0 - c);
}

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

float fbm4(vec3 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; ++i) {
        v += a * noise3(p);
        p = M3 * p * 2.03;
        a *= 0.5;
    }
    return v;
}

vec3 aces(vec3 x) {
    const float a = 2.51, b = 0.03, c = 2.43, d = 0.59, e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

vec2 raySphere(vec3 centre, float r, vec3 rd) {
    float b = dot(-centre, rd);
    float c = dot(centre, centre) - r * r;
    float h = b * b - c;
    if (h < 0.0) return vec2(-1.0);
    h = sqrt(h);
    return vec2(-b - h, -b + h);
}

float shadowFactor(vec3 P, vec3 centre, float R, vec3 L) {
    vec3 oc = centre - P;
    float tca = dot(oc, L);
    if (tca < 0.0) return 1.0;
    float d2 = dot(oc, oc) - tca * tca;
    float r2 = R * R;
    return smoothstep(r2 * 0.88, r2 * 1.22, d2);
}

vec3 starLayer(vec3 rd, float scale, float thresh, float rad, float bloom, float twT) {
    vec3 p = rd * scale;
    vec3 ip = floor(p);
    vec3 fp = fract(p) - 0.5;
    float present = hash13(ip * 1.07 + 0.5);
    if (present <= thresh) return vec3(0.0);
    vec3 off = (hash33(ip + 2.3) - 0.5) * 0.7;
    float d = length(fp - off);
    float bright = pow(hash13(ip + 5.5), 4.5);
    float core = smoothstep(rad, 0.0, d);
    float halo = smoothstep(rad * 5.0, 0.0, d) * bloom;
    float tw = 0.72 + 0.28 * sin(twT * 1.9 + present * 6.2831);
    float temp = hash13(ip + 13.7);
    vec3 col = mix(vec3(0.62, 0.76, 1.0), vec3(1.0, 0.96, 0.9), smoothstep(0.0, 0.5, temp));
    col = mix(col, vec3(1.0, 0.72, 0.44), smoothstep(0.6, 1.0, temp));
    return col * (core + halo) * bright * tw;
}

vec3 galaxy(vec3 rd, float t, float energy) {
    vec3 col = vec3(0.004, 0.006, 0.012);
    float drift = t * 0.005;

    vec3 pp = rd * 2.3;
    vec3 w = vec3(
        fbm4(pp + vec3(drift, 0.0, 0.0)),
        fbm4(pp + vec3(5.2, drift * 0.8, 1.3)),
        fbm4(pp + vec3(9.1, 2.4, drift)));
    float neb  = fbm4(pp * 1.5 + w * 1.9);
    float neb2 = fbm4(pp * 3.2 + w * 2.3);

    vec3 gN = normalize(vec3(0.15, 1.0, -0.2));
    float band = pow(1.0 - abs(dot(rd, gN)), 4.0);

    float density = neb * 0.7 + neb2 * 0.3;
    density = pow(clamp(density * (0.35 + 0.9 * band), 0.0, 1.0), 2.4);

    float dust = fbm4(pp * 2.1 + w * 1.2 + vec3(17.0));
    float dustLane = smoothstep(0.55, 0.82, dust) * (0.4 + 0.6 * band);

    float hueA = clamp(w.x * 1.3, 0.0, 1.0);
    float hueB = clamp(w.z * 1.3, 0.0, 1.0);
    vec3 deepBlue = vec3(0.05, 0.09, 0.20);
    vec3 steel    = vec3(0.08, 0.13, 0.19);
    vec3 warmDust = vec3(0.20, 0.14, 0.09);
    vec3 nebCol = mix(deepBlue, steel, smoothstep(0.3, 0.7, hueA));
    nebCol = mix(nebCol, warmDust, smoothstep(0.6, 1.0, hueB) * 0.6);
    nebCol = mix(nebCol, nebCol * uColor * 1.8, 0.08);

    col += nebCol * pow(density, 2.0) * 0.60;
    col += nebCol * pow(density, 5.0) * 0.8;
    col *= (1.0 - dustLane * 0.85);

    float mote  = fbm4(rd * 8.0  + vec3(drift * 2.0, 0.0, 1.7));
    float mote2 = fbm4(rd * 21.0 + vec3(0.0, drift, 4.0));
    float haze  = smoothstep(0.52, 0.9, mote) * 0.6 + smoothstep(0.62, 1.0, mote2) * 0.35;
    col += mix(vec3(0.10, 0.11, 0.14), vec3(0.15, 0.13, 0.10), hueB) * haze * (0.45 + 0.55 * band);

    vec3 st = vec3(0.0);
    st += starLayer(rd,  90.0, 0.965, 0.050, 0.22, t * 0.5);
    st += starLayer(rd, 200.0, 0.900, 0.050, 0.14, t * 0.7);
    st += starLayer(rd, 430.0, 0.840, 0.045, 0.10, t * 0.9);
    st += starLayer(rd, 820.0, 0.800, 0.040, 0.06, t * 1.1) * 0.7;
    st *= (1.0 - dustLane * 0.7);
    st += vec3(0.8, 0.85, 1.0) * band * 0.02 * (0.5 + 0.5 * neb);

    col += st;
    col *= clamp(uGalaxy, 0.0, 3.0) * (0.7 + energy * 0.4);
    return min(col, vec3(1.6));
}

vec3 shadePlanet(vec3 rd, float tS, vec3 C, float R, vec3 axis, vec3 L, float spin, float detail) {
    vec3 P = rd * tS;
    vec3 N = normalize(P - C);
    vec3 Ns = rotAxis(N, axis, spin);
    float lat = clamp(dot(Ns, axis), -1.0, 1.0);

    vec3 wp = Ns * (2.6 * detail) + vec3(spin * 0.6, 0.0, 0.0);
    float turb  = fbm4(wp) - 0.5;
    float turb2 = fbm4(wp * 2.6 + 13.0) - 0.5;
    float widthMod = fbm4(vec3(lat * 2.2, spin * 0.5, 7.0)) - 0.5;
    float bandArg = lat * 10.0 + turb * 0.7 + turb2 * 0.3 + widthMod * 1.0;
    float band = sin(bandArg * 3.14159) * 0.5 + 0.5;
    float fine = fbm4(Ns * (10.0 * detail) + vec3(0.0, spin * 3.0, 0.0)) - 0.5;
    band = clamp(band + fine * 0.14, 0.0, 1.0);

    vec3 cDark  = vec3(0.52, 0.43, 0.31);
    vec3 cBrown = vec3(0.66, 0.55, 0.38);
    vec3 cAmber = vec3(0.78, 0.67, 0.46);
    vec3 cGold  = vec3(0.87, 0.78, 0.57);
    vec3 cCream = vec3(0.94, 0.89, 0.73);
    vec3 albedo = mix(cDark, cBrown, smoothstep(0.0, 0.28, band));
    albedo = mix(albedo, cAmber, smoothstep(0.25, 0.5, band));
    albedo = mix(albedo, cGold,  smoothstep(0.45, 0.72, band));
    albedo = mix(albedo, cCream, smoothstep(0.7, 0.95, band));
    albedo = mix(albedo, vec3(0.66, 0.62, 0.58), smoothstep(0.72, 0.98, abs(lat)) * 0.55);

    float ndl = dot(N, L);
    float day = smoothstep(-0.12, 0.32, ndl);
    vec3 sun = vec3(1.06, 0.96, 0.80);
    vec3 lit = albedo * (0.05 + 0.95 * day) * sun;

    float term = pow(clamp(1.0 - abs(ndl), 0.0, 1.0), 3.0) * smoothstep(-0.5, 0.25, ndl);
    lit += albedo * term * vec3(0.55, 0.30, 0.12) * 0.7;

    float night = 1.0 - day;
    float eqBounce = smoothstep(0.55, 0.0, abs(lat));
    lit += albedo * night * (vec3(0.02, 0.022, 0.03) + vec3(0.05, 0.045, 0.035) * eqBounce * 0.5);

    float fres = pow(1.0 - clamp(dot(N, -rd), 0.0, 1.0), 3.2);
    vec3 rimCol = vec3(0.96, 0.79, 0.52);
    lit += rimCol * fres * (0.20 + 0.75 * day);

    lit += sun * pow(clamp(ndl, 0.0, 1.0), 8.0) * 0.18;
    return lit;
}

vec4 shadeRing(vec3 P, vec3 rel, float rad, vec3 C, float R,
               float innerR, float outerR, vec3 tx, vec3 tz, vec3 L, float t, float aa) {
    float u = (rad - innerR) / max(outerR - innerR, 1e-4);
    float ang = atan(dot(rel, tz), dot(rel, tx));

    float cRing = 0.32 * smoothstep(0.02, 0.09, u) * smoothstep(0.20, 0.15, u);
    float bRing = 1.00 * smoothstep(0.15, 0.21, u) * smoothstep(0.53, 0.49, u);
    float aRing = 0.70 * smoothstep(0.60, 0.65, u) * smoothstep(0.96, 0.90, u);
    float env = cRing + bRing + aRing;
    env *= mix(0.30, 1.0, smoothstep(0.004, 0.016, abs(u - 0.845)));

    float striae = 0.55 + (0.45 * aa) * (
          0.50 * sin(u * 190.0)
        + 0.30 * sin(u * 97.0 + 1.3)
        + 0.20 * sin(u * 421.0 + 0.7));
    float dens = env * clamp(striae, 0.0, 1.0);

    float orbit = t * 0.22;
    float az = ang + orbit;
    float sweep = 0.5 + 0.5 * sin(az * 5.0 + u * 4.0);
    float dust  = fbm4(vec3(u * 40.0, az * 3.0, 9.0));
    dens *= (0.60 + 0.55 * dust) * (0.78 + 0.22 * sweep);
    dens = clamp(dens, 0.0, 1.0);

    vec3 ringAlb = mix(vec3(0.55, 0.49, 0.41), vec3(0.90, 0.85, 0.74), dust);

    float sh = shadowFactor(P, C, R, L);
    float phase = 0.5 + 0.5 * dot(normalize(rel), L);
    vec3 col = ringAlb * (0.22 + 0.95 * phase) * sh;
    col += ringAlb * pow(phase, 3.0) * sh * 0.28;
    col = mix(col, col * uColor * 2.0, 0.05);

    float alpha = clamp(dens * (0.55 + 0.4 * smoothstep(0.0, 0.4, u)), 0.0, 0.96);
    return vec4(col, alpha);
}

vec3 planetHalo(vec3 rd, vec3 C, float R, vec3 L, float energy) {
    float tca = dot(C, rd);
    if (tca <= 0.0) return vec3(0.0);
    vec3 closest = rd * tca;
    float dPerp = length(C - closest);
    float g = smoothstep(R * 1.32, R * 1.004, dPerp);
    if (g <= 0.0) return vec3(0.0);
    vec3 limbN = normalize(closest - C);
    float lday = smoothstep(-0.15, 0.55, dot(limbN, L));
    vec3 hc = mix(vec3(0.24, 0.19, 0.15), vec3(0.98, 0.74, 0.44), lday);
    return hc * g * g * (0.18 + 0.85 * lday) * (0.7 + energy * 0.4);
}

vec3 ringDust(vec3 rel, float rad, float innerR, float outerR, vec3 tx, vec3 tz, float t, float sh) {
    float dustInner = innerR * 0.98;
    float dustOuter = outerR * 1.12;
    if (rad < dustInner || rad > dustOuter) return vec3(0.0);

    float az = atan(dot(rel, tz), dot(rel, tx)) + t * 0.05;
    float ru = (rad - dustInner) / max(dustOuter - dustInner, 1e-4);

    vec2 cell = vec2(ru * 90.0, az * 520.0 / 6.2831853);
    vec2 ip = floor(cell);
    vec2 fp = fract(cell) - 0.5;

    float present = hash13(vec3(ip, 3.1));
    if (present < 0.86) return vec3(0.0);

    vec2 off = (vec2(hash13(vec3(ip, 7.7)), hash13(vec3(ip, 9.3))) - 0.5) * 0.6;
    vec2 d = (fp - off) * vec2(1.5, 1.0);
    float spark  = smoothstep(0.34, 0.0, length(d));
    float bright = pow(hash13(vec3(ip, 12.4)), 4.0);
    float env    = smoothstep(0.0, 0.1, ru) * smoothstep(1.0, 0.7, ru);
    float tw     = 0.75 + 0.25 * sin(t * 2.0 + present * 6.2831);

    vec3 c = mix(vec3(0.75, 0.82, 1.0), vec3(1.0, 0.93, 0.78), hash13(vec3(ip, 21.0)));
    return c * spark * bright * env * tw * sh * 0.9;
}

vec3 shootingStars(vec3 rd, float t) {
    vec3 col = vec3(0.0);
    for (int i = 0; i < 5; ++i) {
        float fi = float(i);
        float period = 5.0 + fi * 1.9;
        float phase  = t / period + fi * 7.331;
        float idx = floor(phase);
        float lt  = fract(phase);
        float dur = 0.16;
        if (lt > dur) continue;
        float p = lt / dur;

        vec3 r1 = hash33(vec3(idx, fi, 5.0));
        vec3 r2 = hash33(vec3(idx, fi, 9.0));

        float az = r1.x * 6.2831853;
        float el = 0.40 + r1.y * 0.50;
        vec3 A = vec3(cos(el) * cos(az), sin(el), cos(el) * sin(az));

        vec3 want = normalize(vec3(r2.x - 0.5, -0.75 - r2.y * 0.40, r2.z - 0.5));
        vec3 Bt = want - A * dot(A, want);
        if (dot(Bt, Bt) < 1e-4) continue;
        vec3 B  = normalize(Bt);
        float arc  = 0.35 + hash13(vec3(idx, fi, 2.0)) * 0.30;
        float tail = 0.10 + hash13(vec3(idx, fi, 4.0)) * 0.06;

        float sHead = p * arc;
        vec3  Nrm   = normalize(cross(A, B));
        float dPerp = abs(dot(rd, Nrm));
        float along = atan(dot(rd, B), dot(rd, A));
        float dHead = sHead - along;

        float s = clamp(dHead / tail, 0.0, 1.0);
        float onTail = step(0.0, dHead) * step(dHead, tail);
        float width = mix(0.0016, 0.0055, s);
        float trail = smoothstep(width, 0.0, dPerp) * pow(1.0 - s, 1.7) * onTail;

        vec3  headPos = A * cos(sHead) + B * sin(sHead);
        float head = smoothstep(0.0055, 0.0, distance(rd, headPos));

        vec3 headCol = vec3(1.0, 0.98, 0.92);
        vec3 tailCol = mix(vec3(0.72, 0.9, 1.0), vec3(0.80, 1.0, 0.86), hash13(vec3(idx, fi, 15.0)));
        vec3 trailC  = mix(headCol, tailCol, s);

        float lifeFade = smoothstep(0.0, 0.12, p) * smoothstep(1.0, 0.5, p);
        col += (trailC * trail * 1.1 + headCol * head * 1.8) * lifeFade;
    }
    return col;
}

void main() {
    float aspect = uResolution.x / max(uResolution.y, 1.0);
    vec2 uv = gl_FragCoord.xy / uResolution;
    vec2 sp = uv * 2.0 - 1.0;

    float tanV = tan(radians(uFov) * 0.5);
    vec3 rayV = normalize(vec3(sp.x * tanV * aspect, sp.y * tanV, 1.0));
    vec3 rd = normalize(corzSkyRay(rotY(uCameraDir.x) * rotX(uCameraDir.y) * rayV));

    float t = uTime * uSpeed;
    float energy = clamp(uIntensity / 0.01, 0.2, 6.0);
    float detail = clamp(uScale * 0.14 + 0.3, 0.4, 3.0);

    vec3 saturnDir = normalize(vec3(0.12, 0.90, 0.98));
    vec3 sunDir    = normalize(vec3(-0.55, 0.42, -0.55));
    float dist = 50.0;
    vec3  C = saturnDir * dist;
    float R = dist * 0.30 * clamp(uSaturnSize, 0.2, 4.0);

    vec3 up0 = abs(saturnDir.y) < 0.95 ? vec3(0.0, 1.0, 0.0) : vec3(1.0, 0.0, 0.0);
    vec3 sRight = normalize(cross(up0, saturnDir));
    vec3 sUp    = normalize(cross(saturnDir, sRight));
    float openAngle = radians(mix(84.0, 50.0, clamp(uRingTilt, 0.0, 1.0)));
    vec3 axis = normalize(cos(openAngle) * saturnDir + sin(openAngle) * sUp);

    vec3 tRef = abs(axis.y) < 0.95 ? vec3(0.0, 1.0, 0.0) : vec3(1.0, 0.0, 0.0);
    vec3 tx = normalize(cross(axis, tRef));
    vec3 tz = cross(axis, tx);

    float spin = t * 0.010;

    vec2 sph = raySphere(C, R, rd);
    bool pHit = sph.x > 0.0;
    float tS = pHit ? sph.x : 1e9;

    bool ringHit = false;
    vec4 ring = vec4(0.0);
    vec3 dust = vec3(0.0);
    float innerR = R * 1.30;
    float outerR = R * 2.30;
    float denom = dot(rd, axis);
    if (abs(denom) > 1e-5) {
        float tp = dot(C, axis) / denom;
        if (tp > 0.0 && (!pHit || tp < tS)) {
            vec3 P = rd * tp;
            vec3 rel = P - C;
            float rad = length(rel);
            float aa = clamp(abs(denom) * 4.0, 0.3, 1.0);
            float sh = shadowFactor(P, C, R, sunDir);
            if (rad > innerR && rad < outerR) {
                ringHit = true;
                ring = shadeRing(P, rel, rad, C, R, innerR, outerR, tx, tz, sunDir, t, aa);
            }
            dust = ringDust(rel, rad, innerR, outerR, tx, tz, t, sh) * aa;
        }
    }

    vec3 col;
    if (pHit) {
        col = shadePlanet(rd, tS, C, R, axis, sunDir, spin, detail);
    } else {
        col = galaxy(rd, t, energy);
        col += planetHalo(rd, C, R, sunDir, energy);
    }
    if (ringHit) col = mix(col, ring.rgb, ring.a);
    col += dust;
    col += shootingStars(rd, t);

    col = aces(col * 1.05);
    col += (hash13(vec3(gl_FragCoord.xy, floor(t * 60.0))) - 0.5) / 255.0;

    fragColor = vec4(col, uAlpha);
}
