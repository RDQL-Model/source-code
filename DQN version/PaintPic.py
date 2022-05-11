#!/usr/bin/python3
# coding = UTF-8
# this is program for 
# @python version 3.7.9
# @code by


import matplotlib.pyplot as plt
import os
import copy
import imageio

def readCoordinate(path):
    fileReader = open(path, 'r')
    xCoor = list()
    yCoor = list()
    while True:
        string = fileReader.readline()
        if len(string) == 0:
            break
        coor = string.split('\t')
        coor[2].replace('\n', '')
        xCoor.append(float(coor[1]))
        yCoor.append(float(coor[2]))

    fileReader.close()
    return xCoor, yCoor

def moreFileExtract(filepath):
    if not os.path.exists(filepath):
        exit('notFountFile!')
    fileList = os.listdir(filepath)
    xCoor = list()
    yCoor = list()
    for fileName in fileList:
        f = filepath + '/' + fileName
        if os.path.isdir(f):
            continue
        xtemp, ytemp = readCoordinate(filepath+'/'+fileName)
        xCoor = xCoor + xtemp
        yCoor = yCoor + ytemp
    return xCoor, yCoor

def fileMorepic(filePath, savePath, long, short):
    if not os.path.exists(filePath):
        exit(1)
    fileList = os.listdir(filePath)
    for fileName in fileList:
        f = filePath + '/' + fileName
        if os.path.isdir(f):
            continue

        xCoor, yCoor = readCoordinate(filePath + '/' + fileName)
        plt.figure(figsize=(9, 9))
        plt.grid(linestyle='-.', linewidth=1.5)
        plt.rc('axes', axisbelow=True)
        plt.xlim(left=-0.2, right=long + 0.2)
        plt.ylim(bottom=-0.2, top=short + 0.2)
        plt.xlabel(fileName)
        plt.xticks(range(long + 1))
        plt.yticks(range(short + 1))
        plt.scatter(xCoor, yCoor)
        # for index in range(27):
        #     plt.annotate(str(index), xy=(xCoor[index], yCoor[index]), fontsize=5)
        plt.savefig(savePath + '/' + fileName + '.png')


def reName(filePath):
    fileList = os.listdir(filePath)
    print(fileList)
    for fileName in fileList:
        newName = copy.deepcopy(fileName)
        newName = newName.replace('lowDimensionData', '')
        newName = newName.replace('.txt', '')
        os.chdir('H:\PythonProject\TuringPatternPro' + '/' + filePath)
        os.rename(fileName, newName)

def generate_gif(image_paths, gif_path, duration=0.1):
    frames = []
    for image_path in image_paths:
        frames.append(imageio.imread(image_path))
    imageio.mimsave(gif_path, frames, 'GIF', duration=duration)


def Gmain(imgFolder, gifPath):
    image_folder = imgFolder
    gif_path = gifPath

    image_paths = []
    files = os.listdir(image_folder)
    files.sort(key=lambda x: int(x[:-4]))
    print(files)
    for file in files:
        image_path = os.path.join(image_folder, file)
        image_paths.append(image_path)

    duration = 0.1
    generate_gif(image_paths, gif_path, duration=0.1)



if __name__ == '__main__':

    xCoor, yCoor = moreFileExtract('./src/files/lowDimension')

    print(xCoor)
    print(yCoor)
    plt.figure(figsize=(10, 10))
    # plt.xlabel('Java LowDimension')
    plt.xlabel('Python Origin Program Low Dimension ALL')
    plt.xticks(range(10))
    plt.yticks(range(10))
    plt.scatter(xCoor, yCoor)
    plt.show()


    fileMorepic('./src/files/lowDimension/', './src/files/lowDimension/img', 10, 10)

