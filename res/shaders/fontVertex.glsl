#version 330

in vec2 positions;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform vec2 translation;

void main(void){
	
	gl_Position = vec4(positions + translation * vec2(2.0, -2.0), 0.0, 1.0);
	pass_textureCoords = textureCoords;
	
}