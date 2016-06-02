package com.mrhid6.irontide.utils;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.render.ModelData;
 
public class OBJFileLoader {
     
 
    public static ModelData loadOBJ(InputStream in) {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        try {
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);
 
                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                processVertex(vertex1, vertices, indices);
                processVertex(vertex2, vertices, indices);
                processVertex(vertex3, vertices, indices);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Error reading the file");
            System.exit(-1);
        }
 
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
                furthest);
        return data;
    }
 
    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }
 
    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }
 
    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
            List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
            float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        return furthestPoint;
    }
 
    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
            int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
            }
 
        }
    }
    
	public static Matrix4f readTransformationMatrixFromString(String[] strMatrix, Matrix4f dest) {
		Matrix4f result = new Matrix4f();
		result.setIdentity();
		
		result.m00 = Float.valueOf(strMatrix[0]); result.m10 = Float.valueOf(strMatrix[1]); result.m20 = Float.valueOf(strMatrix[2]); result.m30 = Float.valueOf(strMatrix[3]);
		result.m01 = Float.valueOf(strMatrix[4]); result.m11 = Float.valueOf(strMatrix[5]); result.m21 = Float.valueOf(strMatrix[6]); result.m31 = Float.valueOf(strMatrix[7]);
		result.m02 = Float.valueOf(strMatrix[8]); result.m12 = Float.valueOf(strMatrix[9]); result.m22 = Float.valueOf(strMatrix[10]); result.m32 = Float.valueOf(strMatrix[11]);
		result.m03 = Float.valueOf(strMatrix[12]); result.m13 = Float.valueOf(strMatrix[13]); result.m23 = Float.valueOf(strMatrix[14]); result.m33 = Float.valueOf(strMatrix[15]);
		
		if (dest != null) {
			dest.m00 = result.m00;
			dest.m01 = result.m01;
			dest.m02 = result.m02;
			dest.m03 = result.m03;
			dest.m10 = result.m10;
			dest.m11 = result.m11;
			dest.m12 = result.m12;
			dest.m13 = result.m13;
			dest.m20 = result.m20;
			dest.m21 = result.m21;
			dest.m22 = result.m22;
			dest.m23 = result.m23;
			dest.m30 = result.m30;
			dest.m31 = result.m31;
			dest.m32 = result.m32;
			dest.m33 = result.m33;
		}
		
		return result;
	}
 
}