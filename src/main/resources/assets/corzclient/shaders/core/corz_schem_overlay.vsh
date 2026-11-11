#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;
out float camDist;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * view;

    camDist = length(view.xyz);
    vertexColor = Color;
}
