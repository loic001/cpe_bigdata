import numpy as np
import json
from pprint import pprint
from sklearn import decomposition
from sklearn import preprocessing
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D


areasPath = "/home/lolo/projects/cpe/bigdata/cpe_bigdata/areas.json"
areasData = open(areasPath)

areasJson = json.load(areasData)
areas = areasJson['data']

x = list(map(lambda area: (area['parkingCount'], area['stairsCount'], area['accessibility']), areas))

X = np.array(x)

xNorm = preprocessing.normalize(X)


pca = decomposition.PCA(n_components=1)
pca.fit(xNorm)
transformedX = pca.transform(X)
transformedX = preprocessing.normalize(transformedX)


for i, x in enumerate(transformedX):
    areas[i]['pcaValue']=x[0]


with open('result.json', 'w') as fp:
    json.dump(areas, fp)
