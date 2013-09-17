package com.mrhid6.gameengine.obj.parser.obj;

import com.mrhid6.gameengine.obj.Group;
import com.mrhid6.gameengine.obj.WavefrontObject;
import com.mrhid6.gameengine.obj.parser.LineParser;

public class GroupParser extends LineParser {

	Group newGroup = null;
	
	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		
		if (wavefrontObject.getCurrentGroup() != null)
			wavefrontObject.getCurrentGroup().pack();
		
		wavefrontObject.getGroups().add(newGroup);
		wavefrontObject.getGroupsDirectAccess().put(newGroup.getName(),newGroup);
		
		wavefrontObject.setCurrentGroup(newGroup);
	}

	@Override
	public void parse() {
		
		String groupName = words[1];
		newGroup = new Group(groupName);
	}

}
