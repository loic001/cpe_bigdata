import numpy as np
import json
from pprint import pprint
from sklearn import decomposition
from sklearn import preprocessing
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import pandas as pd

def getAreas():
    areasPath = "/home/lolo/projects/cpe/bigdata/cpe_bigdata/areasParis.json"
    areasData = open(areasPath)
    areasJson = json.load(areasData)

    areas = areasJson


    x = list(map(lambda area: (int(area['name']), area['parkingCount'], area['stairsCount'], area['accessibility'], area['sup']), areas))
    X = np.array(x)
    data = X[:,:]
    import pandas as pd

    arr = pd.DataFrame(data.astype('int32'))
    arr
    arr.columns = ['arr', 'park', 'stairs', 'acc', 'sup']
    return arr.set_index('arr')

getAreas()


def getRichData():
    richData = [[1,2,12272],
    [2,18,5993],
    [3,3,7906],
    [4,4,11478],
    [5,8,7665],
    [6,14,14637],
    [7,26,22547],
    [8,12,15933],
    [9,5,7169],
    [10,2,4858],
    [11,5,5225],
    [12,6,5469],
    [13,5,4926],
    [14,9,6612],
    [15,18,5993],
    [16,56,16582],
    [17,18,9125],
    [18,4,6699],
    [19,3,4817],
    [20,3,4051]]

    rich = np.array(richData)
    richPd = pd.DataFrame(rich)
    richPd.columns = ['arr', 'patrimoine_total', 'impot_moyen']
    richPd = richPd.set_index('arr')
    return richPd


def getAllData():
    return pd.concat([getAreas(), getRichData()], axis=1)
