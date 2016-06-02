package com.mrhid6.irontide.guis;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.entities.Player;
import com.mrhid6.irontide.font.GUIText;
import com.mrhid6.irontide.font.TextMaster;
import com.mrhid6.irontide.mousetools.MouseToolManager;
import com.mrhid6.irontide.settings.GameSettings;
import com.mrhid6.irontide.world.World;
import com.mrhid6io.utils.Utils;

public class GuiDebug {

	private int count = -1;
	
	private final float LINEHEIGHT = 18.0f;
	private final float DEBUGTOP = 120.0f;

	private int positionTextID;
	private int rotationTextID;
	private int mouseToolTextID;
	
	private ArrayList<GUIText> texts = new ArrayList<GUIText>();
	
	public GuiDebug() {
		positionTextID = addTextToDebug("X:0, Y:0, Z:0");
		rotationTextID = addTextToDebug("RotX:0, RotY:0, RotZ:0");
		mouseToolTextID = addTextToDebug("Selected Mouse:");
	}
	
	public int addTextToDebug(String text){
		
		float x = 5.0f;
		float y = DEBUGTOP - (count * LINEHEIGHT);
		
		GUIText temp = new GUIText(text, 24f, TextMaster.ARIAL, new Vector2f(x, GameSettings.HEIGHT-y), 0.3f, false);
		texts.add(temp);
		count++;
		return count;
	}

	public void update(){
		updatePosition();
		updateRotation();
		updateMouseTool();
	}

	private void updatePosition(){
		Vector3f pp = World.getInstance().getWorldPlayer().getPosition();
		GUIText temp = texts.get(positionTextID);
		
		float x = Utils.round(pp.getX(), 2);
		float y = Utils.round(pp.getY(), 2);
		float z = Utils.round(pp.getZ(), 2);

		String ppStr = "X:"+x+", Y:"+y+", Z:"+z;

		if(!temp.getTextString().equalsIgnoreCase(ppStr)){
			temp.setTextString(ppStr);
			TextMaster.updateText(temp);
		}
	}
	
	private void updateRotation(){
		Player p = World.getInstance().getWorldPlayer();
		GUIText temp = texts.get(rotationTextID);
		
		float x = Utils.round(p.getRotX(), 2);
		float y = Utils.round(p.getRotY(), 2);
		float z = Utils.round(p.getRotZ(), 2);

		String ppStr = "RotX:"+x+", RotY:"+y+", RotZ:"+z;

		if(!temp.getTextString().equalsIgnoreCase(ppStr)){
			temp.setTextString(ppStr);
			TextMaster.updateText(temp);
		}
	}
	
	private void updateMouseTool(){
		GUIText temp = texts.get(mouseToolTextID);
		
		String txt = "Selected Mouse: "+MouseToolManager.getInstance().getCurrentTool().getToolName();
		
		if(!temp.getTextString().equalsIgnoreCase(txt)){
			temp.setTextString(txt);
			TextMaster.updateText(temp);
		}
	}
}
