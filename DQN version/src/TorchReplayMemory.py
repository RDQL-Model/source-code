#!/usr/bin/python3
# coding = UTF-8
# this is program for 
# @python version 3.7.9
# @code by

from collections import deque, namedtuple
import random


Experience = namedtuple('Experience', ['obs', 'action', 'reward', 'next_obs'])


class ReplayMemory(object):
    def __init__(self, capacity):
        self.capacity = capacity
        self.memory = []
        self.position = 0

    def append(self, *args):
        if len(self.memory) < self.capacity:
            self.memory.append(None)
        self.memory[self.position] = Experience(*args)
        self.position = (self.position + 1) % self.capacity

    def sample(self, batch_size):
        exp = random.sample(self.memory, batch_size)

        return exp

    def __len__(self):
        return len(self.memory)


if __name__ == '__main__':
    momery = ReplayMemory(500)
    momery.append(1, 1, 1, 1)
    momery.append(2, 2, 2, 2)
    momery.append(3, 3, 3, 3)
    momery.append(4, 4, 4, 4)
    momery.append(5, 5, 5, 5)
    momery.append(6, 6, 6, 6)
    momery.append(7, 7, 7, 7)
    momery.append(8, 8, 8, 8)
    momery.append(9, 9, 9, 9)

    exp = momery.sample(5)
    print(exp)
    batch = Experience(*zip(*exp))
    print(batch)
    print(batch.obs)
    # state_batch = torch.cat(batch.obs)

    print(f"{type((batch.obs))}", batch.obs)
