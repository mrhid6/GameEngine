package com.mrhid6.gameengine.obj.parser.mtl;

//import java.util.Hashtable;
import com.mrhid6.gameengine.obj.LineParserFactory;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.CommentParser;




public class MtlLineParserFactory extends LineParserFactory
{
	public MtlLineParserFactory(WavefrontObject object)
	{
		this.object = object;
		parsers.put("newmtl",new MaterialParser());
		parsers.put("Ka",new KaParser());
		parsers.put("Kd",new KdParser());
		parsers.put("Ks",new KsParser());
		parsers.put("Ns",new NsParser());		
		parsers.put("map_Kd",new KdMapParser(object));
		parsers.put("#",new CommentParser());
	}
	
	
		
	
		
	
}
