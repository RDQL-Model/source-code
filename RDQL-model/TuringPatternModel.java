package releaseModelJar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import publicSource.QLearningModel;

public class TuringPatternModel {

	
	public static void main(String args[]){
		
		//Please first create the directory ".\\files\\test\\public" in the current project.
		
		int agentNumOneRegion = 5;
		int rowNum = 9;
		int columnNum = 9;
		int runStemNum = 200;
		double epsilon = 0.1;
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