package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Scene {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;
	private Matrix4f modelMatrix;

	private Shader shader;

	public Scene() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);

		modelMatrix = new Matrix4f();
		Matrix4f affineMatrix = new Matrix4f(); 
		
		Scene.translationMatrix(-0.6f, 0.6f, affineMatrix);
		modelMatrix.mul(affineMatrix);
		Scene.rotationMatrix((float) Math.toRadians(45f), affineMatrix);
		modelMatrix.mul(affineMatrix);
		Scene.scaleMatrix(0.5f, 0.5f, affineMatrix);
		modelMatrix.mul(affineMatrix);
	}

	public void draw()
	{		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_colour", colourBuffer);

		shader.setUniform("u_modelMatrix", modelMatrix);

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest)
	{
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 1 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest)
	{
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ cos(a) -sin(a) 0 0 ]
		// T = [ sin(a) cos(a)  0 0 ]
	    //     [ 0      0       1 0 ]
		//     [ 0      0       0 1 ]

		// Perform operations on only the x and y values of the i and j vecs. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		float sina = (float) Math.sin(angle);
		float cosa = (float) Math.cos(angle);
		
		dest.m00(cosa);
		dest.m01(sina);
		dest.m10(-sina);
		dest.m11(cosa);

		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest)
	{
		// clear the matrix to the identity matrix
		dest.identity();

		//	   [ sx 0  0 0 ]
		// T = [ 0  sy 0 0 ]
	    //     [ 0  0  0 0 ]
		//     [ 0  0  0 1 ]

		// Perform operations on only the x and y values of the i and j vecs. 
		// Leaves the z value alone, as we are only doing 2D transformations.

		dest.m00(sx);
		dest.m11(sy);

		return dest;
	}
}
