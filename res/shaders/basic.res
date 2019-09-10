#shader vertex
#version 400 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;
layout (location = 2) in vec3 in_normal;

out vec2 pass_texCoords;
out vec3 pass_surfaceNormal;
out vec3 pass_toLightVector[4];
out vec3 pass_toCameraVector;
out float pass_visibility;

uniform mat4 tfMat;
uniform mat4 projMat;
uniform mat4 viewMat;
uniform vec3 lightPosition[4];
uniform float useFakeLighting;

uniform float numberOfRows;
uniform vec2 offset;

const float density = 0.0035;
const float gradient = 5.0;

void main() {

	vec4 worldPosition = tfMat * vec4(in_position, 1.0);
	vec4 posRelCam = viewMat * worldPosition;

	gl_Position = projMat * posRelCam;

	pass_texCoords = (in_texCoord / numberOfRows) + offset;

	vec3 actualNormal = in_normal;
	if(useFakeLighting > 0.5) {
		actualNormal = vec3(0.0, 1.0, 0.0);
	}

	pass_surfaceNormal = (tfMat * vec4(actualNormal, 0.0)).xyz;
	// Note(Sora): Uncomment this to have light source travel with player
	// vec3 lightPosition2 = (tfMat * vec4(actualNormal, 0.0)).xyz;

	for(int i = 0; i < 4; i++) {
		pass_toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	pass_toCameraVector = (inverse(viewMat) * vec4(0.0, 0.0, 0.0, 1.0)).xyz;

	float distance = length(posRelCam.xyz);
	pass_visibility = exp(-pow((distance * density), gradient));
	pass_visibility = clamp(pass_visibility, 0.0, 1.0);

}

#shader fragment
#version 400 core

in vec2 pass_texCoords;
in vec3 pass_surfaceNormal;
in vec3 pass_toLightVector[4];
in vec3 pass_toCameraVector;
in float pass_visibility;

out vec4 out_colour;

uniform sampler2D modelTexture;
uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void) {

	vec3 unitNormal = normalize(pass_surfaceNormal);
	vec3 unitVectorToCamera = normalize(pass_toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for(int i = 0; i < 4; i++) {
		float distance = length(pass_toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);

		vec3 unitLightVector = normalize(pass_toLightVector[i]);

		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.0);

		vec3 lightDirection = -unitLightVector;
		vec3 reflLightDir = reflect(lightDirection, unitNormal);

		float specularFactor = dot(reflLightDir, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);

		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse += (brightness * lightColour[i]) / attFactor;
		totalSpecular += (dampedFactor * reflectivity * lightColour[i]) / attFactor;
	}

	totalDiffuse = max(totalDiffuse, 0.1);

	vec4 textureColour = texture(modelTexture, pass_texCoords);

	if(textureColour.a < 0.5) {
		discard;
	}

	out_colour = vec4(totalDiffuse, 1.0) * textureColour + vec4(totalSpecular, 1.0);
	out_colour = mix(vec4(skyColour, 1.0), out_colour, pass_visibility);

}







