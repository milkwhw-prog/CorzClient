#version 330
#moj_import <corzclient:corz_knife_surface.glsl>
in vec2 uv;
in vec3 normal;
in vec3 view;
in vec3 illumination;
out vec4 fragColor;
void main(){fragColor=knifeSurface(uv,normal,view,illumination);}
