DQN version README:  [<u>中文</u>](./README.md)	[<u>English</u>](./README_EN.md)

### **环境准备**

经测试，在一个(Linux/windows)空环境下**仅安装成功如下包即可以运行**: 

````shell
python -m pip install paddlepaddle -i https://mirror.baidu.com/pypi/simple
python -m pip install parl -i https://mirror.baidu.com/pypi/simple
python -m pip install torch -i https://mirror.baidu.com/pypi/simple
pip install imageio
````

**如果以上环境不可以**，其中包含了环境(windows)中所有的包`/requirements.txt`

````shell
pip install -r requirements.txt
````

### **文件结构**

**`src`**

- `TorchTrain`: 主文件，train
- `RegionRandom`: 在某一个状态内产生随机坐标
- `TorchAgent`: 智能体，包含了智能体智能性的来源、智能体决策以及智能体学习部分
- `TorchAgentEnv`: 智能体所在的网格环境
- `TorchAlgorithm`: DQN算法
- `TorchModel`: 智能体的神经网络模型
- `TorchReplayeMemory`: 经验池

**`files`**

- `lowDimension`: 保存低维数据
- `HighDimension`: 保存高维数据



### **相关参数**

#### **环境参数**

环境相关的参数在`src/TorchTrain.py`的 `main`函数中设置

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

- `TrainModel(5, 10, 10)`: 表示环境中每个状态有5个节点，水平方向每行有10个状态，垂直方向每列有10个状态，共100个状态
- `InitializatioModelUniform(0., 10., 0., 10.)`: 表示横坐标最小值0，最大值10， 纵坐标最小值0，最大值10.

如下图: 

<img src="https://s2.loli.net/2022/05/04/mrStPGcdQ5RhD2g.png" alt="lowDimensionData0.txt" style="zoom: 50%;" />

#### **模型参数**

模型除环境本身之外的的参数设置在`src/params`文件下。

````python
{
    'gamma': 0.1, # discounted future reward
    'epsilon': 0.5, # epsilon-greedy policy: probability of random action
    'stateNum': 100, # number of states
    'connection_num': 0., # It's not useful now. Default 0 is fine, it was originally intended to set the link probability of high-dimensional data.
    'epoch': 200, # number of epochs
}
````

- `gamma`: 对未来的折扣
- `epsilon`: epsilon-greedy的探索概率
- `stateNum`: 状态个数
- `connection_num`: 默认`0`即可，设置的高维的链接概率后来不用了
- `epoch`: 演化的轮次

### **运行**

**Windows**

直接运行`src/TorchTrain.py`文件即可。

**Linux**

````shell
python TorchTrain.py
````

或者

````shell
bash run.sh
````


