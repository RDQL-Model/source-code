#!/usr/bin/python3
# coding = UTF-8
# this is program for train the model
# @python version 3.7.9
# @code by



import torch
from TorchAgent import Agent
from collections import namedtuple
import numpy as np
from RegionRandom import RegionRandom
import copy
from TorchAgentEnv import AgentEnv
import os


Experience = namedtuple('Experience', ['obs', 'action', 'reward', 'next_obs'])
with open('./params', mode='r') as f:
    PARAMS: dict = eval(f.read())

# 将状态转换为onehot编码的形式
def get_screen(state):
    y_state = torch.Tensor([[state]]).long()
    y_onehot = torch.FloatTensor(1, PARAMS.get('stateNum')+1)
    # 全部使用0进行填充
    y_onehot.zero_()
    # one-hot
    y_onehot.scatter_(1, y_state, 1)
    return y_onehot

class TrainModel():
    # 6, 7, 9
    def __init__(self, num, rowSize, columS):
        self.agentVec = []
        self.agentNumEachRegion = num
        self.rowSize = rowSize
        self.columnize = columS
        # 每个状态下所对应的节点的一个字典
        self.stateToNodeSetMap = {}
        self.stateNumber = 0
        self.epsilon = PARAMS.get('epsilon')
        self.xMinimize = 0
        self.yMinimize = 0
        self.xMaximize = 0
        self.yMaximize = 0
        self.xScale = 0
        self.yScale = 0

        # 选取高维数据
        self.highDimensionData = {}

    # 初始化节点属性，设置为均匀分布
    # 这里设定的是坐标的形式，而不是直接设定状态数量，当使用实际数据时，降维后得到的就是各个节点的坐标数据
    # TODO 在这里又想到一个问题，我是把坐标作为状态还是，还是直接把state作为输入神经网络中
    # epsilon 参数为了后来更好的连续跑程序，后加上去，为了跑epsilon相变加上去的。
    def InitializatioModelUniform(self, xMin, xMax, yMin, yMax):

        self.xMinimize = xMin
        self.yMinimize = yMin

        # 所有节点x轴的坐标跨度
        xScale = xMax - xMin
        yScale = yMax - yMin

        # 每个区域状态的x轴的跨度
        xScaleEachOrigin = 0
        yScaleEachOrigin = 0

        # 这里除出来是每个区域的xScale？
        if (xScale % self.rowSize == 0):
            xScaleEachOrigin = xScale / self.rowSize
        else:
            xScaleEachOrigin = xScale / self.rowSize
            self.rowSize += 1

        if (yScale % self.columnize == 0):
            yScaleEachOrigin = yScale / self.columnize
        else:
            yScaleEachOrigin = yScale / self.columnize
            self.columnize += 1

        # 上面的代码实现了，输入整个区域大小和状态数量，得到切分好的每个区域的长和宽
        stateNum = self.rowSize * self.columnize
        self.stateNumber = stateNum
        # agentInitialActionUtilityTable = -1. * np.ones((stateNum, 6))

        agentID = 0
        agentTime = 0
        # alpha 是0.5
        parameter1 = 0.3
        parameter2 = 0.5

        # 这里设置不同节点的epsilon
        # epsilon_b = 0.8
        # epsilon_s = 0.3
        epsilon = self.epsilon

        # 时刻从 1 开始计数
        agentTime += 1
        coordinateTest = []
        # 对每个状态中的每个agent进行初始化
        # TODO在这里出现了大量的agent
        agentEpsilon = []

        # 状态左下角坐标`
        state_set = set()
        for state in range(1, stateNum+1):
            for i in range(self.agentNumEachRegion):
                agentID += 1
                # 创建一个有stateNum个状态的Agent
                oneNewAgent: Agent = Agent(stateNum)
                # 初始化agent的参数
                oneNewAgent.setAgentID(agentID)
                oneNewAgent.setAgentState(state)
                oneNewAgent.setAgentTimeStep(agentTime)
                # oneNewAgent.setAgentActionUtilityTable(agentInitialActionUtilityTable)

                oneNewAgent.setAlpha(parameter1)
                oneNewAgent.setBeta(parameter2)

                # TODO 尝试 这里设置80%有比较小的探索概率， 20%的节点有较大的epsilon
                # if (np.random.random() <= 0.2):
                #     oneNewAgent.setEpsilon(epsilon_s)
                # else:
                #     oneNewAgent.setEpsilon(epsilon_b)
                oneNewAgent.setEpsilon(epsilon)
                # 输出试试
                agentEpsilon.append((oneNewAgent.agentID, oneNewAgent.getEpsilon()))

                # 产生节点的坐标，现得到节点所在状态的坐下角坐标
                xInitialCooridate = xMin + ((state - 1) % self.rowSize) * xScaleEachOrigin
                yInitialCooridate = yMin + ((state - 1) // self.rowSize) * yScaleEachOrigin
                state_set.add((xInitialCooridate, yInitialCooridate))

                test = RegionRandom()
                # print("生成坐标用的参数：", xInitialCooridate, yInitialCooridate, xScaleEachOrigin, yScaleEachOrigin)
                test.GetOneCoordinate(xInitialCooridate, yInitialCooridate, xScaleEachOrigin, yScaleEachOrigin)
                # print("最后生成的坐标：", "(", test.xCoordinate, test.yCoordinate, ")")
                # 这个是为了写入文件
                coordinateTest.append((test.xCoordinate, test.yCoordinate))

                oneNewAgent.agentXCoordinate = test.xCoordinate
                oneNewAgent.agentYCoordinate = test.yCoordinate
                # 每个节点的scale就是他所在区域的scale
                oneNewAgent.xScale = xScaleEachOrigin
                oneNewAgent.yScale = yScaleEachOrigin

                # 将新创建的agent放入到容器中
                self.agentVec.append(oneNewAgent)

                # 每个状态都有那些节点，给放到一个字典中,
                # 字典的结构是，状态-》agentID的列表
                if (self.stateToNodeSetMap.get(state) != None):
                    self.stateToNodeSetMap.get(state).append(oneNewAgent.agentID)
                else:
                    vecNew = []
                    vecNew.append(oneNewAgent.agentID)
                    self.stateToNodeSetMap[state] = vecNew
                # 这里就是为了打印出来看看坐标
        with open('./files/lowDimension/lowDimensionData0.txt', 'w') as file:
            # file.write(str(xInitialCooridate) + "\t" + str(yInitialCooridate) + "\t" + str(
            #     xScaleEachOrigin) + "\t" + str(yScaleEachOrigin))
            for index, cor in enumerate(coordinateTest):
                file.write(str(index) + '\t' + str(cor[0]) + "\t" + str(cor[1]) + "\n")
        # 为了打印出来状态看看对不对
        # print(state_set)
        # with open('state_set.txt', 'w') as f:
        #     for x, y in state_set:
        #         f.write(str(x) + '\t' + str(y) + '\n')

                # with open("./epsilon", 'w') as file:
                #     for x, y in agentEpsilon:
                #         file.write(str(x) + "\t" + str(y) + "\n")

    # 这个应该是所有节点移动完成之后，开始更新邻居，
    # 需要知道每个agent所在的区域，应该放在model类这里，而不是agentMove类
    # 这里应该建立新的邻居,也就是考虑到了连接的消失与重新连接,这是一种内部重连
    # 这里打算把内部重连的机制改一下， 通过筛选
    # 或者是建立一个索引，只有排名前几的有机会参与选择
    def refreshAgentNeighbors(self):

        # 对同一个状态的节点进行分别处理,状态与对应的节点列表
        for key, val in self.stateToNodeSetMap.items():
            nodeSetState = val
            nodeSetStatecopy = copy.deepcopy(val)

            # 遍历这个节点列表
            for i in range(len(nodeSetState)):
                # 选择第i个节点，并取出id
                oneAgentID = int(nodeSetState[i])
                # agentID从1开始，但是存到list里面从0开始，取出agent
                oneAgent = self.agentVec[oneAgentID - 1]
                # 以上就取出了当前状态下的一个agent

                # 对处于新状态的节点进行处理
                # 这里要考虑原来邻居也有消失的
                for j in range(len(nodeSetStatecopy)):
                    anotherAgentID = int(nodeSetStatecopy[j])
                    # 建立连接的概率
                    # 这里能改保留多少的连接, 后来不用了
                    p_gen = np.random.random()
                    # 将不同的节点建立联系
                    if (anotherAgentID != oneAgentID and p_gen>PARAMS.get('connection_num')):
                        # 两个节点之间有联系
                        if (anotherAgentID in oneAgent.agentNeighbors):
                            # 如果这个节点之前就是agent的邻居，就让权重=1+之前的权重/2， 相当于加强之前的权重
                            newWeight = float(oneAgent.agentNeighbors.get(anotherAgentID))
                            newWeight = newWeight / 2 + 1
                            oneAgent.agentNeighbors[anotherAgentID] = newWeight
                        else:
                            # 如果之前的节点没有这个邻居，就建立一个连接让权重=1
                            newWeight = 1.
                            oneAgent.agentNeighbors[anotherAgentID] = newWeight

                # 上面是处理处于新状态的节点，下面处理过去有关系，但是现在不在同一个状态的节点
                for keyAgentNeighbor, valWeight in oneAgent.agentNeighbors.items():
                    # 之前在同一个状态，但是现在不在一个状态了，权重更新 / 2
                    if (not (keyAgentNeighbor in nodeSetState)):
                        newWeight = float(valWeight)
                        newWeight /= 2
                        oneAgent.agentNeighbors[keyAgentNeighbor] = newWeight


    # output lowdimesion data
    def outputLowDimensionData(self, files, timeStep):
        fileName = f"lowDimensionData{timeStep}.txt"
        try:
            fileWritter = open(f"./{files}/lowDimension/"+fileName, 'w', encoding='utf8')
            for i in range(len(self.agentVec)):
                oneSelectAgent: Agent = self.agentVec[i]
                agentID = oneSelectAgent.getAgentID()
                xCoor = oneSelectAgent.getAgentXCoordinate()
                yCoor = oneSelectAgent.getAgentYCoordinate()
                fileWritter.write(str(agentID)+"\t"+str(xCoor)+"\t"+str(yCoor)+"\n")
            fileWritter.close()
        except FileNotFoundError as e:
            print(e)
        except IOError as e:
            print(e)

    # output highdimesion data
    def outputHighDimensionDataWithCommonLinks(self, files, timeStep):
        filename = f"highDimensionData{timeStep}.txt"

        try:
            fileWriter = open(f"./{files}/highDimension/"+filename, 'w')
            for i in range(len(self.agentVec)):
                oneSelectAgent: Agent = self.agentVec[i]
                for key, val in oneSelectAgent.agentNeighbors.items():
                    fileWriter.write(str(oneSelectAgent.agentID) + "\t" + str(key) + "\t" + str(val) + "\n")
            fileWriter.close()
        except FileNotFoundError as e:
            print(e)
        except IOError as e:
            print(e)

    def outputHighDimensionDataWithoutCommonLinks(self, files, timeStep):
        fileName = f"highDimensionData{timeStep}.txt"
        allLinksSet = {}
        for i in range(len(self.agentVec)):
            oneSelectAgent: Agent = self.agentVec[i]
            for key, value in oneSelectAgent.agentNeighbors.items():
                linkStr = oneSelectAgent + "\t" + str(key)
                linkStrFlipp = str(key) + "\t" + oneSelectAgent

                if ((linkStr in allLinksSet) or (linkStrFlipp in allLinksSet)):
                    continue
                else:
                    allLinksSet[linkStr] = str(value)
        try:
            fileWriter = open("./file", "w")
            for key, value in allLinksSet.items():
                fileWriter.write(str(key) +"\t"+str(value)+"\n")
            fileWriter.close()
        except FileNotFoundError as e:
            print(e)
        except IOError as e:
            print(e)

    # TODO 这一步是更新Q表现在由神经网络自动完成。还没想好要怎么写
    def refreshAgentActionUtilityTable(self):
        # TODO　对现有每个节点都要进行更新
        for i in range(len(self.agentVec)):
            oneSelectAgent: Agent = self.agentVec[i]
            agentCurrentInitialActionUtilityTable = oneSelectAgent.getAgentActionUtilityTable()
            print(oneSelectAgent.actionInt)

            # 公式中第一个参数
            Qat = agentCurrentInitialActionUtilityTable[oneSelectAgent.agentLastState - 1, oneSelectAgent.actionInt]
            # 找到这个状态下的所有节点
            vec = self.stateToNodeSetMap.get(oneSelectAgent.agentState)


            if (vec != None):
                Rat = len(vec)
            else:
                Rat = 0

            currentState = oneSelectAgent.agentState

            # 找当前状态下收益最大的
            # Qat1 = 0.
            QcurrentState = agentCurrentInitialActionUtilityTable[currentState - 1, :]
            Qat1 = np.max(QcurrentState)
            print("Qat2:" + str(Qat1))

            # 这个可以将gamma设置为一个参数
            # 强化学习的公式
            newUtility = Qat + oneSelectAgent.getAlpha() * (Rat + oneSelectAgent.getAlpha() * Qat1 - Qat)
            oneSelectAgent.agentActionUtilityTable[
                oneSelectAgent.agentLastState - 1, oneSelectAgent.actionInt] = newUtility
            self.agentVec[i] = oneSelectAgent

    # 现在已经没有Q表了，不能输出
    def outputUtilityTableData(self, timeStep):
        pass

    # 更新刚才的一个区域内的节点的map
    # 因为在执行完一系列的操作之后，每个状态下的节点的集合发生了改变
    # TODO 我觉得这里因为状态什么都发生了变化,所以需要重新做一个表去存而不是用以前的表
    def updatestateToNodeSetMap(self):
        newstateToNodeSetMap = {}
        for i in range(len(self.agentVec)):
            oneAgent: Agent = self.agentVec[i]
            # 获取agent此时的state
            agentState = oneAgent.getAgentState()

            if (newstateToNodeSetMap.get(agentState)!=None):
                newstateToNodeSetMap.get(agentState).append(oneAgent.agentID)
            else:
                vec = []
                vec.append(oneAgent.agentID)
                newstateToNodeSetMap[agentState] = vec
        self.stateToNodeSetMap = copy.deepcopy(newstateToNodeSetMap)



    def NodeDispear(self):
        pass

    # TODO 更新每个节点的网络， 算是run_episode
    def updateAllNetwork(self):

        for i in range(len(self.agentVec)):
            oneAgent: Agent = self.agentVec[i]
            AgentRefreshTool: AgentEnv = AgentEnv(agent=oneAgent)
            AgentRefreshTool.rowSize = self.rowSize
            AgentRefreshTool.columnize = self.columnize
            AgentRefreshTool.xMin = self.xMinimize
            AgentRefreshTool.yMin = self.yMinimize

            vec = self.stateToNodeSetMap.get(oneAgent.agentState)
            # 这里得到的obs是一维的， 根本放不到网络里面， 在这里写一下
            obs = oneAgent.getAgentState()

            obs_onehot = get_screen(obs)
            # 得到 action并更新action 这里已经拿着obs去采样了
            # 这里返回的action是一个tensor
            action_tensor = oneAgent.sample(obs_onehot)
            action = action_tensor.detach().item()
            # 所以这里先不进行采样直接把agent里面的原有的属性
            # action = oneAgent.actionInt
            # 这个step函数并对状态进行了更新
            next_obs = AgentRefreshTool.step(action=action)


            next_obs_onehot = get_screen(next_obs)
            # 这里设置的奖励函数也是 周围所有的节点的集合，这个奖励函数本身就决定了，所有的节点本身要形成一个大的节点
            if vec!=None:
                reward = len(vec)
            else:
                reward = 0
            reward_tensor = torch.tensor([reward], dtype=torch.float)
            oneAgent.memory.append(obs_onehot, action_tensor, reward_tensor, next_obs_onehot)

            # if (len(oneAgent.memory.buffer) >= MEMORY_MAX_SIZE) and (RunStep % LEARN_FREQ == 0):
            # 这里已经对q网络进行了更新
            if (len(oneAgent.memory) > 32):
                exp = oneAgent.memory.sample(32)
                batch = Experience(*zip(*exp))
                train_loss = oneAgent.learn(batch)
            oneAgent.total_reward += reward


    def updateAllMemory(self):
        for i in range(len(self.agentVec)):
            oneAgent: Agent = self.agentVec[i]
            AgentRefreshTool: AgentEnv = AgentEnv(agent=oneAgent)
            AgentRefreshTool.rowSize = self.rowSize
            AgentRefreshTool.columnize = self.columnize
            AgentRefreshTool.xMin = self.xMinimize
            AgentRefreshTool.yMin = self.yMinimize

            for j in range(200):
                obs = oneAgent.getAgentState()

                obs_onehot = get_screen(obs)
                # obs_onehot = paddle.nn.functional.one_hot(obs_tensor, 80)
                action = oneAgent.actionInt
                action_tensor = torch.tensor([[action]], dtype=torch.int64)
                # if len(self.stateToNodeSetMap[i]) != 0 or self.stateToNodeSetMap[i] != None:
                #     reward = len(self.stateToNodeSetMap[i])
                # else:
                #     reward = 0
                reward = 6
                reward_tensor = torch.tensor([reward], dtype=torch.float)

                next_obs = AgentRefreshTool.step(action)
                # next_obs = next_obs
                # next_obs_tensor = paddle.to_tensor([next_obs])
                next_obs_onehot = get_screen(next_obs)
                oneAgent.memory.append(obs_onehot, action_tensor, reward_tensor, next_obs_onehot)



    def modelRunOneTime(self):
        for i in range(len(self.agentVec)):
            oneAgent: Agent = self.agentVec[i]
            AgentRefreshTool: AgentEnv = AgentEnv(agent=oneAgent)
            AgentRefreshTool.rowSize = self.rowSize
            AgentRefreshTool.columnize = self.columnize
            AgentRefreshTool.xMin = self.xMinimize
            AgentRefreshTool.yMin = self.yMinimize


            # 更新时间步
            AgentRefreshTool.refreshAgentTimeStep()
            # 更新坐标
            AgentRefreshTool.refreshAgentCoordinate()
            # 这里更新了里面的动作，状态，以及网络
            self.updateAllNetwork()

    def printAllGamma(self):
        gammalist = []
        for i in range(len(self.agentVec)):
            oneAgent: Agent = self.agentVec[i]
            gammalist.append(oneAgent.gamma)
        print(gammalist)

    def prinAllEpsilon(self):
        epsilonList = []
        for i in range(len(self.agentVec)):
            oneAgent: Agent = self.agentVec[i]
            epsilonList.append(oneAgent.epsilon)
        print(epsilonList)


    def outputTable(self, runStep):
        if not os.path.exists(f"./table/runStep={runStep}"):
            os.mkdir(f"./table/runStep={runStep}")
        # 读神经网络估计出来的q表
        alltable = dict()
        for agentid in range(len(self.agentVec)):
            table = []
            for state in range(1, PARAMS.get("obs_dim")):
                x = get_screen(state)
                # 最主要的就是这一句
                predict = self.agentVec[agentid].model(x).detach().numpy().tolist()
                table.append(predict)
            # print(table)
            alltable[agentid] = table
        for agentid, tables in alltable.items():
            with open(f"./table/runStep={runStep}/agentid={agentid}.txt", mode='w') as file:
                for lists in tables:
                    file.write(str(lists) + '\n')




import time
def main(epsilon=None):
    # 5 is the nubmer of agents in each state, 10 is the number of rows, 10 is the number of columns
    oneRealModel = TrainModel(5, 10, 10)
    # 0. is the minimum horizontal coordinate of the environment,
    # 10. is the maximum horizontal coordinate of the environment;
    # the second 0. is the minimum vertical coordinate of the environment,
    # the second 10. is the maximum vertical coordinate of the environment
    oneRealModel.InitializatioModelUniform(0., 10., 0., 10.)
    # 看看所有节点的gamma值
    oneRealModel.printAllGamma()
    # 更新邻居节点
    oneRealModel.refreshAgentNeighbors()
    epoch = PARAMS.get('epoch')
    oneRealModel.updateAllMemory()
    oneRealModel.prinAllEpsilon()
    globalstart = time.time()
    print("loading Memory ....")
    for runStep in range(1, epoch+1):
        start = time.time()
        print(f"runStep: {runStep}, running", end=' ')
        # 这里更新了选择新的action存起来了，更新了state状态，更新了timeStep时间步，更新了Coordinate坐标， 更新了网络
        oneRealModel.modelRunOneTime()
        # 更新状态里面的节点
        oneRealModel.updatestateToNodeSetMap()
        # 更新节点信息
        oneRealModel.refreshAgentNeighbors()
        oneRealModel.outputLowDimensionData("files", runStep)
        oneRealModel.outputHighDimensionDataWithCommonLinks("files", runStep)
        oneRealModel.outputTable(runStep)
        # print(f"runStep: {runStep}")
        end = time.time()
        print(f'本次step用了 {end-start} s!')
    globalend = time.time()
    print(f"Algorithm end! \n {epoch}个step，共用时 {globalend - globalstart} s!")


if __name__ == '__main__':
    main()

