/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.lib.mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetManager;

import com.pheiffware.lib.graphics.FatalGraphicsException;

/**
 * Holds a triangular mesh. Used to load raw mesh information from files.
 */
public class MeshLegacy
{

	public static Map<String, MeshLegacy> loadMeshesLegacy(AssetManager assetManager, String assetFileName) throws FatalGraphicsException
	{
		try
		{
			Map<String, MeshLegacy> meshMap = new HashMap<String, MeshLegacy>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(assetFileName), "UTF-8"));
			String meshType = reader.readLine();
			while (meshType != null)
			{
				if (meshType.equals("MESH_VNI"))
				{
					MeshLegacy meshLegacy = new MeshLegacy();
					meshLegacy.load(reader);
					meshMap.put(meshLegacy.ID, meshLegacy);
				}
				else
				{
					throw new FatalGraphicsException("Cannot load mesh of type: \"" + meshType + "\"");
				}
				meshType = reader.readLine();
			}
			return meshMap;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new FatalGraphicsException(e);
		}
		catch (IOException e)
		{
			throw new FatalGraphicsException(e);
		}
	}

	/**
	 * Creates an ugly, yet visible set of colors for a given number of vertices.
	 * 
	 * @return
	 */
	public static float[] generateMultiColorValues(int numVertices)
	{
		float[] colors = new float[numVertices * 4];
		for (int i = 0; i < numVertices; i++)
		{
			if ((i % 3) == 0)
			{
				colors[i * 4 + 0] = 1.0f;
				colors[i * 4 + 1] = 0.0f;
				colors[i * 4 + 2] = 0.0f;
				colors[i * 4 + 3] = 1.0f;
			}
			else if ((i % 3) == 1)
			{
				colors[i * 4 + 0] = 0.0f;
				colors[i * 4 + 1] = 1.0f;
				colors[i * 4 + 2] = 0.0f;
				colors[i * 4 + 3] = 1.0f;
			}
			else
			{
				colors[i * 4 + 0] = 0.0f;
				colors[i * 4 + 1] = 0.0f;
				colors[i * 4 + 2] = 1.0f;
				colors[i * 4 + 3] = 1.0f;
			}
		}
		return colors;
	}

	public String ID;
	public float[] vertices;
	public float[] normals;
	public short[] primitiveIndices;

	public MeshLegacy()
	{
	}

	public void load(BufferedReader reader) throws IOException
	{
		ID = reader.readLine();
		String numVerticesString = reader.readLine();
		int numVertices = Integer.valueOf(numVerticesString);
		vertices = new float[4 * numVertices];
		normals = new float[4 * numVertices];

		String vertexListString = reader.readLine();
		String[] vertexStrings = vertexListString.split(",");
		for (int i = 0; i < numVertices; i++)
		{
			vertices[i * 4 + 0] = Float.valueOf(vertexStrings[i * 3]);
			vertices[i * 4 + 1] = Float.valueOf(vertexStrings[i * 3 + 1]);
			vertices[i * 4 + 2] = Float.valueOf(vertexStrings[i * 3 + 2]);
			vertices[i * 4 + 3] = 1;
		}

		String normalListString = reader.readLine();
		String[] normalStrings = normalListString.split(",");
		for (int i = 0; i < numVertices; i++)
		{
			normals[i * 4 + 0] = Float.valueOf(normalStrings[i * 3]);
			normals[i * 4 + 1] = Float.valueOf(normalStrings[i * 3 + 1]);
			normals[i * 4 + 2] = Float.valueOf(normalStrings[i * 3 + 2]);
			normals[i * 4 + 3] = 1;
		}

		String numPrimitiveIndicesString = reader.readLine();
		int numPrimitiveIndices = Integer.valueOf(numPrimitiveIndicesString);
		primitiveIndices = new short[numPrimitiveIndices];

		String primitiveIndexListString = reader.readLine();
		String[] primitiveIndexStrings = primitiveIndexListString.split(",");
		for (int i = 0; i < numPrimitiveIndices; i++)
		{
			primitiveIndices[i] = Short.valueOf(primitiveIndexStrings[i]);
		}
	}

	/**
	 * Creates an ugly, yet visible set of colors for all vertices.
	 * 
	 * @return
	 */
	public float[] generateMultiColorValues()
	{
		return generateMultiColorValues(getNumVertices());
	}

	public int getNumVertices()
	{
		return vertices.length / 4;
	}

	public int getNumPrimitives()
	{
		return primitiveIndices.length;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Vertices:\n");
		for (int i = 0; i < vertices.length; i++)
		{
			builder.append(vertices[i]);
			builder.append(",");
		}
		builder.append("\n");
		builder.append("Normals:\n");
		for (int i = 0; i < normals.length; i++)
		{
			builder.append(normals[i]);
			builder.append(",");
		}
		builder.append("\n");
		builder.append("Primitive Indices:\n");
		for (int i = 0; i < primitiveIndices.length; i++)
		{
			builder.append(primitiveIndices[i]);
			builder.append(",");
		}
		builder.append("\n");
		return builder.toString();
	}
}
