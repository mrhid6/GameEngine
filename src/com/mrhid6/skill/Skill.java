package com.mrhid6.skill;

import java.text.DecimalFormat;

public class Skill {

	static int minlevel = 1; // first level to display
	static int maxlevel = 200; // last level to display
	DecimalFormat df = new DecimalFormat("#.##########");
	
	private static int ATTACK_ID = 0;
	private static int STRENGTH_ID = 1;
	private static int DEFENCE_ID = 2;
	private static int RANGED_ID = 3;
	private static int MAGIC_ID = 4;

	private static int PRAYER_ID = 5;
	
	private static double[] skills_exp = new double[6];
	private static int[] skills_lvl = new int[6];
	
	public Skill(){
		
		skills_exp[0] = 150;
		skills_exp[1] = 120;
		skills_exp[2] = 120;
		skills_exp[3] = 120;
		skills_exp[4] = 120;
		skills_exp[5] = 120;
		
		loadSkillsToLvl();
		
		double hp_lvl = getHitPoints(getSkillExp(ATTACK_ID), getSkillExp(STRENGTH_ID), getSkillExp(DEFENCE_ID), getSkillExp(RANGED_ID), getSkillExp(MAGIC_ID));
		double combat_lvl = getCombatLevel(getSkillLvl(ATTACK_ID),getSkillLvl(STRENGTH_ID),getSkillLvl(DEFENCE_ID), hp_lvl, getSkillLvl(PRAYER_ID), getSkillLvl(RANGED_ID),getSkillLvl(MAGIC_ID));
		
		System.out.println(expToString(hp_lvl));
		System.out.println(expToString(combat_lvl));
	}
	
	private void loadSkillsToLvl(){
		for(int i=0;i<skills_lvl.length;i++){
			skills_lvl[i] = expToLevel(skills_exp[i]);
		}
	}
	
	private double getSkillExp(int index){
		return skills_exp[index];
	}
	
	private double getSkillLvl(int index){
		return skills_lvl[index];
	}


	private static double levelToExp(int level){

		if(level < 1) return 0;
		
		double points = 0;	
		for(int lvl = 1; lvl < level; lvl++){
			points += Math.floor(lvl + 300 * Math.pow(2, lvl / 7.));
		}
		
		return Math.floor(points/4);
	}
	
	private static int expToLevel(double exp){
		for(int i=0; i < maxlevel; i++){
			if(levelToExp(i) > exp){
				return (i-1);
			}
		}
		
		return 0;
	}
	
	private String expToString(double exp){
		return df.format(exp);
	}
	
	public static double getHitPoints(double attack, double strength, double defence, double ranged, double magic){
		double total_exp = attack + strength + defence + ranged + magic;
		double hp_exp = Math.floor(total_exp / 3.0) + 1154;
		
		int hitpoints_lvl = 0;
		
		for(int lvl = 1; lvl <= maxlevel; lvl++)
		{
			if(levelToExp(lvl) > hp_exp)
			{
				hitpoints_lvl = lvl-1;
				break;
			}
		}
		
		return hitpoints_lvl;
	}
	
	public static double getCombatLevel(double attack, double strength, double defence, double hitpoints, double prayer, double ranged, double magic){
		
		
		double combatLevel = ((defence + hitpoints) * 0.25) + ((magic + prayer) * 0.125);
		double warrior = (attack + strength) * 0.25;

		double ranger = ranged * 0.375;
		return Math.floor(combatLevel + (((ranged * 1.5) > (attack + strength)) ? ranger : warrior));

	}
}
