#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

out vec3 vRel;
out vec2 vWave;
out vec3 vTint;
out vec3 vLight;
out float vSky;
out vec2 vFlow;
out float vDepth;
out float vSphere;
out float vCyl;
out float vFadeIn;

const float DEPTH_MAX = 8.0;

void main() {
    vec3 pos = Position + ModelOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vRel = pos;
    vWave = UV0;
    vTint = Color.rgb;
    vLight = texture(Sampler2, clamp((UV2 / 256.0) + 0.5 / 16.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0))).rgb;
    vSky = clamp(float(UV2.y) / 240.0, 0.0, 1.0);
    vFlow = Normal.xz;
    vDepth = clamp(Normal.y, 0.0, 1.0) * DEPTH_MAX;
    vSphere = fog_spherical_distance(pos);
    vCyl = fog_cylindrical_distance(pos);
    vFadeIn = ColorModulator.a;
}
