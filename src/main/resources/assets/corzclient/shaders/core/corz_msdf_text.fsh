#version 330

layout(std140) uniform CorzMsdf {
    vec2 uScaledRes;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float median(vec3 c) {
    return max(min(c.r, c.g), min(max(c.r, c.g), c.b));
}

const float MSDF_RANGE = 25.0;
const float EDGE = 0.48;
const float SMOOTHNESS = 1.0;

void main() {
    vec3 msd = texture(Sampler0, texCoord0).rgb;
    float dist = median(msd) - EDGE;

    vec2 h = vec2(dFdx(texCoord0.x), dFdy(texCoord0.y)) * vec2(textureSize(Sampler0, 0));
    float pixels = MSDF_RANGE * inversesqrt(h.x * h.x + h.y * h.y);

    float alpha = smoothstep(-SMOOTHNESS, SMOOTHNESS, dist * pixels);
    if (alpha <= 0.001) {
        discard;
    }
    fragColor = vec4(vertexColor.rgb, vertexColor.a * alpha);
}
