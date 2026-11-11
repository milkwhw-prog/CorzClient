#version 150

in vec2 texCoord0;
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
    return mat3(1.0, 0.0, 0.0, 0.0, c, s, 0.0, -s, c);
}

mat3 rotY(float a) {
    float c = cos(a), s = sin(a);
    return mat3(c, 0.0, s, 0.0, 1.0, 0.0, -s, 0.0, c);
}

float hash3(vec3 p) {
    p = fract(p * vec3(443.8975, 397.2973, 491.1871));
    p += dot(p, p.yxz + 19.19);
    return fract((p.x + p.y) * p.z);
}

float noise3D(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash3(i), hash3(i + vec3(1.0, 0.0, 0.0)), f.x),
            mix(hash3(i + vec3(0.0, 1.0, 0.0)), hash3(i + vec3(1.0, 1.0, 0.0)), f.x), f.y),
        mix(mix(hash3(i + vec3(0.0, 0.0, 1.0)), hash3(i + vec3(1.0, 0.0, 1.0)), f.x),
            mix(hash3(i + vec3(0.0, 1.0, 1.0)), hash3(i + vec3(1.0, 1.0, 1.0)), f.x), f.y),
        f.z
    );
}

float fbm(vec3 p) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100.0);
    for (int n = 0; n < 3; ++n) {
        v += a * noise3D(p);
        p = p * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a < 0.1) {
        discard;
    }

    vec2 uv = gl_FragCoord.xy / uResolution;
    vec2 sp = uv * 2.0 - 1.0;
    float aspect = uResolution.x / uResolution.y;

    float tanV = tan(radians(uFov) * 0.5);
    vec3 rayV = normalize(vec3(sp.x * tanV * aspect, sp.y * tanV, 1.0));
    vec3 rayW = rotY(uCameraDir.x) * rotX(uCameraDir.y) * rayV;

    float time = uTime * uSpeed * 0.4;
    vec3 p = rayW * (uScale * 0.4 + 0.5);

    vec3 q = vec3(
        fbm(p + vec3(0.0, time * 0.4, 0.0)),
        fbm(p + vec3(1.3, 0.8, time * 0.3)),
        fbm(p + vec3(2.8, time * 0.5, 1.1))
    );

    vec3 r = vec3(
        fbm(p + 1.2 * q + vec3(1.7, 9.2, time * 0.6)),
        fbm(p + 1.2 * q + vec3(8.3, 2.8, time * 0.4)),
        fbm(p + 1.2 * q + vec3(3.2, 1.4, time * 0.5))
    );

    float f = fbm(p + r * 1.8);

    vec3 color1 = uColor;
    vec3 color2 = vec3(uColor.b, uColor.r, uColor.g);
    vec3 color3 = vec3(1.0) - uColor * 0.4;

    vec3 finalColor = mix(color1, color2, clamp(f * f * 4.0, 0.0, 1.0));
    finalColor = mix(finalColor, color3, clamp(length(q), 0.0, 1.0));
    finalColor = mix(finalColor, color1 * 1.5, clamp(length(r.xy), 0.0, 1.0));

    finalColor *= (f * 1.5 + uIntensity * 12.0);

    fragColor = vec4(finalColor, uAlpha * texColor.a);
}
