#version 330

layout(std140) uniform CorzSchemComposite {
    vec4 uScreen;
    vec4 uRect;
    vec4 uShape;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;

out vec4 fragColor;

float roundedBox(vec2 p, vec2 halfSize, float r) {
    vec2 q = abs(p) - halfSize + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 center = (uRect.xy + uRect.zw) * 0.5;
    vec2 halfSize = max((uRect.zw - uRect.xy) * 0.5, vec2(0.0));
    float radius = min(uShape.x, min(halfSize.x, halfSize.y));
    float d = roundedBox(gl_FragCoord.xy - center, halfSize, radius);
    float mask = clamp(0.5 - d, 0.0, 1.0) * uShape.y;
    vec4 color = texture(Sampler0, texCoord0) * mask;
    if (color.a <= 0.0005) {
        discard;
    }
    fragColor = color;
}
