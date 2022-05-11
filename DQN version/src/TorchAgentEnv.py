#!/usr/bin/python3
# coding = UTF-8
# this is program for 
# @python version 3.7.9
# @code by


import numpy as np
from TorchAgent import Agent
from RegionRandom import RegionRandom
with open('./params', mode='r') as f:
    PARAMS: dict = eval(f.read())

# 这里面就不实现对于action的choose
class AgentEnv():
    def __init__(self, **kwargs):
        self.rowSize = 2
        self.columnize = 2
        # 因为不在环境里面做决策了，所以这个就给剔除了
        # self.agentUtilityChoices = model.value()
        self.decsion = 0
        self.xMin = 0
        self.yMin = 0

        if kwargs:
            # 在这里我增强了探索策略看看能不能在形成超级大的社团之后能不能散开去探索新的环境
            # 在这里实现从不同的节点读取不同的
            # self.epsilon = 0.5
            # self.epsilon = 0.3
            self.agentRuning: Agent = kwargs["agent"]
            self.epsilon = self.agentRuning.getEpsilon()
        else:
            self.epsilon = 0.1
            self.agentRuning = Agent(1)
            self.decsion = 0

    # 这里要实现能够返回next_obs, reward, 因为我们的环境不需要done，也就是不需要结束这个约束，我想这里可以不用实现
    # 这个reward其实在runepisode 也可以实现
    def step(self, action):
        self.decsion = action
        return self.extractNextState(action)

    def extractNextState(self, action):
        currentState = self.agentRuning.agentState
        nextState = 0

        # 选择stay
        if (action == 0):
            nextState = currentState
        # 选择向上
        if (action == 1):
            nextState = currentState + self.rowSize
        # 选择向下
        if (action == 2):
            nextState = currentState - self.rowSize
        # 选择向左
        if (action == 3):
            nextState = currentState - 1
        # 选择向右
        if (action == 4):
            nextState = currentState + 1
        # 如果是5，随机
        if (action == 5):
            nextState = self.generateRandomState()

        if nextState<=0 or nextState>PARAMS.get('stateNum'):
            nextState = self.generateRandomState()

        # 直接更新就行
        self.agentRuning.agentState = nextState
        self.agentRuning.agentLastState = currentState
        # print(nextState)
        return nextState

    # 这里更新nextstate
    def updateState(self, nextState):
        self.agentRuning.agentLastState = self.agentRuning.agentState
        self.agentRuning.agentState = nextState


    def getEpsilon(self):
        return self.epsilon

    def setEpsilon(self, epsilon):
        self.epsilon = epsilon

    def getAgentRuning(self):
        return self.agentRuning

    def setAgentRuning(self, agent: Agent):
        self.agentRuning = agent

    # 在Agent里面实现了
    def AgentChooseActionNew(self):
        pass

    # 随机一个状态
    def generateRandomState(self):
        rowsize = self.rowSize
        columnsize = self.columnize

        randomState = np.random.randint(0, rowsize * columnsize)
        return randomState

    def refreshAgentTimeStep(self):
        self.agentRuning.agentTimeStep += 1

    def refreshAgentState(self):
        currentState = self.agentRuning.agentState
        nextState = 0
        self.decsion = self.agentRuning.sample(currentState)
        # 选择stay
        if (self.decsion == 0):
            nextState = currentState
        # 选择向上
        if (self.decsion == 1):
            nextState = currentState + self.rowSize
        # 选择向下
        if (self.decsion == 2):
            nextState = currentState - self.rowSize
        # 选择向左
        if (self.decsion == 3):
            nextState = currentState - 1
        # 选择向右
        if (self.decsion == 4):
            nextState = currentState + 1

        # if nextState <= 0:
        #     nextState

        self.agentRuning.agentState = nextState
        self.agentRuning.agentLastState = currentState
        # 这里可以通过step返回出去 就是next_step
        return nextState


    # 选择下一个agent，这里还做了更新
    def refreshAgentStateNew(self):
        currentState = self.agentRuning.agentState
        nextState = 0
        self.decsion = self.agentRuning.sample(currentState)


        # 选择stay
        if (self.decsion == 0):
            nextState = currentState
        # 选择向上
        if (self.decsion == 1):
            nextState = currentState + self.rowSize
        # 选择向下
        if (self.decsion == 2):
            nextState = currentState - self.rowSize
        # 选择向左
        if (self.decsion == 3):
            nextState = currentState - 1
        # 选择向右
        if (self.decsion == 4):
            nextState = currentState + 1
        # 如果是5，随机
        if (self.decsion == 5):
            nextState = self.generateRandomState()

        self.agentRuning.agentState = nextState
        self.agentRuning.agentLastState = currentState
        return nextState

    # 更新坐标，基于状态求坐标
    def refreshAgentCoordinate(self):
        xCoorInt = (self.agentRuning.agentState - 1) % self.rowSize
        yCoorInt = (self.agentRuning.agentState - 1) // self.rowSize

        xCoordouble = self.xMin + (xCoorInt) * self.agentRuning.xScale
        yCoordouble = self.yMin + (yCoorInt) * self.agentRuning.yScale
        test = RegionRandom()
        test.GetOneCoordinate(xCoordouble, yCoordouble, self.agentRuning.xScale, self.agentRuning.yScale)
        self.agentRuning.agentXCoordinate = test.xCoordinate
        self.agentRuning.agentYCoordinate = test.yCoordinate

    # 这里要换为更新网络
    def refreshAgentActionUtilityTable(self):
        pass

    # 更新了状态
    def refreshAgentAllInformation(self):
        self.AgentChooseActionNew()
        self.refreshAgentStateNew()
        self.refreshAgentTimeStep()
        self.refreshAgentCoordinate()
        self.refreshAgentActionUtilityTable()
