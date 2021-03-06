package com.mrhid6.irontide.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.mrhid6.irontide.log.Logger;
import com.mrhid6.irontide.models.RawModel;
import com.mrhid6.irontide.render.ModelData;
import com.mrhid6.irontide.settings.GameSettings;
import com.mrhid6.irontide.textures.ModelTexture;
import com.mrhid6.irontide.textures.TextureData;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Loader {

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private HashMap<String,Integer> textures = new HashMap<String,Integer>();

	private static Loader instance;

	/**
	 * Creates a new RawModel object from raw data
	 * @param positions - Vertex positions Float[]
	 * @param textureCoords - Texture coords Float[] 
	 * @param indices - Indices of vertex positions Int[]
	 * @return New RawModel
	 */

	public static Loader getInstance() {

		if(instance == null){
			instance = new Loader();
		}

		return instance;
	}

	public JSONObject loadJSONFR(InputStream in) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line=reader.readLine())!=null) {
			sb.append(line);
			sb.append("\n");
		}

		reader.close();

		String config = sb.toString();
		JSONObject obj = new JSONObject(config);
		return obj;
	}

	public JSONObject loadJSONFR(String JSONFile) throws IOException{
		return loadJSONFR(new FileInputStream(JSONFile));
	}

	public JSONObject loadJSON(String JSONFile) throws IOException{

		if(useInstallDir(JSONFile)){
			JSONFile = JSONFile.replace(GameSettings.INSTALLDIR, "");
			JSONFile = GameSettings.INSTALLDIR + JSONFile;
		}

		return loadJSONFR(JSONFile);
	}
	
	public RawModel loadToVAO(float[] positions, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public void loadToVAO(int vaoID, float[] positions, float[] textureCoords, float[] normals, int[] indices){

		GL30.glBindVertexArray(vaoID);
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);

		unbindVAO();
	}
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices){

		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);

		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public int loadToVAO(float[] positions, float[] textureCoords){
		
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		
		unbindVAO();
		return vaoID;
	}

	public RawModel loadToVAO(float[] positions, int dimensions){

		int vaoID = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();

		return new RawModel(vaoID,positions.length/dimensions);

	}

	public RawModel loadToVAO(float[] positions, float[] textureCoords, int[] indices){

		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		//storeDataInAttributeList(2, 3, normals);

		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public RawModel loadToVAO(ModelData data){
		return loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
	}
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices, float[] boneIDs, float[] boneWeights) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		storeDataInAttributeList(3, 4, boneIDs);
		storeDataInAttributeList(4, 4, boneWeights);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public RawModel loadObjAsset(InputStream in){
		ModelData data = OBJFileLoader.loadOBJ(in);
		RawModel rawModel = loadToVAO(data);

		return rawModel;
	}

	public RawModel loadObjAsset(String filename){

		InputStream in = null;

		if(useInstallDir(filename)){
			filename = GameSettings.INSTALLDIR + filename;
			try {
				in = new FileInputStream(filename);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else{
			in = Class.class.getResourceAsStream(filename);
		}

		return loadObjAsset(in);

	}


	public boolean useInstallDir(String Filename){
		if(!Filename.startsWith("/textures") && !Filename.startsWith("/shader")){
			return true;
		}
		return false;
	}

	public ModelTexture createModelTexture(String fileName) {
		return new ModelTexture(loadTexture(fileName));
	}
	
	/**
	 * 
	 * @param fileName - Texture File Location
	 * @return Texture ID
	 */
	public int loadTexture(String fileName){

		if(useInstallDir(fileName)){
			fileName = GameSettings.INSTALLDIR + fileName;
		}

		if(textures.containsKey(fileName)){
			Logger.info("using cached texture: "+fileName);
			return textures.get(fileName);
		}else{
			InputStream in = null;

			try {
				in = (useInstallDir(fileName))?new FileInputStream(fileName):Class.class.getResourceAsStream(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return loadTexture(fileName, in);
		}
	}
	
	public int loadSplatTexture(String fileName){
		if(useInstallDir(fileName)){
			fileName = GameSettings.INSTALLDIR + fileName;
		}

		if(textures.containsKey(fileName)){
			Logger.info("using cached texture: "+fileName);
			return textures.get(fileName);
		}else{
			InputStream in = null;

			try {
				in = (useInstallDir(fileName))?new FileInputStream(fileName):Class.class.getResourceAsStream(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Texture texture = null;
			try {
				texture = TextureLoader.getTexture("PNG", in);
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
				
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int textureID = texture.getTextureID();
			textures.put(fileName,textureID);

			return textureID;
		}
	}

	public int loadTexture(String identifier, InputStream in){
		if(textures.containsKey(identifier)){
			Logger.info("using cached texture: "+identifier);
			return textures.get(identifier);
		}else{
			Texture texture = null;
			try {
				texture = TextureLoader.getTexture("PNG", in);
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int textureID = texture.getTextureID();
			textures.put(identifier,textureID);

			return textureID;
		}
	}

	/**
	 * Deletes all OpenGl Arrays and Textures
	 */
	public void cleanUp(){
		for(int vao : vaos){
			GL30.glDeleteVertexArrays(vao);
		}

		for(int vbo : vbos){
			GL15.glDeleteBuffers(vbo);
		}

		for(String key : textures.keySet()){
			GL11.glDeleteTextures(textures.get(key));
		}
	}

	public void cleanUpRawModel(RawModel model){
		for(int i=0;i<vaos.size();i++){
			int vao = vaos.get(i);
			if(vao == model.getVaoID()){
				GL30.glDeleteVertexArrays(model.getVaoID());
				vaos.remove(i);
			}
		}
	}


	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);

		GL30.glBindVertexArray(vaoID);

		return vaoID;
	}

	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}

	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);

		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);

		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	public int loadCubeMap(String[] textureFiles){
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for(int i=0;i<textureFiles.length;i++){
			TextureData data = decodeTextureFile(textureFiles[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		textures.put("cubeMap_"+texID,texID);
		return texID;
	}

	private TextureData decodeTextureFile(String fileName) {

		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		
		if(useInstallDir(fileName)){
			fileName = GameSettings.INSTALLDIR + fileName;
		}
		
		try {
			InputStream in = (useInstallDir(fileName))?new FileInputStream(fileName):Class.class.getResourceAsStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}

}
