#version 410

in vec4 a_position;	// vertex position as a homogenous vector in NDC 
in vec3 a_colour; // vertex colour RGB

uniform mat4 u_modelMatrix; // model -> world transform

out vec3 v_colour; // to fragment shader

void main()
{
	v_colour = a_colour;
	
	vec4 p = u_modelMatrix * a_position;
	
	//a_position *= u_matrix;

	// pad the vertex to a homogeneous 3D point
    gl_Position = p;
}

