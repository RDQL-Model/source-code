

DQN version README:  [<u>中文</u>](./README.md)	[<u>English</u>](./README_EN.md)


### Environmental Preparation

After testing, the program is ready to run only the following packages were successfully installed in an empty (Linux/windows) environment : 

````shell
python -m pip install paddlepaddle -i https://mirror.baidu.com/pypi/simple
python -m pip install parl -i https://mirror.baidu.com/pypi/simple
python -m pip install torch -i https://mirror.baidu.com/pypi/simple
pip install imageio
````

**If above environment is not available**, there is a requirements file which contains all packages and version information in the environment (windows) , `/requirements.txt`. You can install it as following:

````shell
pip install -r requirements.txt
````

### File Structure

**`src`**

- `TorchTrain`: main file, for train model
- `RegionRandom`: Generates random coordinates within a state
- `TorchAgent`: The Agent, which contains the source of Agent, the Agent's decisions, and the Agent's learning.
- `TorchAgentEnv`: the grid environment where the Agent is located
- `TorchAlgorithm`: The DQN algorithm
- `TorchModel`: The neural network model of Agent
- `TorchReplayeMemory`: Experience pool

**`files`**

- `lowDimension`: save low-dimensional data
- `HighDimension`: save high-dimensional data

### **Related parameters**

#### Environmental parameters

The environment-related parameters are set in the `main` function of `src/TorchTrain.py`

````python
def main(epsilon=None):
    # 5 is the nubmer of agents in each state, 10 is the number of rows, 10 is the number of columns
    oneRealModel = TrainModel(5, 10, 10)
    # 0. is the minimum horizontal coordinate of the environment,
    # 10. is the maximum horizontal coordinate of the environment;
    # the second 0. is the minimum vertical coordinate of the environment,
    # the second 10. is the maximum vertical coordinate of the environment
    oneRealModel.InitializatioModelUniform(0., 10., 0., 10.)
````

- `TrainModel(5, 10, 10)`: indicates that there are 5 nodes per state in the environment, 10 states per row in the horizontal direction, and 10 states per column in the vertical direction, for a total of 100 states
- `InitializatioModelUniform(0., 10., 0., 10.)`: means that the horizontal coordinate has a minimum value of 0 and a maximum value of 10, and the vertical coordinate has a minimum value of 0 and a maximum value of 10.

For example, the following: 

<img src="https://s2.loli.net/2022/05/04/mrStPGcdQ5RhD2g.png" alt="lowDimensionData0.txt" style="zoom: 50%;" />

#### Model Parameters

The parameters of model are set in the `src/params` file.

````python
{
    'gamma': 0.1, # discounted future reward
    'epsilon': 0.5, # epsilon-greedy policy: probability of random action
    'stateNum': 100, # number of states
    'connection_num': 0., # It's not useful now. Default 0 is fine, it was originally intended to set the link probability of high-dimensional data.
    'epoch': 200, # number of epochs
}
````

### RUN

#### Windows

Just run the `src/TorchTrain.py` file directly from the terminal or IDE.

#### Linux

````shell
python TorchTrain.py
````

or 

````shell
bash run.sh
````

