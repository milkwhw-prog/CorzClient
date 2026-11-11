#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

out vec4 vertexColor;
out vec2 texCoord0;
out float camDist;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * view;

    camDist = length(view.xyz);
    vertexColor = Color;
    texCoord0 = UV0;
}
