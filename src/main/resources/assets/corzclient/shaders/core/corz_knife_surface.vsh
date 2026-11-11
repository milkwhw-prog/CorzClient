#version 330
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
uniform sampler2D Sampler2;
in vec3 Position;
in vec4 Color;
in vec3 Normal;
in vec2 UV0;
in ivec2 UV2;
out vec2 uv;
out vec3 normal;
out vec3 view;
out vec3 illumination;
void main(){vec4 p=ModelViewMat*vec4(Position,1);gl_Position=ProjMat*p;uv=UV0;normal=mat3(ModelViewMat)*Normal;view=-p.xyz;illumination=texelFetch(Sampler2,UV2/16,0).rgb*Color.rgb*ColorModulator.rgb;}
