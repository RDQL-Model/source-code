#!/usr/bin/python3
# coding = UTF-8
# this is program for DQN with torch
# @python version 3.7.9
# @code by


import warnings
warnings.simplefilter('default')

import copy
import torch
import torch.optim as optim
import torch.nn.functional as F
import parl

__all__ = ['DQN']

device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")

class DQN(parl.Algorithm):
    def __init__(self, model, act_dim=6, gamma=None, lr=None):
        self.model = model
        self.target_model = copy.deepcopy(model)

        self.gamma = gamma
        self.lr = lr

        self.mse_loss = torch.nn.MSELoss()
        self.optimizer = optim.Adam(self.model.parameters(), lr=self.lr)

    def predict(self, obs):

        with torch.no_grad():
            pred_q = self.model(obs)
        return pred_q

    #
    def learn(self, batch):
        non_final_mask = torch.tensor(tuple(map(lambda s: s is not None, batch.next_obs)), device=device,
                                      dtype=torch.bool)
        non_final_next_states = torch.cat(
            [s for s in batch.next_obs if s is not None])

        # 下面是将几个变量都转换为batch的类型
        state_batch = torch.cat(batch.obs)
        action_batch = torch.cat(batch.action)
        reward_batch = torch.cat(batch.reward)

        state_action_values = self.model(state_batch).gather(1, action_batch)  # Q(s,a)的值

        # batch_size 是 32
        next_state_values = torch.zeros(32, device=device)
        next_state_values[non_final_mask] = self.target_model(non_final_next_states).max(1)[0].detach()

        # Compute the expected Q values
        expected_state_action_values = (next_state_values * self.gamma) + reward_batch
        # expected_state_action_values = state_action_values + 0.3 * (reward_batch + 0.3 * next_state_values - state_action_values)

        # Compute loss
        loss = F.smooth_l1_loss(state_action_values, expected_state_action_values.unsqueeze(1))

        # Optimize the model
        self.optimizer.zero_grad()
        loss.backward()

        self.optimizer.step()
        return loss

    def sync_target(self):
        self.target_model.load_state_dict(self.model.state_dict())