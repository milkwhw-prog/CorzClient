#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in ivec2 UV2;

uniform sampler2D Sampler2;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec2 texCoord0;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    texCoord0 = UV0;
    vec3 light = texelFetch(Sampler2, UV2 / 16, 0).rgb;
    float lift = UV0.x < 2.0 ? 0.24 : 0.09;
    light = light * (1.0 - lift) + vec3(0.86, 0.90, 1.0) * lift;
    vertexColor = vec4(Color.rgb * light, Color.a);
}
