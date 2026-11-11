uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
vec3 conductorF0(int id) {
    vec3 n=vec3(1),k=vec3(1);
    if(id==230){n=vec3(2.9114,2.9497,2.5845);k=vec3(3.0893,2.9318,2.7670);}
    else if(id==231){n=vec3(.18299,.42108,1.3734);k=vec3(3.4242,2.3459,1.7704);}
    else if(id==232){n=vec3(1.3456,.96521,.61722);k=vec3(7.4746,6.3995,5.3031);}
    else if(id==233){n=vec3(3.1071,3.1812,2.3230);k=vec3(3.3314,3.3291,3.1350);}
    else if(id==234){n=vec3(.27105,.67693,1.3164);k=vec3(3.6092,2.6248,2.2921);}
    else if(id==235){n=vec3(1.9100,1.8300,1.4400);k=vec3(3.5100,3.4000,3.1800);}
    else if(id==236){n=vec3(2.3757,2.0847,1.8453);k=vec3(4.2655,3.7153,3.1365);}
    else if(id==237){n=vec3(.15943,.14512,.13547);k=vec3(3.9291,3.1900,2.3808);}
    return ((n-1.0)*(n-1.0)+k*k)/((n+1.0)*(n+1.0)+k*k);
}
vec4 knifeSurface(vec2 uv,vec3 normal,vec3 view,vec3 illumination) {
    vec4 color=texture(Sampler0,uv);if(color.a<.1)discard;
    vec4 material=texture(Sampler1,uv);
    vec3 n=normalize(normal),v=normalize(view);if(dot(n,v)<0.0)n=-n;
    int id=int(round(material.g*255.0));bool metal=id>=230;
    vec3 albedo=pow(max(color.rgb,vec3(0)),vec3(2.2));
    vec3 f0=metal?conductorF0(id):vec3(material.g);
    if(metal){if(id==255)f0=albedo;else if(max(color.r,max(color.g,color.b))>.001)f0*=pow(color.rgb,vec3(.45));}
    vec3 fresnel=f0+(1.0-f0)*pow(1.0-max(dot(n,v),0.0),5.0);
    float roughness=max(.025,pow(1.0-material.r,2.0));
    float exponent=clamp(2.0/(roughness*roughness)-2.0,8.0,512.0);
    vec3 l=normalize(vec3(-.4,.65,.7)),fill=normalize(vec3(.75,.15,-.45));
    float spec=pow(max(dot(n,normalize(l+v)),0.0),exponent)*1.25;
    spec+=pow(max(dot(n,normalize(fill+v)),0.0),exponent)*.55;
    vec3 reflected=reflect(-v,n);
    float environment=.62+.32*max(reflected.y,0.0)+.12*max(reflected.z,0.0);
    vec3 diffuse=albedo*(.48+.52*max(dot(n,l),0.0))*(1.0-fresnel);
    vec3 result=(metal?vec3(0):diffuse)+fresnel*(environment+spec);
    result*=illumination;
    float emission=material.a<.999?material.a:0.0;
    result+=albedo*emission;
    return vec4(pow(clamp(result,0.0,1.0),vec3(1.0/2.2)),color.a);
}
