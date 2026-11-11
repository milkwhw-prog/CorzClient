#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform vec2 OutSize;
uniform vec2 Direction;
uniform float Radius;
uniform float Quality;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 texelSize = 1.0 / InSize;
    vec4 color = vec4(0.0);
    float total = 0.0;

    float offset = Radius;

    for (float i = -Quality; i <= Quality; i++) {
        float weight = 1.0 - abs(i) / (Quality + 1.0);
        weight = weight * weight;

        vec2 sampleOffset = Direction * texelSize * i * offset;
        color += texture(DiffuseSampler, texCoord + sampleOffset) * weight;
        total += weight;
    }

    fragColor = color / total;
}
