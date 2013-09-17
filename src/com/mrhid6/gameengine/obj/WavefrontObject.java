package com.mrhid6.gameengine.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;

import com.mrhid6.gameengine.obj.parser.LineParser;
import com.mrhid6.gameengine.obj.parser.obj.ObjLineParserFactory;



public class WavefrontObject{

	public int displayListId = 0;
	public int displayListRenderAllButId = 0;
	public int displayListRenderOnlylButId = 0;
	public String fileName;
	public double radius=0;
	public Vertex rotate; 
	public Vertex translate;
	
	public float xScale;

	public float yScale;
	
	public float zScale;
	
	private String contextfolder ="" ;
	
	private Group currentGroup;
	
	
	private Material currentMaterial; 
	//private ArrayList<Face> faces = new ArrayList<Face>();
	private ArrayList<Group> groups = new ArrayList<Group>();
	private Hashtable<String,Group> groupsDirectAccess = new Hashtable<String,Group>();
	
	private ArrayList<Vertex> normals = new ArrayList<Vertex>();
	private ObjLineParserFactory parserFactory ;
	
	private ArrayList<TextureCoordinate> textures = new ArrayList<TextureCoordinate>();
	
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	
	Hashtable<String, Material> materials = new Hashtable<String, Material>();
	
	public WavefrontObject(String fileName)
	{
		this(fileName,1f,1f,1f,new Vertex(),new Vertex());
	}
	
	public WavefrontObject(String fileName, float scale) 
	{	
		this(fileName,scale,scale,scale,new Vertex(),new Vertex());
	}
	
	public WavefrontObject(String fileName,float xScale, float yScale, float zScale)
	{
		this(fileName,xScale,yScale,zScale,new Vertex(),new Vertex());
	}


	public WavefrontObject(String fileName,float xScale, float yScale, float zScale, Vertex translation,Vertex rotation)
	{
		try
		{
			this.fileName = fileName;
			
			this.translate = translation;
			this.rotate= rotation;
			
			this.xScale= xScale;
			this.yScale= yScale;
			this.zScale= zScale;
			
			int lastSlashIndex = fileName.lastIndexOf('/');
			if ( lastSlashIndex != -1)
				this.contextfolder = fileName.substring(0,lastSlashIndex+1);
			
			lastSlashIndex = fileName.lastIndexOf('\\');
			if ( lastSlashIndex != -1)
				this.contextfolder = fileName.substring(0,lastSlashIndex+1);
			
			parse(fileName);
			
			calculateRadius();
		}
		catch(Exception e )
		{
			System.out.println("Error, could not load obj:"+fileName);
		}
	}


	public WavefrontObject(String fileName,float scale,Vertex translation,Vertex rotation)
	{
		this(fileName,scale,scale,scale,translation,rotation);
	}
	
	public void cleanUp() {
		GL11.glDeleteLists(displayListId, 1);
	}

	public String getBoudariesText() {
		
		float minX=0;
		float maxX=0;
		float minY=0;
		float maxY=0;
		float minZ=0;
		float maxZ=0;
		
		Vertex currentVertex = null;
		for (int i=0; i < getVertices().size(); i++)
		{
			currentVertex = getVertices().get(i);
			if (currentVertex.getX() > maxX) maxX = currentVertex.getX();
			if (currentVertex.getX() < minX) minX = currentVertex.getX();
			
			if (currentVertex.getY() > maxY) maxY = currentVertex.getY();
			if (currentVertex.getY() < minY) minY = currentVertex.getY();
			
			if (currentVertex.getZ() > maxZ) maxZ = currentVertex.getZ();
			if (currentVertex.getZ() < minZ) minZ = currentVertex.getZ();
			
		}
		
		return "maxX=" + maxX + " minX=" + minX +  " maxY=" +  maxY + " minY=" + minY+  " maxZ=" + maxZ+  " minZ=" + minZ;  
	}

	public String getContextfolder() {
		return contextfolder;
	} 
	public Group getCurrentGroup() {
		return currentGroup;
	}
	
	public Material getCurrentMaterial() {
		return currentMaterial;
	} 
	public ArrayList<Group> getGroups() {
		return groups;
	}
	
	public Hashtable<String, Group> getGroupsDirectAccess() {
		return groupsDirectAccess;
	} 
	public Hashtable<String, Material> getMaterials() {
		
		return this.materials;
	}

	
	public ArrayList<Vertex> getNormals() {
		return normals;
	}

	public ArrayList<TextureCoordinate> getTextures() {
		return textures;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public void parse(String fileName)  
	{
		parserFactory = new ObjLineParserFactory(this);
		
		
		InputStream fileInput = this.getClass().getResourceAsStream(fileName);
		if (fileInput == null)
			// Could not find the file in the jar.
			try
			{
				File file = new File(fileName);
				if (file.exists())
					fileInput = new FileInputStream(file);
			}
			catch(Exception e2)
			{
				e2.printStackTrace();
			}
		
		
		
		BufferedReader in = null;
		
		try 
		{
			in = new BufferedReader(new InputStreamReader(fileInput));
		
		
			String currentLine = null;
			while((currentLine = in.readLine()) != null)
				parseLine(currentLine);
				
			if (in != null)
				in.close();
		
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading file :'"+fileName+"'");
		}
		
	}

	public void printBoudariesText() {
		System.out.println(getBoudariesText());
		
	}

	public void render() {
		
		
		if (displayListId != 0)
		{
			GL11.glCallList(displayListId);
			return;
		}

		displayListId = GL11.glGenLists(1);
		
		GL11.glNewList(displayListId,GL11.GL_COMPILE);

		
		for (int i=0;i<getGroups().size()  ;i++) 
			renderGroup(getGroups().get(i));
		
		GL11.glEndList();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
	}



	public void renderAllBut(String[] groupNames) 
	{
		
		if (displayListRenderAllButId != 0)
		{
			GL11.glCallList(displayListRenderAllButId);
			return;
		}

		displayListRenderAllButId = GL11.glGenLists(1);
		
		GL11.glNewList(displayListRenderAllButId,GL11.GL_COMPILE);
			ArrayList<Group> toRender = new ArrayList<Group>();
			
			for (int i = 0; i < getGroups().size(); i++)
			{
				boolean found = false;
				for (int j=0;j<groupNames.length && !found;j++)
					if (groupNames[j].equals(getGroups().get(i).getName()))
						found =true;
				if (!found)
					toRender.add(getGroups().get(i));
			}	
			
			for (int x=0;x<toRender.size()  ;x++)
				renderGroup(toRender.get(x));
		GL11.glEndList();
	}

	public void renderOnly(String[] groupNames) 
	{
		
		if (displayListRenderOnlylButId != 0)
		{
			GL11.glCallList(displayListRenderOnlylButId);
			return;
		}

		displayListRenderOnlylButId = GL11.glGenLists(1);
		
		GL11.glNewList(displayListRenderOnlylButId,GL11.GL_COMPILE);

		ArrayList<Group> toRender = new ArrayList<Group>();
		
		for (int i = 0; i < getGroups().size(); i++)
		{
			for (int j=0;j<groupNames.length;j++)
				if (groupNames[j].equals(getGroups().get(i).getName()))
					toRender.add(getGroups().get(i));
		}	
		
		for (int x=0;x<toRender.size()  ;x++)
			renderGroup(toRender.get(x));
			
		GL11.glEndList();
	}

	public void setCurrentGroup(Group currentGroup) {
		this.currentGroup = currentGroup;
	}

	public void setCurrentMaterial(Material currentMaterial )
	{
		this.currentMaterial= currentMaterial;
	}
	
	public void setMaterials(Hashtable<String, Material> materials) {
		this.materials = materials;
	}


	public void setNormals(ArrayList<Vertex> normals) {
		this.normals = normals;
	}


	public void setTextures(ArrayList<TextureCoordinate> textures) {
		this.textures = textures;
	}


	public void setVertices(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
	}


	private void calculateRadius() {
		double currentNorm = 0;
		for(Vertex vertex : vertices)
		{
			currentNorm = vertex.norm(); 
			if ( currentNorm> radius )
				radius = currentNorm;
		}
		
	}

	private void parseLine(String currentLine) {
		
		//System.out.println("Parsing line:"+lineCounter);
		if ("".equals(currentLine))
			return;
		
		LineParser parser = parserFactory.getLineParser(currentLine);
		parser.parse();
		parser.incoporateResults(this);
	}

	protected void renderGroup(Group group) {
		
		currentGroup = group;
		
		if (currentGroup.getName().equals("TIKA011") || currentGroup.getName().equals("TIKA0115") )
			GL11.glColor4f(1,1,1, 0);
		else
			GL11.glColor4f(1,1,1, 1);
		
		currentMaterial = currentGroup.getMaterial();
		
		if (currentMaterial!=null && currentMaterial.getTexture() != null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentMaterial.getTexture().getTextureID());
		
		Face currentFace = null;
		for (int i=0;i<currentGroup.getFaces().size();i++)
		{
			currentFace = currentGroup.getFaces().get(i);
			GL11.glBegin(GL11.GL_TRIANGLES);
			Vertex vertex = null;
			Vertex normal = null;
			TextureCoordinate textureCoo = null;
			for (int j=0;j<currentFace.getVertices().length;j++)
			{
				vertex = currentFace.getVertices()[j];
				if (j < currentFace.getNormals().length &&  currentFace.getNormals()[j] != null)
				{
					normal = currentFace.getNormals()[j];
					GL11.glNormal3f(normal.getX(),normal.getY(),normal.getZ());
				}
				
				if (currentMaterial!=null && currentMaterial.getTexture()!= null && j < currentFace.getTextures().length && currentFace.getTextures()[j] != null )
				{
					textureCoo = currentFace.getTextures()[j];
					GL11.glTexCoord2f(textureCoo.getU(),textureCoo.getV());
				}
				
				GL11.glVertex3f(vertex.getX(),vertex.getY(),vertex.getZ());
			}
			GL11.glEnd(); 
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
	}
}
