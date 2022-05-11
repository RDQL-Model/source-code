#!/usr/bin/python3
# coding = UTF-8
# this is program for RegionRandom
# @python version 3.7.9
# @code by


import numpy as np


class RegionRandom():
    def __init__(self):
        self.userList = {}
        self.phoneModeVec = []
        self.phoneBrandUserAgeMap = {}
        self.xCoordinate = 0
        self.yCoordinate = 0

    def GetOneCoordinate(self, x, y, xScale, yScale):
        self.xCoordinate = np.random.random() * xScale + x
        self.yCoordinate = np.random.random() * yScale + y


def main():
    test = RegionRandom()
    for i in range(10):
        test.GetOneCoordinate(1.5, 4.5, 2.2, 3.1)
        print(str(i + 1) + "**********")
        print("x: " + str(test.xCoordinate) + "\t y: " + str(test.yCoordinate))


if __name__ == '__main__':
    main()
