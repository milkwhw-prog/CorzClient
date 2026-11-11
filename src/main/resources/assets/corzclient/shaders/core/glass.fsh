#version 150

in vec2 texCoord0;
in vec2 texCoord;

out vec4 fragColor;

uniform sampler2D Sampler0;

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
};

mat3 rotX(float a) {
    float c = cos(a), s = sin(a);
    return mat3(1.0, 0.0, 0.0,
                0.0,   c,   s,
                0.0,  -s,   c);
}

mat3 rotY(float a) {
    float c = cos(a), s = sin(a);
    return mat3(  c, 0.0,   s,
                0.0, 1.0, 0.0,
                 -s, 0.0,   c);
}

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 3; i++) {
        v += a * noise(p);
        p *= 2.0;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec2 uvCoord = (texCoord0.x != 0.0 || texCoord0.y != 0.0) ? texCoord0 : texCoord;
    vec4 texColor = texture(Sampler0, uvCoord);
    if (texColor.a < 0.1) {
        discard;
    }
    vec2 res = max(uResolution, vec2(1.0));
    vec2 uv = gl_FragCoord.xy / res;
    vec2 sp = uv * 2.0 - 1.0;
    float aspect = res.x / res.y;
    float safeFov = clamp(uFov, 10.0, 170.0);
    float tanV = tan(radians(safeFov) * 0.5);
    vec3 rayV = normalize(vec3(sp.x * tanV * aspect, sp.y * tanV, 1.0));
    vec3 rayW = rotY(uCameraDir.x) * rotX(uCameraDir.y) * rayV;
    float yaw = atan(rayW.z, rayW.x + 1e-5);
    float pitch = clamp(rayW.y, -0.99, 0.99);
    vec2 skyUV = vec2(yaw, pitch * 2.0);
    float t = uTime * uSpeed * 0.3;
    vec2 p = skyUV * (uScale * 0.3 + 0.15);
    float glassNoise = fbm(p + vec2(t * 0.2, -t * 0.1));
    vec2 distortedUV = skyUV + vec2(glassNoise * 0.08);
    float streak1 = sin((distortedUV.x + distortedUV.y) * 3.5 + t * 0.9) * 0.5 + 0.5;
    streak1 = pow(streak1, 10.0) * 0.75;
    float streak2 = sin((distortedUV.x * 1.5 - distortedUV.y) * 5.0 - t * 0.6) * 0.5 + 0.5;
    streak2 = pow(streak2, 14.0) * 0.5;
    float streaks = (streak1 + streak2) * (1.0 + uIntensity * 12.0);
    float fresnel = pow(1.0 - abs(pitch), 2.2) * 0.45;
    vec3 glassBaseColor = uColor * (0.65 + glassNoise * 0.2);
    vec3 glintColor = mix(vec3(1.0), uColor * 1.4, 0.35) * (streaks + fresnel * 0.8);
    vec3 finalColor = glassBaseColor + glintColor;
    float baseGlassAlpha = uAlpha * 0.5;
    float finalAlpha = clamp((baseGlassAlpha + streaks * 0.35 + fresnel * 0.25) * texColor.a, 0.0, 1.0);
    fragColor = vec4(finalColor, finalAlpha);
}
