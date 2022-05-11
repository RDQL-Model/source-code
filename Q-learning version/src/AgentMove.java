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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 

public class AgentMove {
	
	
	public Agent agentRuning;
	public double epsilon;
	public int rowSize;
	public int columnize;
	public double agentUtilityChoices[];
	public int decsion = 0;
	public double xMin = 0;
	public double yMin = 0;
	

	public AgentMove(){
		
		epsilon = 0.5;
		rowSize = 2;
		columnize = 2;
		agentRuning = new Agent(1);
		agentUtilityChoices = new double[6];
		decsion = 0;
		
	}
	
    public AgentMove(Agent agent){
		
		epsilon = 0.3;
		rowSize = 2;
		columnize = 2;
		agentRuning = agent;
		agentUtilityChoices = new double[6];
		decsion = 0;
		
	}
    
    
    public AgentMove(Agent agent, double epsi){
		
		epsilon = epsi;
		rowSize = 2;
		columnize = 2;
		agentRuning = agent;
		agentUtilityChoices = new double[6];
		decsion = 0;
		
	}
	
	public void getagentUtilityChoicesValues() {
		
		for(int i=0;i<6;i++)
		
		agentUtilityChoices[i] = this.agentRuning.agentActionUtilityTable[this.agentRuning.getAgentState()-1][i];
		
		
		
	}
	
	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public Agent getAgentRuning() {
		return agentRuning;
	}

	public void setAgentRuning(Agent agentRuning) {
		this.agentRuning = agentRuning;
	}
	
	
	
	public int AgentChooseAction(){
		
		Random ra =new Random();
		Random raInt =new Random();
    	double pro = ra.nextDouble();
    	int decision = 5;
		double decisionValue = -1;
		
		this.getagentUtilityChoicesValues();
		
    	if(pro>this.agentRuning.epsilon){
    	    	   		
    		
    		for(int i=0;i<6;i++){
    			
    			if(this.agentUtilityChoices[i]>decisionValue){
    				
    				decisionValue = this.agentUtilityChoices[i];
    				decision = i;
    				
    			}
    			
    			
    			
    		}
    		
    		if(decision == -1){
   
    			decision = this.generateRandomDecison();
				
			}
    	}
    	
    	else{
    		
    		
    		decision = this.generateRandomDecison();
    		
    		
    	}
		
    	this.decsion = decision;
    	this.agentRuning.actionInt = decision;
		//System.out.println("decision:"+decision);
    	return decision;
    	
		
		
	}
	
	
	public int AgentChooseActionNew(){
		
		Random ra =new Random();
		Random raInt =new Random();
    	double pro = ra.nextDouble();
    	int decision = 5;
		double decisionValue = -1;
		
		this.getagentUtilityChoicesValues();
		
    	if(pro>this.epsilon){
    		
    	    	   		
    		
    		for(int i=0;i<6;i++){
    			
    			if(this.agentUtilityChoices[i]>decisionValue){
    				
    				decisionValue = this.agentUtilityChoices[i];
    				decision = i;
    				
    			}
    			
    			
    			
    		}
    		
    		if(decision == 5){
				

				decision = this.generateRandomDecison();
				
			}
    	}
    	
    	else{
    		
    		
    		decision = this.generateRandomDecison();
 		
    		
    	}
		
    	this.decsion = decision;
    	this.agentRuning.actionInt = decision;
		//System.out.println("decision:"+decision);
    	return decision;
    	
		
		
	}
	
	
	
	public int generateRandomDecison(){
		
		
		int randomDecison = 0;
		int rowsize = this.rowSize;
		
		int agentState = this.agentRuning.agentState;
		Random raInt =new Random();
		
		int markBreak = 1;
		
		
		for(int i=0;i<100;i++){
			

			randomDecison = raInt.nextInt(6);
			
			markBreak = 1;
			
			if((agentState % this.rowSize==1) && (randomDecison==3)){
				
				markBreak = 0;
				
			}
			
			
			
            if((agentState % this.rowSize==0) && (randomDecison==4)){
				
				markBreak = 0;
				
			}
			
            if(((agentState-1) / this.rowSize==0) && (randomDecison==2)){
				
				markBreak = 0;
				
			}
            
            if(((agentState-1) / this.rowSize==(this.columnize-1)) && (randomDecison==1)){
				
				markBreak = 0;
				
			}
            
            
			
			if(markBreak ==1)break;
			
			

		}
		
		
		
		return randomDecison;
		
		
		
		
		
		
	}
	
	
	
	public int generateRandomState(){
		
		int randomState = 0;
		int rowsize = this.rowSize;
		int columnsize = this.columnize;
		Random raInt =new Random();
		
		randomState = raInt.nextInt(rowsize*columnsize)+1;
		
		return randomState;
		
		
	}
	
	public void refreshAgentTimeStep(){
		
		
		this.agentRuning.agentTimeStep++;
		
		
	}
	
	
	public void refreshAgentState(){
		
		
		//int decisionValue = this.AgentChooseAction();
		
		int currentState = this.agentRuning.agentState;
		int nextState = 0;
		
		if(this.decsion==0){
			
			nextState = currentState;
		}
		
        if(this.decsion==1){
			
			nextState = currentState + this.rowSize;
		}

		if(this.decsion==2){
			
			nextState = currentState - this.rowSize;
		}
		
		if(this.decsion==3){
			
			nextState = currentState - 1;
		}
		
		if(this.decsion==4){
			
			nextState = currentState + 1;
		}
		
		this.agentRuning.agentState = nextState;
		this.agentRuning.agentLastState = currentState;
	}
	
	public void refreshAgentStateNew(){
		
		
		
		int currentState = this.agentRuning.agentState;
		int nextState = 0;
		
		if(this.decsion==0){
			
			nextState = currentState;
		}
		
        if(this.decsion==1){
			
			nextState = currentState + this.rowSize;
		}

		if(this.decsion==2){
			
			nextState = currentState - this.rowSize;
		}
		
		if(this.decsion==3){
			
			nextState = currentState - 1;
		}
		
		if(this.decsion==4){
			
			nextState = currentState + 1;
		}
		
        if(this.decsion==5){
			
			nextState = this.generateRandomState();
		}
		
		this.agentRuning.agentState = nextState;
		this.agentRuning.agentLastState = currentState;
	}
	
	public void refreshAgentCoordinate(){
		
		
		int xCoorInt = (this.agentRuning.agentState-1) % this.rowSize;
		int yCoorInt = (this.agentRuning.agentState-1) / this.rowSize;
		
		double xCoordouble = this.xMin + (xCoorInt)*this.agentRuning.xScale;
		//double yCoordouble = (yCoorInt+1)*this.agentRuning.yScale;
		double yCoordouble = this.yMin + (yCoorInt)*this.agentRuning.yScale;
		
		RegionRandom test = new RegionRandom();
		test.GetOneCoordinate(xCoordouble, yCoordouble, this.agentRuning.xScale, this.agentRuning.yScale);
		this.agentRuning.agentXCoordinate = test.xCoordinate;
		this.agentRuning.agentYCoordinate = test.yCoordinate;
		
				
		
	}
	
	

	
	
	public void refreshAgentActionUtilityTable(){
		
		
		
	}
	

	
	
	public void refreshAgentAllInformation(){
		
		
				
		/*this.AgentChooseAction();
		this.refreshAgentState();*/
		
		
		this.AgentChooseActionNew();
		this.refreshAgentStateNew();
		this.refreshAgentTimeStep();
		this.refreshAgentCoordinate();
		this.refreshAgentActionUtilityTable();
		
	   
		
	}
	
	
}