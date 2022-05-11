import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
 

public class Agent { 
	
	
	public int agentID;
	
	public int agentState;
	public int agentLastState;
	
	public double agentXCoordinate;
	public double agentYCoordinate;
	public HashMap agentNeighbors;
	public double agentActionUtilityTable[][];
	public int agentTimeStep;
	public double xScale = 0;
	public double yScale = 0;
	public double alpha = 0;
	public double beta = 0;
	public int actionInt = 0;
	public double epsilon = 0;



	public Agent(int size){
		
		agentID = 0;
		agentState = 0;
		agentLastState = 0;
		agentXCoordinate = agentYCoordinate = 0;
		agentNeighbors = new HashMap();
		agentActionUtilityTable = new double[size][6];
		agentTimeStep = 0;
		xScale = yScale = 0;	
		alpha = beta = 0;
		actionInt = 0;
		epsilon = 0.5;
		
	}
	
	
	
	public int getAgentID() {
		return agentID;
	}





	public void setAgentID(int agentID) {
		this.agentID = agentID;
	}




	public int getAgentState() {
		return agentState;
	}


	public int getAgentLastState() {
		return agentLastState;
	}



	public void setAgentLastState(int agentLastState) {
		this.agentLastState = agentLastState;
	}




	public void setAgentState(int agentState) {
		this.agentState = agentState;
	}





	public double getAgentXCoordinate() {
		return agentXCoordinate;
	}





	public void setAgentXCoordinate(double agentXCoordinate) {
		this.agentXCoordinate = agentXCoordinate;
	}





	public double getAgentYCoordinate() {
		return agentYCoordinate;
	}





	public void setAgentYCoordinate(double agentYCoordinate) {
		this.agentYCoordinate = agentYCoordinate;
	}





	public HashMap getAgentNeighbors() {
		return agentNeighbors;
	}

	
	public double getAlpha() {
		return alpha;
	}



	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}



	public double getBeta() {
		return beta;
	}



	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getEpsilon() {
		return epsilon;
	}



	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}


	public void setAgentNeighbors(HashMap agentNeighbors) {
		this.agentNeighbors = agentNeighbors;
	}





	public double[][] getAgentActionUtilityTable() {
		return agentActionUtilityTable;
	}





	public void setAgentActionUtilityTable(double[][] agentActionUtilityTable) {
		this.agentActionUtilityTable = agentActionUtilityTable;
	}





	public int getAgentTimeStep() {
		return agentTimeStep;
	}





	public void setAgentTimeStep(int agentTimeStep) {
		this.agentTimeStep = agentTimeStep;
	}
	
	
	


} 

