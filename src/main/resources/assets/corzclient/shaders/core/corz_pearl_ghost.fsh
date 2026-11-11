#version 330

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 texel = texture(Sampler0, texCoord0);

    if (texel.a < 0.5) {
        discard;
    }

    fragColor = vec4(texel.rgb, 1.0) * vertexColor;
}
