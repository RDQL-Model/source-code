# RDQL-Model

This projects provides the source codes of the Reaction-Diffusion model using Q-Learning (RDQL), which contains Q-Learning version implemented by Java and Deep Q-network version implemented by Python.

On the Q-Learning version of the RDQL model, please run the file "src/QLearningModel.java". All the parameters can be set in the main function. The low dimensional vector representation and high dimensional topology representation of the generated networks are saved in the path "src/files/".

On the Deep Q-network version of the RDQL model, please run the file "src/TorchTrain.py" in Windows platform, and run the command "python TorchTrain.py" in Linux platform. The parameter agent number, row number and column number can be set in the main function of file "src/TorchTrain.py", and the configuration of other parameters is in the file "src/params". The low dimensional vector representation and high dimensional topology representation of the generated networks are saved in the path "/files/lowDimension" and "/files/highDimension" respectively.
