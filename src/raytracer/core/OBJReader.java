package raytracer.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import raytracer.core.def.Accelerator;
import raytracer.core.def.StandardObj;
import raytracer.geom.Sphere;
import raytracer.geom.GeomFactory;
import raytracer.geom.Primitive;
import raytracer.math.Point;
import raytracer.math.Vec3;

/**
 * Represents a model file reader for the OBJ format
 */
public class OBJReader {

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param filename
	 *                    The file to read the data from
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the filename is null or the empty string,
	 *                                  the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		read(new BufferedInputStream(new FileInputStream(filename)), accelerator, shader, scale, translate);
	}

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param in
	 *                    The InputStream of the data to be read.
	 * @param accelerator
	 *                    The target acceleration structure
	 * @param shader
	 *                    The shader which is used by all triangles
	 * @param scale
	 *                    The scale factor which is responsible for scaling the
	 *                    model
	 * @param translate
	 *                    A vector representing the translation coordinate with
	 *                    which
	 *                    all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *                                  If the InputStream is null, the accelerator
	 *                                  is null, the shader is null, the translate
	 *                                  vector is null,
	 *                                  the translate vector is not finite or scale
	 *                                  does not
	 *                                  represent a legal (finite) floating point
	 *                                  number
	 */
	public static void read(final InputStream in,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		if (in == null) {
			throw new FileNotFoundException();
		}
		Scanner scanner = new Scanner(in);
		scanner.useLocale(Locale.ENGLISH);

		List<Point> vertices = new ArrayList<>();
		List<int[]> faces = new ArrayList<>();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue; // Skip empty lines and comments
			}

			String[] tokens = line.split("\\s+");
			String type = tokens[0];

			if (type.equals("v")) {
				// Vertex position
				float x = Float.parseFloat(tokens[1]) * scale;
				float y = Float.parseFloat(tokens[2]) * scale;
				float z = Float.parseFloat(tokens[3]) * scale;
				Point pointToAdd = new Point(x, y, z).add(translate);

				// Add the vertex position to the list
				vertices.add(pointToAdd);
			} else if (type.equals("f")) {
				// Face definition
				int[] vertexIndices = new int[tokens.length - 1];
				for (int i = 1; i < tokens.length; i++) {
					vertexIndices[i - 1] = Integer.parseInt(tokens[i]) - 1; // Subtract 1 to convert to zero-based
																			// indexing
				}

				// Add the face indices to the list
				faces.add(vertexIndices);
			}
		}

		for (int[] face : faces) {

			Primitive triangle = GeomFactory.createTriangle(vertices.get(face[0]),
					vertices.get(face[1]),
					vertices.get(face[2]));

			StandardObj o = new StandardObj(triangle, shader);

			accelerator.add(o);

		}
		scanner.close();

	}
}
