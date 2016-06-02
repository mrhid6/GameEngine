package com.mrhid6.irontide.mousetools;

import org.lwjgl.input.Mouse;

import com.mrhid6.irontide.engine.GameEngine;
import com.mrhid6.irontide.entities.Player;
import com.mrhid6.irontide.utils.MousePicker;
import com.mrhid6.irontide.world.World;

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
