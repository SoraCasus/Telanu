package com.soracasus.telanu.core;

public class StateManager {

	private static IGameLogic currentState = null;



	public static void setCurrentState(IGameLogic state) { currentState = state;}
	public static IGameLogic getCurrentState() { return currentState; }

}
