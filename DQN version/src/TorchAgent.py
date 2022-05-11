#!/usr/bin/python3
# coding = UTF-8
# this is program for TorchAgent
# @python version 3.7.9
# @code by


import numpy as np
import torch
import random
import parl
from TorchModel import TorchModel
from TorchAlgorithm import DQN
from TorchReplayMemory import ReplayMemory

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# PARAMS = {}
with open('./params', mode='r') as f:
    PARAMS: dict = eval(f.read())

class Agent(parl.Agent):
    # TODO 这个size啥意思？这个size是用来构筑Q表的
    # TODO 这里加入了Model作为属性的话，就需要在初始化的时候进行添加回来
    def __init__(self, size):
        self.agentID = 0
        self.agentState = 0
        self.agentLastState = 0
        self.agentXCoordinate = 0
        self.agentYCoordinate = 0
        # key是邻居节点，value是权重
        self.agentNeighbors = {}
        self.agentActionUtilityTable = np.zeros((size, 6))
        self.agentTimeStep = 0
        self.xScale = 0
        self.yScale = 0
        self.alpha = 0
        self.beta = 0
        self.actionInt = 0
        self.epsilon = 0.
        # TODO 这里只和DQN的更新有关，刚开始设置太大了，很快就收敛了
        self.gamma = PARAMS.get('gamma')
        # 让他等于size + 1 方便神经网络进行计算
        self.obs_dim = size+1
        self.action_dim = 6
        self.total_reward = 0
        # 这里是后来改的  这里关系到obs的tensor、还有agent的定义
        self.stateNum = PARAMS.get('stateNum')


        # 每个节点定义一个model, 同时定义一个DQN的algorithm
        # TODO 这个model的状态数是后拿的
        # self.model = Model(obs_dim=self.stateNum, act_dim=self.action_dim)
        self.model = TorchModel(obs_dim=PARAMS.get('stateNum')+1, act_dim=self.action_dim)
        self.alg = DQN(model=self.model, act_dim=self.action_dim, gamma=self.gamma, lr=0.01)
        # super(Agent, self).__init__(algorithm)
        # 每个agent都定义一个经验池, 这里先定义最大长度是100
        self.memory = ReplayMemory(200)

        self.global_step = 0
        # 这个对当前的环境影响还是比较大的
        self.update_target_steps = 10
        # TODO 这两个可以在trainModel里面获得
        self.rowSize = 7
        self.columnize = 9
        # self.model = model

    # 输入的是一个one-hot的tensor，直接返回一个含有一个tensor
    def sample(self, state):
        """这里是包含探索的
        """
        sample = random.random()

        if sample > self.epsilon:
            with torch.no_grad():
                # 这里产生的最好的可能会冲出去，这个在Q表中不会产生这种问题
                return self.alg.model(state).max(1)[1].view(1, 1)  # 返回的最大动作的indice
        else:
            return torch.tensor([[self.generateRandomDecison()]], device=device, dtype=torch.long)  # 随机选一个动作

    # 要把这个obs改成是one-hot, 这里传过来的就是个tensor
    def predict(self, obs):
        # obs = paddle.to_tensor(obs, dtype='float32')
        pred_q = self.alg.predict(obs)
        act = pred_q.argmax().numpy()[0]
        return act


    def generateRandomDecison(self):
        randomDecison = 0
        rowsize = self.rowSize
        agentState = self.agentState
        markBreak = 1

        for i in range(100):
            # 0-5
            randomDecison = np.random.choice(6)
            markBreak = 1
            # 在最左侧，选择往左走
            if (agentState % self.rowSize == 1) and (randomDecison == 3):
                markBreak = 0
            #  在最右侧，选择往右走
            if (agentState % self.rowSize == 0) and (randomDecison == 4):
                markBreak = 0
            # 在最下侧，选择往下走
            if ((agentState - 1) // self.rowSize == 0) and (randomDecison == 2):
                markBreak = 0
            # 在最上侧，选择往上走
            if ((agentState - 1) // self.rowSize == (self.columnize - 1)) and (randomDecison == 1):
                markBreak = 0
            if markBreak == 1:
                break
        return randomDecison


    def learn(self, batch):
        if self.global_step % self.update_target_steps == 0:
            self.alg.sync_target()
        self.global_step += 1

        loss = self.alg.learn(batch)
        return loss



    def getAgentID(self):
        return self.agentID

    def setAgentID(self, agentID):
        self.agentID = agentID

    def getAgentState(self):
        return self.agentState

    def setAgentState(self, agentState):
        self.agentState = agentState

    def getAgentLastState(self):
        return self.agentLastState

    def setAgentLastState(self, agentLastState):
        self.agentLastState = agentLastState

    def getAgentXCoordinate(self):
        return self.agentXCoordinate

    def setAgentXCoordinate(self, agentXCoordinate):
        self.agentXCoordinate = agentXCoordinate

    def getAgentYCoordinate(self):
        return self.agentYCoordinate

    def setAgentYCoordinate(self, agentYCoordinate):
        self.agentYCoordinate = agentYCoordinate

    def getAgentNeighbors(self):
        return self.agentNeighbors

    def setAgentNeighbors(self, agentNeighbors):
        self.agentNeighbors = agentNeighbors

    def getAlpha(self):
        return self.alpha

    def setAlpha(self, alpha):
        self.alpha = alpha

    def getBeta(self):
        return self.beta

    def setBeta(self, beta):
        self.beta = beta

    def getEpsilon(self):
        return self.epsilon

    def setEpsilon(self, eposilon):
        self.epsilon = eposilon

    def getAgentActionUtilityTable(self):
        return self.agentActionUtilityTable

    def setAgentActionUtilityTable(self,agentActionUtilityTable):
        self.agentActionUtilityTable = agentActionUtilityTable

    def getAgentTimeStep(self):
        return self.agentTimeStep

    def setAgentTimeStep(self, agentTimeStep):
        self.agentTimeStep = agentTimeStep


def main():
    test = Agent(5)
    test1 = Agent(5)
    test.setAgentID(10)
    a = test.getAgentID()

    s1 = test.agentState
    b = test.getAgentState()
    test.setAgentState(100)
    c = test.getAgentState()
    s2 = test.agentState
    print(test.predict(10))
    print(a)
    print(s1)
    print(b)
    print(c)
    print(s2)
    print(test.agentID)
if __name__ == '__main__':
    main()