package com.mrhid6.gameengine.obj;




public class Material 
{
	public String texName;
	private float _shininess;
	private Vertex Ka;
	private Vertex Kd;
	private Vertex Ks;
	private String name;
	private Texture texture;
	
	public Material(String name)
	{
		Ka = null;
		Kd = null;
		Ks = null;
		texture = null;
		name = null;
		texName = null;
		_shininess = 0;
		
		this.name = name;
	}

	public Vertex getKa() 
	{
		return Ka;
	}
	
	public Vertex getKd() {
		return Kd;
	}
	
	public Vertex getKs() {
		return Ks;
	}

	public String getName() {
		return name;
	}

	public float getShininess()
	{
		return _shininess;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public void setKa(Vertex ka) {
		Ka = ka;
	}
	
	public void setKd(Vertex kd) {
		Kd = kd;
	}

	public void setKs(Vertex ks) {
		Ks = ks;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public void setShininess( float s )
	{
		_shininess = s;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	
}
