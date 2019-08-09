#version 330

out vec4 color;

uniform sampler2D u_material;

in DATA {
    vec2 texcoords;
    vec4 color;
} fi;

void main() {
    color = texture2D(u_material, fi.texcoords) * fi.color;
}