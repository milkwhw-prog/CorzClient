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

    float time = uTime * uSpeed * 0.5;
    vec3 p = rayW * (uScale * 0.4 + 0.8);

    vec3 warp = vec3(
        noise3D(p + vec3(0.0, time * 0.8, 0.0)),
        noise3D(p + vec3(time * 0.6, 12.4, time * 0.4)),
        noise3D(p + vec3(27.1, 0.0, time * 0.7))
    ) * 1.2;

    vec3 distortedRay = normalize(rayW + warp * 0.45);

    float lineBloom = 0.0;
    float lineCore = 0.0;

    for (int i = 0; i < 4; i++) {
        float fi = float(i);
        float wave = sin(distortedRay.x * 2.5 + time + fi * 1.8) *
                     cos(distortedRay.z * 2.5 - time * 0.6 + fi * 0.7) * 0.45;

        float organicBend = (noise3D(distortedRay * 2.0 + vec3(time * 0.3, fi * 4.0, time * 0.2)) - 0.5) * 0.6;
        float targetY = (fi - 1.5) * 0.3 + wave + organicBend;

        float dist = abs(rayW.y - targetY);

        float core = exp(-110.0 * dist);
        float bloom = exp(-11.0 * dist);

        lineCore += core;
        lineBloom += bloom;
    }

    vec3 brightColor = uColor * (2.2 + uIntensity * 20.0);
    vec3 bloomColor = uColor * (0.6 + uIntensity * 10.0);

    vec3 finalColor = uColor * 0.04;
    finalColor += bloomColor * lineBloom;
    finalColor += brightColor * lineCore;

    fragColor = vec4(finalColor, uAlpha * texColor.a);
}
