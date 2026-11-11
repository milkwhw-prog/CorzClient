#version 330

layout(std140) uniform CorzRoundedTex {
    vec2 uScaledRes;
    vec2 uResolution;
    vec2 uPosition;
    vec2 uSize;
    float uRadius;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float roundedBoxSDF(vec2 centerPosition, vec2 size, float radius) {
    vec2 q = abs(centerPosition) - size + radius;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
}

void main() {
    vec4 tex = texture(Sampler0, texCoord0) * vertexColor;

    vec2 _position = vec2(uPosition.x, uResolution.y - uPosition.y);
    vec2 size05 = uSize / 2.0;
    float distance = roundedBoxSDF(vec2(gl_FragCoord.x - size05.x, gl_FragCoord.y + size05.y) - _position, size05, uRadius);
    float mask = clamp(0.5 - distance, 0.0, 1.0);

    float alpha = tex.a * mask;
    if (alpha <= 0.001) {
        discard;
    }
    fragColor = vec4(tex.rgb, alpha);
}
