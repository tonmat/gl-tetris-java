#version 330

layout(location = 0) in vec4 position_texcoords;
layout(location = 1) in vec4 color;

uniform mat4 u_projection;

out DATA {
    vec2 texcoords;
    vec4 color;
} vo;

void main() {
    gl_Position = vec4(position_texcoords.xy, 0, 1) * u_projection;
    vo.texcoords = position_texcoords.zw;
    vo.color = color;
}