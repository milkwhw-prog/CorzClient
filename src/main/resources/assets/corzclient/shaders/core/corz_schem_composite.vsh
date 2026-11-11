#version 330

layout(std140) uniform CorzSchemComposite {
    vec4 uScreen;
    vec4 uRect;
    vec4 uShape;
};

in vec3 Position;
in vec2 UV0;

out vec2 texCoord0;

void main() {
    gl_Position = vec4(Position.x / uScreen.x * 2.0 - 1.0,
                       1.0 - Position.y / uScreen.y * 2.0,
                       0.0, 1.0);
    texCoord0 = UV0;
}
