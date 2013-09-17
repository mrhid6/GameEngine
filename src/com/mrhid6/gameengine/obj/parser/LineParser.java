package com.mrhid6.gameengine.obj.parser;

import com.mrhid6.gameengine.obj.WavefrontObject;

public abstract class LineParser 
{

	protected String[] words = null ;
	
	public abstract void incoporateResults(WavefrontObject wavefrontObject);
	public abstract void parse();
	
	public void setWords(String[] words)
	{
		this.words = words;
	}
		
}
