#version 330

uniform sampler2D HistorySampler;

layout(std140) uniform MotionBlurData {
    vec4 TintRetention;
};

out vec4 fragColor;

void main() {
    vec4 history = texelFetch(HistorySampler, ivec2(gl_FragCoord.xy), 0);
    vec4 premultiplied = vec4(history.rgb * history.a, history.a);
    vec4 paint = vec4(TintRetention.rgb * TintRetention.a, TintRetention.a);
    fragColor = premultiplied * paint;
}
