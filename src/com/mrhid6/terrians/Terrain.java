package com.mrhid6.terrians;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.models.RawModel;
import com.mrhid6.textures.TerrianTexture;
import com.mrhid6.textures.TerrianTexturePack;
import com.mrhid6.utils.Loader;
import com.mrhid6.utils.Maths;
import com.mrhid6.world.areas.WorldArea;

public class Terrain {
	
	public static final float SIZE = 512;
	public static final float  MAX_HEIGHT = 60;
	private static final float MAX_PIXLE_COLOUR = 256;
	
	private float x, z;
	private int gridX, gridZ;
	
	private RawModel model;
	private TerrianTexturePack texturePack;
	
	private float[][] heights;
	private String heightMap;
	
	public Terrain(String chunkFile){
		System.out.println("loading '"+chunkFile+"' using json..");
		loadTerrainConfig(chunkFile);
		
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		
		generateHeights();
	}
	
	private void loadTerrainConfig(String chunkFile){
		try {
			JSONObject jsonfile = Loader.getInstance().loadJSON(chunkFile);
			int areaid = jsonfile.getInt("areaid");
			this.gridX = jsonfile.getInt("gridX");
			this.gridZ = jsonfile.getInt("gridZ");
			
			String areaURL = WorldArea.getAreaURL(areaid);
			String terrainURL = areaURL+"/"+this.gridX+"_"+this.gridZ;
			
			String str_texture_base  = areaURL+"/"+jsonfile.getString("texture.base");
			String str_texture_red 	 = areaURL+"/"+jsonfile.getString("texture.red");
			String str_texture_green = areaURL+"/"+jsonfile.getString("texture.green");
			String str_texture_blue  = areaURL+"/"+jsonfile.getString("texture.blue");
			String str_texture_splat = terrainURL+"/"+jsonfile.getString("texture.splat");
			
			TerrianTexture texture_base  = new TerrianTexture(Loader.getInstance().loadTexture(str_texture_base));
			TerrianTexture texture_red   = new TerrianTexture(Loader.getInstance().loadTexture(str_texture_red));
			TerrianTexture texture_green = new TerrianTexture(Loader.getInstance().loadTexture(str_texture_green));
			TerrianTexture texture_blue  = new TerrianTexture(Loader.getInstance().loadTexture(str_texture_blue));
			TerrianTexture texture_splat = new TerrianTexture(Loader.getInstance().loadTexture(str_texture_splat));
			
			TerrianTexturePack ttp = new TerrianTexturePack(texture_base, texture_red, texture_green, texture_blue, texture_splat);
			this.texturePack = ttp;
			
			this.heightMap = terrainURL+"/"+jsonfile.getString("texture.height");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrianTexturePack getTexturePack() {
		return texturePack;
	}
	
	public float getHeightOfTerrain(float worldX, float worldZ){
		
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		
		if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0  || gridZ < 0){
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		
		float answer;
		
		if (xCoord <= (1-zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		
		return answer;
	}
	
	public float[][] getHeights() {
		return heights;
	}
	
	private void generateHeights(){
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(heightMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int VERTEX_COUNT = image.getHeight();
		
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				
				float height = getHeightImage(j, i, image);
				if(i==0 || i==VERTEX_COUNT-1 || j==0 || j==VERTEX_COUNT-1){
					
					//x edge
					if(j==0){
						Terrain leftTerrain = TerrainGrid.getInstance().getTerrian(this.x-1, this.z);
						if(leftTerrain!=null){
							float testHeight[][] = leftTerrain.getHeights();
							height=testHeight[VERTEX_COUNT-1][i];
						}
					}else if(j==VERTEX_COUNT-1){
						Terrain rightTerrain = TerrainGrid.getInstance().getTerrian(this.x+SIZE+1, this.z);
						if(rightTerrain!=null){
							float testHeight[][] = rightTerrain.getHeights();
							height=testHeight[0][i];
						}
					}
					
					if(i==0){
						Terrain topTerrain = TerrainGrid.getInstance().getTerrian(this.x, this.z-1);
						if(topTerrain!=null){
							float testHeight[][] = topTerrain.getHeights();
							height=testHeight[j][VERTEX_COUNT-1];
						}
					}else if(i==VERTEX_COUNT-1){
						Terrain bottomTerrain = TerrainGrid.getInstance().getTerrian(this.x, this.z+SIZE+1);
						if(bottomTerrain!=null){
							float testHeight[][] = bottomTerrain.getHeights();
							height=testHeight[j][0];
						}
					}
					
				}

				heights[j][i] = height;

			}
		}
	}

	public void generateTerrain(){
		
		if(model != null){
			Loader.getInstance().cleanUpRawModel(getModel());
			model = null;
		}
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(heightMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int VERTEX_COUNT = image.getHeight();
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT*1)];
		int vertexPointer = 0;
		
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		
		System.out.println("Terrain Created "+(vertices.length/3)+" Triangles");
		
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		this.model = Loader.getInstance().loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private Vector3f calculateNormal(int x, int z){
		
		float heightL = getHeight(x-1, z);
		float heightR = getHeight(x+1, z);
		float heightD = getHeight(x, z-1);
		float heightU = getHeight(x, z+1);
		
		int imageSize = heights.length;
		
		if(x==0 || x==imageSize-1 || z==0 || z==imageSize-1){
			if(x==0){
				Terrain leftTerrain = TerrainGrid.getInstance().getTerrian(this.x-1, this.z);
				if(leftTerrain!=null){
					float testHeight[][] = leftTerrain.getHeights();
					heightL=testHeight[imageSize-1][z];
				}
			}else if(x==imageSize-1){
				Terrain rightTerrain = TerrainGrid.getInstance().getTerrian(this.x+SIZE+1, this.z);
				if(rightTerrain!=null){
					float testHeight[][] = rightTerrain.getHeights();
					heightR=testHeight[0][z];
				}
			}
			
			if(z==0){
				Terrain topTerrain = TerrainGrid.getInstance().getTerrian(this.x, this.z-1);
				if(topTerrain!=null){
					float testHeight[][] = topTerrain.getHeights();
					heightD=testHeight[x][imageSize-1];
				}
			}else if(z==imageSize-1){
				Terrain bottomTerrain = TerrainGrid.getInstance().getTerrian(this.x, this.z+SIZE+1);
				if(bottomTerrain!=null){
					float testHeight[][] = bottomTerrain.getHeights();
					heightU=testHeight[x][0];
				}
			}
		}
		
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		
		normal.normalise();
		
		return normal;
	}
	
	private float getHeight(int x, int z){
		if(x<0 || x>=heights.length || z<0 || z>=heights.length){
			return 0;
		}
		
		return heights[x][z];
	}
	
	private float getHeightImage(int x, int z, BufferedImage image){
		if(x<0 || x>=image.getHeight() || z<0 || z>=image.getHeight()){
			return 0;
		}
		
		Color c = new Color(image.getRGB(x,z));
		
		float height = c.getRed();
		
		height+= MAX_PIXLE_COLOUR / 2f;
		height /= MAX_PIXLE_COLOUR / 2f;
		
		height *= MAX_HEIGHT/2;
		height -=60.00f;
		
		return height;
		
	}
	
	private float getSaveHeight(int x, int z){
		if(x<0 || x>=heights.length || z<0 || z>=heights.length){
			return 0;
		}
		
		float height = heights[x][z];
		height +=60.00f;
		
		height /= MAX_HEIGHT/2;
		height*= MAX_PIXLE_COLOUR / 2f;
		height -= MAX_PIXLE_COLOUR / 2f;
		
		return height;
		
	}
	
	public void saveTerrain(){
		try{
            BufferedImage img = new BufferedImage( 
                heights.length, heights.length, BufferedImage.TYPE_INT_RGB );

            File f = new File("res/TempImage.png");
            
            for(int i=0;i<heights.length;i++){
    			for(int j=0;j<heights.length;j++){
                	
                	float c = Maths.clampf(getSaveHeight(j, i)/255, 0.0f, 1.0f);
                	Color col = new Color(c,c,c);
                    img.setRGB(j, i, col.getRGB());
                }
            }
            ImageIO.write(img, "PNG", f);
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
	

	@Override
	public String toString() {
		return "Terrain [x=" + x + ", z=" + z + ", heightMap=" + heightMap + "]";
	}
	
	public void setHeights(float[][] heights) {
		this.heights = heights;
	}
	
}
