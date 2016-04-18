package com.mrhid6.mousetools;

import org.lwjgl.input.Mouse;

import com.mrhid6.engine.GameEngine;
import com.mrhid6.entities.Player;
import com.mrhid6.utils.MousePicker;
import com.mrhid6.world.World;

public class StandardMouseTool extends MouseTool{

	@Override
	public void update() {
		
		MousePicker picker = GameEngine.getInstance().getPicker();
		picker.update();
		Player player = World.getInstance().getWorldPlayer();
		
		if(Mouse.isButtonDown(0)){
			float angleChange= Mouse.getDX() * 0.3f;
			player.setRotY(player.getRotY()-angleChange);
		}

	}

	@Override
	public String getToolName() {
		return "Standard Tool";
	}
}
