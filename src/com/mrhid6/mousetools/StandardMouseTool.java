package com.mrhid6.mousetools;

import org.lwjgl.input.Mouse;

import com.mrhid6.engineTester.MainGameLoop;
import com.mrhid6.entities.Player;

public class StandardMouseTool extends MouseTool{

	@Override
	public void update() {
		Player player = MainGameLoop.getInstance().getPlayer();
		
		if(Mouse.isButtonDown(0)){
			float angleChange= Mouse.getDX() * 0.3f;
			player.setRotY(player.getRotY()-angleChange);
		}

	}
}
