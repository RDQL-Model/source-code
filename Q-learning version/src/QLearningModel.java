import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.*; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 

public class QLearningModel {
	
	
	public Vector agentVec;
	public int agentNumEachRegion;
	public int agentSize;
	public int rowSize;
	public int columnize;
	public HashMap stateToNodeSetMap = new HashMap(); 
	public int stateNumber = 0;
    public double epsilon = 0.5;
    public double xMinimize = 0;
    public double yMinimize = 0;
    public double xMaximize = 0;
    public double yMaximize = 0;
    public double xScale = 0;
    public double yScale = 0;
	public Vector initialCoordinateVec;
	public Vector stateChangeProbabilty;
	

	
	
	public QLearningModel(int num, int rowSize, int columS){
		
		this.agentNumEachRegion = num;
		this.rowSize = rowSize;
		this.columnize = columS;
		agentVec = new Vector();
		initialCoordinateVec = new Vector();
		stateChangeProbabilty = new Vector();
		
	}
	
	
	public void InitializatioModelUniform(double xMin, double xMax, double yMin, double yMax){
		
		
		
		this.xMinimize = xMin;
		this.yMinimize = yMin;
		
		
		
		double xScale = xMax - xMin;
		double yScale = yMax - yMin;
		
		double xScaleEachOrigin = 0;
		double yScaleEachOrigin = 0;
	
		if(xScale % rowSize ==0){
			
			xScaleEachOrigin = xScale/rowSize;
			
		}
		else {
			
			xScaleEachOrigin = xScale/rowSize;
			this.rowSize++;
			
			
		}
		
		
        if(xScale % columnize ==0){
			
        	yScaleEachOrigin = yScale/columnize;
			
			
			
		}
		else {
			
			
			yScaleEachOrigin = yScale/columnize;
			this.columnize++;
		}
		
        
		
        int stateNum = this.rowSize*this.columnize;
        this.stateNumber = stateNum;
        double agentInitialActionUtilityTable[][] = new double[stateNum][6];
        for(int i=0;i<stateNum;i++){
        	
        	for(int j=0;j<6;j++){
        		
        		agentInitialActionUtilityTable[i][j] = -1;
        		
        	}
        	
        }
        
        int agentID = 0;
        int agentTime = 0;
        double parameter1 = 0.5;
        double parameter2 = 0.2;
       
        
        agentTime++;
        
        for(int state=1;state<=stateNum;state++){
        	
        	
        	for(int i=0;i<agentNumEachRegion;i++){
        		
        		agentID++;
        		
        		Agent oneNewAgent = new Agent(stateNum);
        		oneNewAgent.setAgentID(agentID);
        		oneNewAgent.setAgentState(state);
        		oneNewAgent.setAgentTimeStep(agentTime);
        		
        		
        		oneNewAgent.setAgentActionUtilityTable(agentInitialActionUtilityTable);
        		oneNewAgent.setAlpha(parameter1);
        		oneNewAgent.setBeta(parameter2);
        		    		
        		double xInitialCooridate = xMin + ((state-1)%this.rowSize)*xScaleEachOrigin;
        		//double yInitialCooridate = yMin + (state/this.rowSize-1)*yScaleEachOrigin;
        		double yInitialCooridate = yMin + ((state-1)/this.rowSize)*yScaleEachOrigin;
        		
        		RegionRandom test = new RegionRandom();
        		test.GetOneCoordinate(xInitialCooridate, yInitialCooridate, xScaleEachOrigin, yScaleEachOrigin);
        		oneNewAgent.agentXCoordinate = test.xCoordinate;
        		oneNewAgent.agentYCoordinate = test.yCoordinate;
        		oneNewAgent.xScale = xScaleEachOrigin;
        		oneNewAgent.yScale = yScaleEachOrigin;
        	
        		
        		
        		this.agentVec.add(oneNewAgent);
        		

        		if(this.stateToNodeSetMap.containsKey(state)){
        			
        			Vector vec = new Vector();
        			vec = (Vector)this.stateToNodeSetMap.get(state);
        			vec.add(oneNewAgent.agentID);
        			this.stateToNodeSetMap.put(state, vec);
        			
        			
        		}
        		
        		else{
        			
        			Vector vecNew = new Vector();
        			vecNew.add(oneNewAgent.agentID);
        			this.stateToNodeSetMap.put(state, vecNew);
        			
        			
        		}
        		
        	}
        	
        	
        	
        }
        
        
        
        
        
		
		
		
		
	}
	
	
	public void InitializatioModelUniformAlphaGema(double xMin, double xMax, double yMin, double yMax, double alpha, double gamma){//初始化每个节点的属性，初始设定为均匀分布
		
		this.xMinimize = xMin;
		this.yMinimize = yMin;
		
		
		
		double xScale = xMax - xMin;
		double yScale = yMax - yMin;
		
		double xScaleEachOrigin = 0;
		double yScaleEachOrigin = 0;
		
		if(xScale % rowSize ==0){
			
			xScaleEachOrigin = xScale/rowSize;
			
		}
		else {
			
			xScaleEachOrigin = xScale/rowSize;
			this.rowSize++;
			
			
		}
		
		
        if(xScale % columnize ==0){
			
        	yScaleEachOrigin = yScale/columnize;
			
			
			
		}
		else {
			
			
			yScaleEachOrigin = yScale/columnize;
			this.columnize++;
		}
		
		
        int stateNum = this.rowSize*this.columnize;
        this.stateNumber = stateNum;
        double agentInitialActionUtilityTable[][] = new double[stateNum][6];
        for(int i=0;i<stateNum;i++){
        	
        	for(int j=0;j<6;j++){
        		
        		agentInitialActionUtilityTable[i][j] = -1;
        		
        	}
        	
        }
        
        int agentID = 0;
        int agentTime = 0;
        double parameter1 = alpha;
        double parameter2 = gamma;
        
        agentTime++;
        
        for(int state=1;state<=stateNum;state++){
        	
        	
        	for(int i=0;i<agentNumEachRegion;i++){
        		
        		agentID++;
        		
        		Agent oneNewAgent = new Agent(stateNum);
        		oneNewAgent.setAgentID(agentID);
        		oneNewAgent.setAgentState(state);
        		oneNewAgent.setAgentTimeStep(agentTime);
        		oneNewAgent.setAgentActionUtilityTable(agentInitialActionUtilityTable);
        		oneNewAgent.setAlpha(parameter1);
        		oneNewAgent.setBeta(parameter2);
        		    		
        		double xInitialCooridate = xMin + ((state-1)%this.rowSize)*xScaleEachOrigin;
        		//double yInitialCooridate = yMin + (state/this.rowSize-1)*yScaleEachOrigin;
        		double yInitialCooridate = yMin + ((state-1)/this.rowSize)*yScaleEachOrigin;
        		
        		RegionRandom test = new RegionRandom();
        		test.GetOneCoordinate(xInitialCooridate, yInitialCooridate, xScaleEachOrigin, yScaleEachOrigin);
        		oneNewAgent.agentXCoordinate = test.xCoordinate;
        		oneNewAgent.agentYCoordinate = test.yCoordinate;
        		oneNewAgent.xScale = xScaleEachOrigin;
        		oneNewAgent.yScale = yScaleEachOrigin;
        	
        		
        		
        		this.agentVec.add(oneNewAgent);
        		if(this.stateToNodeSetMap.containsKey(state)){
        			
        			Vector vec = new Vector();
        			vec = (Vector)this.stateToNodeSetMap.get(state);
        			vec.add(oneNewAgent.agentID);
        			this.stateToNodeSetMap.put(state, vec);
        			
        			
        		}
        		
        		else{
        			
        			Vector vecNew = new Vector();
        			vecNew.add(oneNewAgent.agentID);
        			this.stateToNodeSetMap.put(state, vecNew);
        			
        			
        		}
        		
        	}
        	
        	
        	
        }
        
        
        
        
        
		
		
		
		
	}
	
 
	
	
   public void getFourCoordinateValues(String path){
 	  
 	  double xMin = 0;
 	  double xMax = 0;
 	  double yMin = 0;
 	  double yMax = 0;
 	  
 	  LineNumberReader reader = null;   
      FileReader in = null; 
      String paris[] = new String[10];
      int count = 0;
       
       try {   
           in = new FileReader(path);   
           reader = new LineNumberReader(in);
           
           while (true) {
           	 String str = reader.readLine();  
           	 count++;
              if (str == null)   
              break;   
               
              this.initialCoordinateVec.add(str);
              //System.out.println(str);
              paris = str.split("\t"); 
              double xCoordinate = Double.parseDouble(paris[1]);
              double yCoordinate = Double.parseDouble(paris[2]);
              
              if(count==1){
            	   
            	   xMin = xMax = xCoordinate;
            	   yMin = yMax = yCoordinate;
            	   continue;
            	   
              }
               
               
               if(xMin > xCoordinate) xMin = xCoordinate;
               if(xMax < xCoordinate) xMax = xCoordinate;
               if(yMin > yCoordinate) yMin = yCoordinate;
               if(yMax < yCoordinate) yMax = yCoordinate;
        
               

           }
           
           this.xMinimize = xMin;
           this.xMaximize = xMax;
           this.yMinimize = yMin;
           this.yMaximize = yMax;
           

       } catch (FileNotFoundException e) {   
           e.printStackTrace();   
       } catch (IOException e) {   
           e.printStackTrace();   
       } finally {   
           try {   
               if (reader != null)   
                   reader.close();   
           } catch (IOException e) {   
               e.printStackTrace();   
           }   
           try {   
               if (in != null)   
                   in.close();   
           } catch (IOException e) {   
               e.printStackTrace();   
           }   
       } 
  	  
   }
 	
 	
 	
    public void InitializatioModelInputFile(String path){
 
 		this.getFourCoordinateValues(path);
 		
 		
 		double xScale = this.xMaximize - this.xMinimize;
 		double yScale = this.yMaximize - this.yMinimize;
 		
 		double xScaleEachOrigin = 0;
 		double yScaleEachOrigin = 0;
 		
 		if(xScale % rowSize ==0){
 			
 			xScaleEachOrigin = xScale/rowSize;
 			
 		}
 		else {
 			
 			xScaleEachOrigin = xScale/rowSize;
 			this.rowSize++;
 			
 		}
 		
 		
         if(xScale % columnize ==0){
 			
         	yScaleEachOrigin = yScale/columnize;
 			
 			
 		}
 		else {
 						
 			yScaleEachOrigin = yScale/columnize;
 			this.columnize++;
 		}
 		
 		
         int stateNum = this.rowSize*this.columnize;
         //stateNum++;
         this.stateNumber = stateNum;
         double agentInitialActionUtilityTable[][] = new double[stateNum][6];
         for(int i=0;i<stateNum;i++){
         	
         	for(int j=0;j<6;j++){
         		
         		agentInitialActionUtilityTable[i][j] = -1;
         		
         	}
         	
         }
         
         int agentID = 0;
         int agentTime = 0;
         double parameter1 = 0.3;
         double parameter2 = 0.5;
         agentTime++;
 	   
         
         String paris[] = new String[10];
         
         
 		for(int i=0;i<this.initialCoordinateVec.size();i++){
 			
 			String oneAgentStr = this.initialCoordinateVec.get(i).toString();
 			
 			paris = oneAgentStr.split("\t");
 			
 			agentID = Integer.parseInt(paris[0]);
 			double xCooridate = Double.parseDouble(paris[1]);
 			double yCooridate = Double.parseDouble(paris[2]);
 			
 			int agentWidth = (int)((xCooridate-this.xMinimize)/xScaleEachOrigin) + 1;
 			int agentHeigh = (int)((yCooridate-this.yMinimize)/yScaleEachOrigin);
 			int agentState = agentHeigh*this.rowSize + agentWidth;

     		
     		Agent oneNewAgent = new Agent(stateNum);
     		
     		oneNewAgent.setAgentID(agentID);
     		oneNewAgent.setAgentState(agentState);
     		oneNewAgent.setAgentTimeStep(agentTime);
     		oneNewAgent.setAgentActionUtilityTable(agentInitialActionUtilityTable);
     		oneNewAgent.setAlpha(parameter1);
     		oneNewAgent.setBeta(parameter2);
     		
     		oneNewAgent.agentXCoordinate = xCooridate;
     		oneNewAgent.agentYCoordinate = yCooridate;
     		oneNewAgent.xScale = xScaleEachOrigin;
     		oneNewAgent.yScale = yScaleEachOrigin;
 			
     		this.agentVec.add(oneNewAgent);

     		if(this.stateToNodeSetMap.containsKey(agentState)){
     			
     			Vector vec = new Vector();
     			vec = (Vector)this.stateToNodeSetMap.get(agentState);
     			vec.add(oneNewAgent.agentID);
     			this.stateToNodeSetMap.put(agentState, vec);
     			
     			
     		}
     		
     		else{
     			
     			Vector vecNew = new Vector();
     			vecNew.add(oneNewAgent.agentID);
     			this.stateToNodeSetMap.put(agentState, vecNew);
     			
     			
     		}
 			
 		}
 		
 		
 		for(int state=1;state<=stateNum;state++){
 			
 			Vector vec = new Vector();
 			if(!this.stateToNodeSetMap.containsKey(state))this.stateToNodeSetMap.put(state, vec);
 			
 			
 		}
 		
 		
 	}
 	
    
    public void loadUtilityTableForEachAgent(String untilityTablePath){
 	   
 		LineNumberReader reader = null;   
 		FileReader in = null;
 		String paris[] = new String[30];
 		int allitems = 0;
 		double agentInitialActionUtilityTable[][] = new double[this.stateNumber][6];
 		int count = 0;
 		
 		 String agentIDStr = "";
   		 int agentID = 0;
 		
         for(int i=0;i<this.stateNumber;i++){
         	
         	for(int j=0;j<6;j++){
         		
         		agentInitialActionUtilityTable[i][j] = -1;
         		
         	}
         	
         }
 		
 		
 		try {


 			in = new FileReader(untilityTablePath); 
 			reader = new LineNumberReader(in); 
 			
 			while (true) {
 				
 		      	 String str = reader.readLine(); 
 		      	 if (str == null)   
                    break;
 		      	 allitems++;
 		      	 
 		      	 if(str.contains("*")){
 		      		 
 		      		 agentIDStr = str.replace("*", "");
 		      		 agentID = Integer.parseInt(agentIDStr);
 		      		 int idVec = agentID-1;
 		      		 Agent oneSelectAgent = (Agent)this.agentVec.get(idVec);
 		      		 oneSelectAgent.setAgentActionUtilityTable(agentInitialActionUtilityTable);
 		      		 this.agentVec.setElementAt(oneSelectAgent, idVec);
 		      		 count = 0;
 		      		 		      		 
 		      	 }
 		      	 
 		      	 else{
 		      		 
 		      		paris = str.split("\t");  	 
 			    	
 			         for(int j=0;j<5;j++){
 			        		
 			        	double utilityValue = Double.parseDouble(paris[j]);
 			            agentInitialActionUtilityTable[count][j] = utilityValue;
 			        		
 			        	}
 			         
 			         count++;
 			        	
 			        }
 		      		 
 		      	 }
 		      	 
 		    	 
 		      	 
 		      	 
 		      	 	
 		      
 		  }
 		
 		
		 catch (FileNotFoundException e) {   
		      e.printStackTrace();   
		  } 
 		
 		 catch (IOException e) {   
		      e.printStackTrace();   
		  } 
 		
 		 finally {   
		      try {   
		          if (reader != null)   
		              reader.close();   
		      } catch (IOException e) {   
		          e.printStackTrace();   
		      }   
		      try {   
		          if (in != null)   
		              in.close();   
		      } catch (IOException e) {   
		          e.printStackTrace();   
		      }   
		      
		  
		
		  }
 	   
 	   
 }
    
    
    public void loadEpsilonValueForEachAgent(String epsilonPath){

 		LineNumberReader reader = null;   
 		FileReader in = null;
 		String paris[] = new String[30];
 		int allitems = 0;
 		double epsilonValue = 0;
 		int count = 0;

 		try {
 			

 			in = new FileReader(epsilonPath); 
 			reader = new LineNumberReader(in); 
 			
 			while (true) {
 				
 		      	 String str = reader.readLine(); 
 		      	 if (str == null)   
                   break;
 		      	 allitems++;
 		      	 paris = str.split("\t"); 
 		      	 int agentID = Integer.parseInt(paris[0]);
 		      	 epsilonValue = Double.parseDouble(paris[1]);
 		      	 
 	      		 int idVec = agentID-1;
 	      		 Agent oneSelectAgent = (Agent)this.agentVec.get(idVec);
 	      		 oneSelectAgent.setEpsilon(epsilonValue);
 	      		 this.agentVec.setElementAt(oneSelectAgent, idVec);
 	      		 
 		      	 	 
 			}
 		    	 
 		}
 		
	 	catch (FileNotFoundException e) {   
			      e.printStackTrace();   
			     } 
 		
 		catch (IOException e) {   
			     e.printStackTrace();   
			     } 
 		
 		finally {   
			     try {   
			         if (reader != null)   
			             reader.close();   
			     } catch (IOException e) {   
			         e.printStackTrace();   
			     }   
			     try {   
			         if (in != null)   
			             in.close();   
			     } catch (IOException e) {   
			         e.printStackTrace();   
			     }   
	     
	
	   }
 	   
 	   
 	   
  }
    
	
	public void refreshAgentNeighbors(){
		
		
			Iterator iter = this.stateToNodeSetMap.entrySet().iterator();
		    while (iter.hasNext()) {
		    	
		          Map.Entry entry = (Map.Entry) iter.next();
		          Object key = entry.getKey();
		          Object val = entry.getValue();
		          Vector nodeSetState = (Vector)val;
		          Vector nodeSetStatecopy =  (Vector)nodeSetState.clone();
		          
		          for(int i=0;i<nodeSetState.size();i++){
		        	  
		        	  int oneAgentID = Integer.parseInt(nodeSetState.get(i).toString());
		        	  //Agent oneAgent = (Agent)this.agentVec.get(oneAgentID);
		        	  Agent oneAgent = (Agent)this.agentVec.get(oneAgentID-1);
		        	  
		        	  for(int j=0;j<nodeSetStatecopy.size();j++){
		        		  
		        		  int anotherAgentID = Integer.parseInt(nodeSetState.get(j).toString());
		        		  
		        		  if(anotherAgentID!=oneAgentID){
		        			  
		        			  if(oneAgent.agentNeighbors.containsKey(anotherAgentID)){
		        				  
		        				  double newWeight = Double.parseDouble(oneAgent.agentNeighbors.get(anotherAgentID).toString());
		        				  newWeight = newWeight/2 + 1;
		        				  oneAgent.agentNeighbors.put(anotherAgentID, newWeight);
		        				  
		        				  
		        			  }
		        			  
		        			  else{
		        				  
		        				  double newWeight = 1;
		        				  oneAgent.agentNeighbors.put(anotherAgentID, newWeight);
		        				  
		        			  }
		        			  
		        			  
		        		  }
		        			  
		        		  
		        	  }
		        	  
		        	  Iterator iter2 = oneAgent.agentNeighbors.entrySet().iterator();
		  		      while (iter2.hasNext()) {
		  		          Map.Entry entry2 = (Map.Entry) iter2.next();
		  		          Object keyAgentNeighbor = entry2.getKey();
		  		          Object valWeight = entry2.getValue();
		        	      
		  		          if(!nodeSetState.contains(keyAgentNeighbor)){
		  		        	  
		  		        	  
		  		        	  double newWeight = Double.parseDouble(valWeight.toString());
	        				  newWeight = newWeight/2;
	        				  oneAgent.agentNeighbors.put(keyAgentNeighbor, newWeight);
		  		        	  
		  		        	  
		  		        	  
		  		          }
		        	  
		        	  
		        	  
		          }
		          

		    }

				
	   }
	
	}
	
	
	public void outputLowDimensionData(int timeStep){
		
		String fileName = "lowDimensionData"+Integer.toString(timeStep)+".txt";
		FileWriter fileWriter;
		
       try {
			
			//fileWriter =new FileWriter(".\\files\\test\\"+fileName);
			
			fileWriter =new FileWriter(".\\files\\"+fileName);
			
			
        	for(int i=0;i<this.agentVec.size();i++){
        	    
        		Agent oneSelectAgent = (Agent)this.agentVec.get(i);
        	    int agentID = oneSelectAgent.getAgentID();
        	    double xCoor = oneSelectAgent.getAgentXCoordinate();
        	    double yCoor = oneSelectAgent.getAgentYCoordinate();
        	    fileWriter.write(agentID+"\t"+xCoor+"\t"+yCoor+"\r\n");
        	    
    		    //System.out.println(agentID+"\t"+xCoor+"\t"+yCoor);
   		    
        	    	
        	    }
        	
			 fileWriter.flush();
		     fileWriter.close();
		     
		}
		
	 catch (FileNotFoundException e) {   
		 e.printStackTrace();   
     } 
     
     catch (IOException e) {   
    	 e.printStackTrace();   
     } 
 }
	
	
  public void outputHighDimensionDataWithCommonLinks(int timeStep){
		
		String fileName = "highDimensionData"+Integer.toString(timeStep)+".txt";
		FileWriter fileWriter;

		
       try {
			
			//fileWriter =new FileWriter(".\\files\\test\\"+fileName);
			fileWriter =new FileWriter(".\\files\\"+fileName);
			
			
        	for(int i=0;i<this.agentVec.size();i++){
        	    
        		Agent oneSelectAgent = (Agent)this.agentVec.get(i);
        		
        		Iterator iter = oneSelectAgent.agentNeighbors.entrySet().iterator();
       	        
        		while (iter.hasNext()) {
        			
       	          Map.Entry entry = (Map.Entry) iter.next();
       	          Object key = entry.getKey();
       	          Object val = entry.getValue();
       	          
       	          double weight = Double.parseDouble(val.toString());
       	          
       	          if(weight>1.0)
       	          fileWriter.write(oneSelectAgent.getAgentID()+"\t"+key.toString()+"\t"+val.toString()+"\r\n");
       	          //fileWriter.write(oneSelectAgent.getAgentID()+"\t"+key.toString()+"\t"+"1"+"\r\n");
       	          

       	        }
        		
        	    	
        	    }
        	
			 fileWriter.flush();
		     fileWriter.close();
		     
		}
		
	 catch (FileNotFoundException e) {   
		 e.printStackTrace();   
     } 
       
     catch (IOException e) {   
    	  e.printStackTrace();   
     } 
  }
  
  
  public void outputHighDimensionDataWithCommonLinks(int timeStep, double threshold){
		
		String fileName = "highDimensionData"+Integer.toString(timeStep)+".txt";
		FileWriter fileWriter;

		
     try {
			
			//fileWriter =new FileWriter(".\\files\\test\\"+fileName);
			fileWriter =new FileWriter(".\\files\\"+fileName);
			
			
      	for(int i=0;i<this.agentVec.size();i++){
      	    
      		Agent oneSelectAgent = (Agent)this.agentVec.get(i);
      		
      		Iterator iter = oneSelectAgent.agentNeighbors.entrySet().iterator();
     	        
      		while (iter.hasNext()) {
      			
     	          Map.Entry entry = (Map.Entry) iter.next();
     	          Object key = entry.getKey();
     	          Object val = entry.getValue();
     	          
     	          double weight = Double.parseDouble(val.toString());
     	          
     	          //if(weight>threshold)
     	          if(weight>=threshold)
     	          fileWriter.write(oneSelectAgent.getAgentID()+"\t"+key.toString()+"\t"+val.toString()+"\r\n");
     	          //fileWriter.write(oneSelectAgent.getAgentID()+"\t"+key.toString()+"\t"+"1"+"\r\n");
     	          

     	        }
      		
      	    	
      	    }
      	
			 fileWriter.flush();
		     fileWriter.close();
		     
		}
		
	  catch (FileNotFoundException e) {   
		e.printStackTrace();   
      }

     catch (IOException e) {   
    	 e.printStackTrace();   
     } 
}
	
	

  
  
  
  
  
  
  
  public void refreshAgentActionUtilityTable(){
		
	  
		for(int i=0;i<this.agentVec.size();i++){
			
			
			Agent oneSelectAgent = (Agent)this.agentVec.get(i);
			
	        double agentCurrentInitialActionUtilityTable[][] = new double[this.stateNumber][6];
	        agentCurrentInitialActionUtilityTable =  oneSelectAgent.getAgentActionUtilityTable();
	        
	        System.out.println(oneSelectAgent.actionInt);
			double Qat = agentCurrentInitialActionUtilityTable[oneSelectAgent.agentLastState-1][oneSelectAgent.actionInt];
					
			Vector vec = (Vector)this.stateToNodeSetMap.get(oneSelectAgent.agentState);			
			
			int Rat = vec.size();
		
			int currentState = oneSelectAgent.agentState;
			
			double Qat1 = 0;
			
			for(int k=0;k<6;k++){
				
				if(Qat1<agentCurrentInitialActionUtilityTable[currentState-1][k]) Qat1 = agentCurrentInitialActionUtilityTable[currentState-1][k];//公式中第二个参数	
				
			}
			
			
			double newUtility = Qat + oneSelectAgent.getAlpha()*(Rat+oneSelectAgent.getBeta()*Qat1-Qat);
			oneSelectAgent.agentActionUtilityTable[oneSelectAgent.agentLastState-1][oneSelectAgent.actionInt] = newUtility;
			this.agentVec.setElementAt(oneSelectAgent, i);
			
			
			
		}
	  
	}
  
  
  

  
  
  
    public void updatestateToNodeSetMap(){
    	
    	HashMap newstateToNodeSetMap = new HashMap();
    	
		 for(int i=0;i<this.agentVec.size();i++){
						
		     	Agent oneAgent = (Agent)this.agentVec.get(i);
		     	int agentState = oneAgent.getAgentState();
		     	
		     	
		     	if(newstateToNodeSetMap.containsKey(agentState)){
					
					Vector vec = new Vector();
					//vec = (Vector)this.stateToNodeSetMap.get(agentState);
					vec = (Vector)newstateToNodeSetMap.get(agentState);
					vec.add(oneAgent.agentID);
					newstateToNodeSetMap.put(agentState, vec);
					
					
				}
				
				else{
					
					Vector vecNew = new Vector();
					vecNew.add(oneAgent.agentID);
					newstateToNodeSetMap.put(agentState, vecNew);
					
					
				}
		     	
		     	
						
		 }

    	
    	
		 this.stateToNodeSetMap = (HashMap)newstateToNodeSetMap.clone();
    	
    	
    	
    }
  
	public void	modelRunOneTime(){
		
		for(int i=0;i<this.agentVec.size();i++){
			
			Agent oneAgent = (Agent)this.agentVec.get(i);
			
			AgentMove AgentRereshTool = new AgentMove(oneAgent);
			AgentRereshTool.rowSize = this.rowSize;
			AgentRereshTool.columnize = this.columnize;
			AgentRereshTool.xMin = this.xMinimize;
			AgentRereshTool.yMin = this.yMinimize;
			//AgentRereshTool.yMin = this.yMinimize;
			
			
			AgentRereshTool.refreshAgentAllInformation();
			//System.out.println(AgentRereshTool.agentRuning.agentXCoordinate+" "+AgentRereshTool.agentRuning.agentYCoordinate);
			this.agentVec.setElementAt(AgentRereshTool.agentRuning, i);
			
			//this.agentVec.add(i, AgentRereshTool.agentRuning);
		    //this.agentVec.removeElementAt(i+1);
						
			
		}
		
	}
	
     public void modelRunOneTime(double epsilon){
    	 
		double count =0;
		for(int i=0;i<this.agentVec.size();i++){
			
			Agent oneAgent = (Agent)this.agentVec.get(i);
			int stateLastTime = oneAgent.agentState;
			
			
			AgentMove AgentRereshTool = new AgentMove(oneAgent,epsilon);
			AgentRereshTool.rowSize = this.rowSize;
			AgentRereshTool.columnize = this.columnize;
			AgentRereshTool.xMin = this.xMinimize;
			AgentRereshTool.yMin = this.yMinimize;
			//AgentRereshTool.yMin = this.yMinimize;
			
			
			AgentRereshTool.refreshAgentAllInformation();
			//System.out.println(AgentRereshTool.agentRuning.agentXCoordinate+" "+AgentRereshTool.agentRuning.agentYCoordinate);
			this.agentVec.setElementAt(AgentRereshTool.agentRuning, i);
			
			int stateCurrentTime = AgentRereshTool.agentRuning.agentState;
			if(stateLastTime!=stateCurrentTime)count++;
			//this.agentVec.add(i, AgentRereshTool.agentRuning);
		    //this.agentVec.removeElementAt(i+1);
						
			
		}
		
		double pro= count/this.agentVec.size();
		this.stateChangeProbabilty.add(pro);
		
	}
	
     
	public static void main(String args[]){
		
		int agentNumOneRegion = 5;
		int rowNum = 9;
		int columnNum = 9;
		int runStemNum = 200;
		double epsilon = 0.3;
		double epsilonDecay = 1;
		double decayValue = 0.99;
		double alpha = 0.5;
		double gamma = 0.3;
		double threshold = 1.0;
		
		QLearningModel oneRealModel = new QLearningModel(agentNumOneRegion,rowNum,columnNum);
		
		oneRealModel.InitializatioModelUniformAlphaGema(0.3, 4.5, 1.2, 6.8,alpha, gamma);
		
		oneRealModel.refreshAgentNeighbors();
		
		oneRealModel.outputLowDimensionData(0);
		oneRealModel.outputHighDimensionDataWithCommonLinks(0,threshold);
		
		
		for(int runStep=1;runStep<=runStemNum;runStep++){
		
			//epsilonDecay = epsilonDecay*decayValue;//decayed epsilon
			//oneRealModel.modelRunOneTime(epsilonDecay);
					
			oneRealModel.modelRunOneTime(epsilon);//fixed epsilon
			oneRealModel.updatestateToNodeSetMap();
			oneRealModel.refreshAgentNeighbors();
			oneRealModel.refreshAgentActionUtilityTable();
			oneRealModel.outputLowDimensionData(runStep);
			oneRealModel.outputHighDimensionDataWithCommonLinks(runStep,threshold);
     		System.out.println("runStep:"+runStep);

		
		}
	

	
	}
	

	
}