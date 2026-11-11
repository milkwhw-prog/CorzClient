#version 330

layout(std140) uniform CorzCrosshair {
    vec2 uScaledRes;
    vec2 uResolution;
    vec2 uCenter;
    vec2 uHalfSize;
    vec4 uColor;
    vec4 uOutlineColor;
    float uRadius;
    float uAngle;
    float uOutline;
    float uInner;
    float uArcHalf;
    float uGlow;
    int uShape;
    int uMode;
};

out vec4 fragColor;

const float PI = 3.14159265;

float sdRoundBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

float sdArc(vec2 p, vec2 sc, float ra, float rb) {
    p.x = abs(p.x);
    return ((sc.y * p.x > sc.x * p.y) ? length(p - sc * ra) : abs(length(p) - ra)) - rb;
}

float sdTriangle(vec2 p, float r) {
    const float k = 1.7320508;
    p.x = abs(p.x) - r;
    p.y = p.y + r / k;
    if (p.x + k * p.y > 0.0) p = vec2(p.x - k * p.y, -k * p.x - p.y) / 2.0;
    p.x -= clamp(p.x, -2.0 * r, 0.0);
    return -length(p) * sign(p.y);
}

float coverage(float d) {
    return 1.0 - smoothstep(-0.5, 0.5, d);
}

void main() {
    vec2 f = vec2(gl_FragCoord.x, uResolution.y - gl_FragCoord.y);
    vec2 p = f - uCenter;
    float c = cos(uAngle), s = sin(uAngle);
    p = vec2(c * p.x + s * p.y, -s * p.x + c * p.y);

    float d;
    if (uShape == 1) {
        float ra = (uHalfSize.x + uInner) * 0.5;
        float rb = (uHalfSize.x - uInner) * 0.5;
        if (uArcHalf >= PI) d = abs(length(p) - ra) - rb;
        else d = sdArc(p, vec2(sin(uArcHalf), cos(uArcHalf)), ra, rb);
    } else if (uShape == 2) {
        d = sdTriangle(p, uHalfSize.x);
    } else {
        d = sdRoundBox(p, uHalfSize, uRadius);
        if (uInner > 0.0) d = abs(d) - uInner;
    }

    float aMain = coverage(d);
    float aBand = 0.0;
    float glow = 0.0;
    if (uOutline > 0.0 || uGlow > 0.0) {
        float aOuter = coverage(d - uOutline);
        aBand = uOutline > 0.0 ? aOuter * (1.0 - aMain) : 0.0;
        if (uGlow > 0.0) {
            float t = clamp(1.0 - max(d - uOutline, 0.0) / uGlow, 0.0, 1.0);
            glow = t * t * 0.45 * (1.0 - aOuter);
        }
    }

    vec4 M = (uMode == 1) ? vec4(0.0) : vec4(uColor.rgb, 1.0) * (uColor.a * aMain);
    vec4 O = (uMode == 2) ? vec4(0.0) : vec4(uOutlineColor.rgb, 1.0) * (uOutlineColor.a * aBand);
    vec4 G = (uMode == 2) ? vec4(0.0) : vec4(uColor.rgb, 1.0) * (uColor.a * glow);

    vec4 r = M + O * (1.0 - M.a);
    r += G * (1.0 - r.a);
    if (r.a < 0.002) discard;
    fragColor = vec4(r.rgb / r.a, r.a);
}
