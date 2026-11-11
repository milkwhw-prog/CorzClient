#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    float shape = floor((texCoord0.x + 2.0) * 0.25);
    vec2 p = vec2(texCoord0.x - shape * 4.0, texCoord0.y);
    vec3 col = vertexColor.rgb;

    float a;
    if (shape < 0.5) {
        float r2 = dot(p, p);
        float r = sqrt(r2);
        float core = exp(-r2 * 7.0);
        col *= mix(0.74, 1.06, core);
        a = (1.0 - smoothstep(0.40, 1.0, r)) * (0.60 + 0.40 * core);
    } else {
        float variant = shape - 1.0;
        float t = p.y * 0.5 + 0.5;
        float notch = variant < 0.5 ? 0.0 : (variant < 1.5 ? 0.02 : (variant < 2.5 ? 0.03 : 0.012));
        float skew = variant < 0.5 ? 0.0 : (variant < 1.5 ? 0.07 : (variant < 2.5 ? -0.06 : 0.04));
        float x = p.x - skew * t * t;
        float tn = t + notch * exp(-x * x / 0.03);
        float w;
        if (tn <= 0.6) {
            float u = tn / 0.6;
            w = (0.13 + 0.79 * (1.0 - pow(max(1.0 - u, 0.0), 1.7))) * sqrt(clamp(tn / 0.05, 0.0, 1.0));
        } else {
            float u = clamp((tn - 0.6) / 0.4, 0.0, 1.0);
            w = 0.92 * pow(max(1.0 - pow(u, 2.4), 0.0), 0.41667);
        }
        w = max(w * (1.0 + 0.012 * sin(t * 7.0 + variant * 1.7)), 0.001);
        float d = abs(x) - w;
        float aa = max(fwidth(d), 1.0e-4);
        a = clamp(0.5 - d / aa, 0.0, 1.0);
        float rim = 1.0 - clamp(-d / 0.16, 0.0, 1.0);
        float claw = 1.0 - smoothstep(0.0, 0.32, t);
        col = mix(col, vec3(1.0, 0.86, 0.80) * min(col.r * 1.12, 1.0), 0.30 * claw);
        float q = clamp(x / w, -1.0, 1.0);
        col *= 1.0 - 0.10 * (1.0 - q * q);
        col *= 1.0 - 0.07 * exp(-p.x * p.x / 0.0025) * (1.0 - t);
        col *= 1.0 + 0.10 * rim;
        a *= 0.93 * mix(1.0, 0.72, rim * rim);
    }

    vec4 c = vec4(col, a * vertexColor.a) * ColorModulator;
    if (c.a <= 0.002) {
        discard;
    }
    float fog = total_fog_value(sphericalVertexDistance, cylindricalVertexDistance,
            FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd);
    c.rgb = mix(c.rgb, FogColor.rgb, fog * FogColor.a);
    c.a *= 1.0 - fog;
    fragColor = c;
}
