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

float hash(vec3 p) {
    p = fract(p * vec3(443.897, 441.423, 437.195));
    p += dot(p, p.yzx + 19.19);
    return fract((p.x + p.y) * p.z);
}

float noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f*f*(3.0-2.0*f);
    return mix(mix(mix(hash(p+vec3(0,0,0)), hash(p+vec3(1,0,0)), f.x),
                   mix(hash(p+vec3(0,1,0)), hash(p+vec3(1,1,0)), f.x), f.y),
               mix(mix(hash(p+vec3(0,0,1)), hash(p+vec3(1,0,1)), f.x),
                   mix(hash(p+vec3(0,1,1)), hash(p+vec3(1,1,1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 4; ++i) {
        v += a * noise(p);
        p = p * 2.1;
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

    vec3 p = rayW * uScale * 0.5;
    float t = uTime * uSpeed * 0.05;
    p.xy += vec2(sin(p.z + t), cos(p.z - t)) * 0.5;

    float n1 = fbm(p + vec3(0.0, 0.0, t));
    float n2 = fbm(p * 1.5 - vec3(t, n1, 0.0));

    vec3 nebulaColor1 = uColor;
    vec3 nebulaColor2 = vec3(1.0, 0.1, 0.5);

    vec3 finalColor = mix(nebulaColor1 * 0.08, nebulaColor1, n1 * n1);
    finalColor = mix(finalColor, nebulaColor2 * 1.2, n2 * n2 * n1);

    float starBg = hash(floor(rayW * 180.0));
    if (starBg > 0.993) {
        float spark = sin(uTime * 3.0 + starBg * 100.0) * 0.5 + 0.5;
        finalColor += vec3(1.0) * spark * uIntensity;
    }

    fragColor = vec4(finalColor, uAlpha);
}
