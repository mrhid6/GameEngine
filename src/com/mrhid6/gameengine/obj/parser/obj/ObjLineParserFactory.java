package com.mrhid6.gameengine.obj.parser.obj;

//import java.util.Hashtable;
import com.mrhid6.gameengine.obj.LineParserFactory;
import com.mrhid6.gameengine.obj.NormalParser;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.CommentParser;
import com.mrhid6.gameengine.obj.parser.mtl.MaterialFileParser;


public class ObjLineParserFactory extends LineParserFactory{


	
	public ObjLineParserFactory(WavefrontObject object)
	{
		this.object = object;
		parsers.put("v",new VertexParser());
		parsers.put("vn",new NormalParser());
		parsers.put("vp",new FreeFormParser());
		parsers.put("vt",new TextureCooParser());
		parsers.put("f",new FaceParser(object));
		parsers.put("#",new CommentParser());
		parsers.put("mtllib",new MaterialFileParser(object));
		parsers.put("usemtl",new MaterialParser());
		parsers.put("g",new GroupParser());
	}

	
}
