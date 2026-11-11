#version 330

layout(std140) uniform CorzSchemModel {
    mat4 uMvp;
    vec4 uParams;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a < uParams.x) {
        discard;
    }
    vec3 rgb = color.rgb * uParams.z;
    fragColor = uParams.y > 0.5 ? vec4(rgb, 1.0) : vec4(rgb, color.a);
}
