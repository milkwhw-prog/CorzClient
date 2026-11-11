#version 150

out vec2 texCoord;

void main() {
    vec2 grid[6] = vec2[](
        vec2(-1.0, -1.0),
        vec2( 1.0, -1.0),
        vec2(-1.0,  1.0),
        vec2(-1.0,  1.0),
        vec2( 1.0, -1.0),
        vec2( 1.0,  1.0)
    );

    vec2 pos = grid[gl_VertexID];
    texCoord = pos * 0.5 + 0.5;

    gl_Position = vec4(pos, 1.0, 1.0);
}
