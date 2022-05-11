#!/usr/bin/python3
# coding = UTF-8
# this is program for TorchModel
# @python version 3.7.9
# @code by

import torch.nn as nn
import torch.nn.functional as F


class TorchModel(nn.Module):
    def __init__(self, obs_dim, act_dim):
        super(TorchModel, self).__init__()
        self.fc1 = nn.Linear(obs_dim, 128)
        self.fc2 = nn.Linear(128, 64)
        self.fc3 = nn.Linear(64, 32)
        self.fc4 = nn.Linear(32, act_dim)


    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = F.relu(self.fc3(x))
        x = self.fc4(x)
        return x

if __name__ == '__main__':
    model = TorchModel(80, 6)
    # print(get_parameter_number(model))
    print(list(model.parameters()))


