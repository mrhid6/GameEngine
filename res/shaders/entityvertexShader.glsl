#version 400 core

const int MAX_BONES = 50;
const int MAX_LIGHTS = 4;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec4 boneIDs;
in vec4 boneWeights;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[MAX_LIGHTS];
out vec3 toCameraVector;
out float visibility;

uniform float fogDensity;
uniform float fogGradient;

uniform mat4 bindShapeMatrix;
uniform mat4 boneTransforms[MAX_BONES];
uniform float isAnimated;

uniform mat4 fixPositionMatrix;
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];

uniform float useFakeLighting;

uniform float numberOfRows;
uniform vec2 offset;

uniform vec4 clipPlane;

void main(void) {
	vec4 actualPosition = vec4(position, 1.0);
	vec4 actualNormal = vec4(normal, 0.0);
	
	if (isAnimated >= 0.5) {
		actualPosition = bindShapeMatrix * actualPosition;
		vec4 finalPos = vec4(0.0);
		vec4 finalNormal = vec4(0.0);
		
		for (int i = 0; i < 4; i++) {
			finalPos += (boneTransforms[int(boneIDs[i])] * actualPosition) * boneWeights[i];
			finalNormal += (boneTransforms[int(boneIDs[i])] * actualNormal) * boneWeights[i];
		}
	
		finalNormal = normalize(finalNormal);
		actualNormal = fixPositionMatrix * finalNormal;
		actualPosition = fixPositionMatrix * finalPos;
	}
	
	vec4 worldPosition = transformationMatrix * actualPosition;
	
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = (textureCoords / numberOfRows) + offset;
	
	if (useFakeLighting >= 1) {
		actualNormal = vec4(0.0, 1.0, 0.0, 0.0);
	}
	
	surfaceNormal = (transformationMatrix * actualNormal).xyz;
	for (int i = 0; i < MAX_LIGHTS; i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * fogDensity), fogGradient));
	visibility = clamp(visibility, 0.0, 1.0);
}