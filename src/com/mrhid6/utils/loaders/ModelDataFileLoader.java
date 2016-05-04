package com.mrhid6.utils.loaders;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mrhid6.collision.CollisionBox;
import com.mrhid6.collision.Triangle;
import com.mrhid6.render.ModelData;
import com.mrhid6.render.armature.Animation;
import com.mrhid6.render.armature.Armature;
import com.mrhid6.render.armature.Bone;
import com.mrhid6.utils.XMLUtil;


public class ModelDataFileLoader {

	private static final String RES_LOC = "res/models/";
	private static final String EXTENSION = ".mdat";
	
	public static ModelData loadMdatFromFile(String fileName) {
		InputStream objFile = Class.class.getResourceAsStream(fileName);
		Document doc = XMLUtil.loadDocument(objFile);
		
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		ArrayList<CollisionBox> collisionBoxes = new ArrayList<CollisionBox>();
		
		Armature armature = null;
		ArrayList<Animation> animations = new ArrayList<Animation>();
		
		Element eEntity = (Element) doc.getElementsByTagName("entity").item(0);
		
		/*GEOMETRY*/
		readGeometryNode(eEntity, vertices, textures, normals, indices);
		
		/*ARMATURE*/
		Element eArmature = (Element) eEntity.getElementsByTagName("armature").item(0);
		if (eArmature != null && eArmature.hasChildNodes()) {
			Element eRootBone = (Element) eArmature.getElementsByTagName("node").item(0);
			armature = new Armature();
			readJoint(eRootBone, null, armature);
			
			String[] nameArray = eArmature.getElementsByTagName("nameArray").item(0).getTextContent().split(" ");
			armature.setNameArray(nameArray);
			
			String[] strBSM = eArmature.getElementsByTagName("bind_shape_matrix").item(0).getTextContent().split(" ");
			Matrix4f bsm = new Matrix4f();
			readTransformationMatrixFromString(strBSM, bsm);
			armature.setBindShapeMatrix(bsm);
			
			String[] strInvs = eArmature.getElementsByTagName("invBindMatrix").item(0).getTextContent().split(" ");
			int count = strInvs.length / 16;
			for (int i = 0; i < count; i++) {
				StringBuilder strInv = new StringBuilder();
				for (int k = 0; k < 16; k++) {
					strInv.append(strInvs[i * 16 + k] + " ");
				}
				String[] split = strInv.toString().split(" ");
				Matrix4f inv = new Matrix4f();
				readTransformationMatrixFromString(split, inv);
				armature.getBone(nameArray[i]).setInvBindShapeMatrix(inv);
			}
			
			String[] strWeights = eArmature.getElementsByTagName("weights").item(0).getTextContent().split(" ");
			Element eVertexWeights = (Element) eArmature.getElementsByTagName("vertex_weights").item(0);
			int vertexCount = Integer.parseInt(eVertexWeights.getAttribute("count"));
			String[] strVCount = eVertexWeights.getElementsByTagName("vcount").item(0).getTextContent().split(" ");
			String[] strV = eVertexWeights.getElementsByTagName("v").item(0).getTextContent().split(" ");
			int index = 0;
			for (int i = 0; i < vertexCount; i++) {
				int nbInfluence = Integer.parseInt(strVCount[i]);
				Vertex currentVertex = vertices.get(i);
				for (int j = 0; j < nbInfluence; j++) {
					int jointIndex = Integer.parseInt(strV[index + 0]);
					int weightIndex = Integer.parseInt(strV[index + 1]);
					index += 2;
					currentVertex.addInfluence(jointIndex, Float.parseFloat(strWeights[weightIndex]));
				}
				currentVertex.fixInfluences();
			}
		}
		
		/*ANIMATIONS*/
		Element eAnimations = (Element) eEntity.getElementsByTagName("animations").item(0);
		if (eAnimations != null && eAnimations.hasChildNodes()) {
			NodeList animationNodes = eAnimations.getElementsByTagName("animation");
			for (int i = 0; i < animationNodes.getLength(); i++) {
				Element eAnimation = (Element) animationNodes.item(i);
				String animationName = eAnimation.getAttribute("name");
				String[] strKeyFrames = eAnimation.getElementsByTagName("keyFrames").item(0).getTextContent().split(" ");
				NodeList boneNodes = eAnimation.getElementsByTagName("boneMatrices");
				Animation animation = new Animation(animationName, strKeyFrames, !animationName.equalsIgnoreCase("ATTACK"), false, armature);
				for (int j = 0; j < boneNodes.getLength(); j++) {
					Element eBone = (Element) boneNodes.item(j);
					String boneName = eBone.getAttribute("name");
					String[] strMatrices = eBone.getTextContent().split(" ");
					animation.addBoneAnimation(boneName, strMatrices);
				}
				animations.add(animation);
			}
		}
		
		if (armature != null) {
			armature.calculateWorldMatrices();
		}
		
		/*COLLISION BOXES*/
		Element eCollisionBoxes = (Element) eEntity.getElementsByTagName("collisionBoxes").item(0);
		if (eCollisionBoxes != null && eCollisionBoxes.hasChildNodes()) {
			NodeList collisionBoxNodes = eCollisionBoxes.getElementsByTagName("collisionBox");
			for (int i = 0; i < collisionBoxNodes.getLength(); i++) {
				CollisionBox collisionBox = new CollisionBox();
				Element eCollisionBox = (Element) collisionBoxNodes.item(i);
				String[] positions = eCollisionBox.getElementsByTagName("positions").item(0).getTextContent().trim().split(" ");
				String[] inds = eCollisionBox.getElementsByTagName("p").item(0).getTextContent().trim().split(" ");
				float[] fltPositions = new float[positions.length];
				int[] intInds = new int[inds.length];
				ArrayList<Vector3f> verts = new ArrayList<Vector3f>();
				int pointer = 0;
				for (int j = 0; j < positions.length; j+=3) {
					float fltX = Float.parseFloat(positions[pointer++]);
					float fltY = Float.parseFloat(positions[pointer++]);
					float fltZ = Float.parseFloat(positions[pointer++]);
					Vector3f vert = new Vector3f(fltX, fltY, fltZ);
					verts.add(vert);
					fltPositions[j + 0] = fltX;
					fltPositions[j + 1] = fltY;
					fltPositions[j + 2] = fltZ;
				}
				pointer = 0;
				for (int j = 0; j < inds.length; j+=3) {
					int i1 = Integer.parseInt(inds[j + 0]);
					int i2 = Integer.parseInt(inds[j + 1]);
					int i3 = Integer.parseInt(inds[j + 2]);
					Triangle t = new Triangle(verts.get(i1), verts.get(i2), verts.get(i3));
					collisionBox.addTriangle(t);
					intInds[j + 0] = i1;
					intInds[j + 1] = i2;
					intInds[j + 2] = i3;
				}
				collisionBox.setData(fltPositions, intInds);
				collisionBoxes.add(collisionBox);
			}
		}
		
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float[] boneIDsArray = new float[vertices.size() * 4];
		float[] boneWeightsArray = new float[vertices.size() * 4];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
				texturesArray, normalsArray, boneIDsArray, boneWeightsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, boneIDsArray, boneWeightsArray, furthest, armature, animations, collisionBoxes);
		
		return data;
	}
	
	public static ModelData loadMdat(String entFileName) {
		return loadMdatFromFile(RES_LOC + entFileName + EXTENSION);
	}
	
	private static void readGeometryNode(Element eEntity, List<Vertex> vertices, List<Vector2f> textures,
			List<Vector3f> normals, List<Integer> indices) {
		Element eGeometry = (Element) eEntity.getElementsByTagName("geometry").item(0);
		
		Element ePositions = (Element) eGeometry.getElementsByTagName("positions").item(0);
		String[] strPositions = ePositions.getTextContent().split(" ");
		for (int i = 0; i < strPositions.length; i+=3) {
			Vector3f vertex = new Vector3f(
					(float) Float.valueOf(strPositions[i + 0]),
					(float) Float.valueOf(strPositions[i + 1]),
					(float) Float.valueOf(strPositions[i + 2]));
			Vertex newVertex = new Vertex(vertices.size(), vertex);
			vertices.add(newVertex);
		}
		
		Element eNormals = (Element) eGeometry.getElementsByTagName("normals").item(0);
		String[] strNormals = eNormals.getTextContent().split(" ");
		for (int i = 0; i < strNormals.length; i+=3) {
			Vector3f normal = new Vector3f(
					(float) Float.valueOf(strNormals[i + 0]),
					(float) Float.valueOf(strNormals[i + 1]),
					(float) Float.valueOf(strNormals[i + 2]));
			normals.add(normal);
		}
		
		Element eTextures = (Element) eGeometry.getElementsByTagName("textures").item(0);
		String[] strTextures = eTextures.getTextContent().split(" ");
		for (int i = 0; i < strTextures.length; i+=2) {
			Vector2f texture = new Vector2f(
					(float) Float.valueOf(strTextures[i + 0]),
					(float) Float.valueOf(strTextures[i + 1]));
			textures.add(texture);
		}
		
		Element eP = (Element) eGeometry.getElementsByTagName("p").item(0);
		String[] p = eP.getTextContent().split(" ");
		for (int i = 0; i < p.length; i+=9) {
			String[] vertex1 = { p[i + 0], p[i + 1], p[i + 2] };
			String[] vertex2 = { p[i + 3], p[i + 4], p[i + 5] };
			String[] vertex3 = { p[i + 6], p[i + 7], p[i + 8] };
			processVertexForANI(vertex1, vertices, indices);
			processVertexForANI(vertex2, vertices, indices);
			processVertexForANI(vertex3, vertices, indices);
		}
	}
	
	private static void readJoint(Element bone, Bone parent, Armature armature) {
		Bone joint = new Bone(armature);
		String id = bone.getAttribute("id");
		NodeList children = bone.getChildNodes();
		joint.setID(id);
		armature.addBone(joint);
		if (parent == null) {
			armature.setRootBone(joint.getID());
		} else {
			joint.setParent(parent.getID());
		}
		for (int i = 0; i < children.getLength(); i++) {
			if(children.item(i).getNodeType() == Node.ELEMENT_NODE){
				Element e = (Element) children.item(i);
				if (e.getAttribute("sid").equals("transform")) {
					String[] strMatrix = bone.getElementsByTagName("matrix").item(0).getTextContent().split(" ");
					Matrix4f matrix = new Matrix4f();
					readTransformationMatrixFromString(strMatrix, matrix);
					joint.setLocalTransform(matrix);
				} else if (e.getAttribute("type").equals("JOINT")) {
					readJoint((Element) children.item(i), joint, armature);
				}
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
	
	private static void processVertexForANI(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]);
		Vertex currentVertex = vertices.get(index);
		int normalIndex = Integer.parseInt(vertex[1]);
		int textureIndex = Integer.parseInt(vertex[2]);
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
			float[] normalsArray, float[] boneIDsArray, float[] boneWeightsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3 + 0] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2 + 0] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3 + 0] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
			
			currentVertex.fixInfluences();
			
			List<Integer> boneIDs = currentVertex.getBoneIDs();
			List<Float> boneWeights = currentVertex.getBoneWeights();
			
			for (int j = 0; j < 4; j++) {
				boneIDsArray[i * 4 + j] = boneIDs.get(j);
				boneWeightsArray[i * 4 + j] = boneWeights.get(j);
			}
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
}
