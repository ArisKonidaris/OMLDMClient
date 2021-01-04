from sklearn import datasets
from sklearn.model_selection import train_test_split
from sklearn.linear_model import PassiveAggressiveClassifier
from sklearn.preprocessing import PolynomialFeatures
from sklearn.linear_model._stochastic_gradient import SGDClassifier
import numpy as np

NMR_OF_SAMPLES = 1000000
NMR_OF_FEAT = 43
seed = 1000

# Set random seed (for reproducibility)
np.random.seed(100)

dataset = datasets.make_classification(n_samples=NMR_OF_SAMPLES,
                                       n_features=NMR_OF_FEAT,
                                       n_informative=NMR_OF_FEAT - 4,
                                       n_redundant=0,
                                       n_repeated=0,
                                       n_classes=2,
                                       n_clusters_per_class=2)
con_data = np.hstack((dataset[0], dataset[1].reshape(NMR_OF_SAMPLES, 1)))

# f = open('/path/to/where/you/want/the/dataset/to/be/created/data.txt', 'w')
f = open('/home/aris/IdeaProjects/DataStream/DummyDataSet_43f2c_mil_e1.txt', 'w')
for i in range(NMR_OF_SAMPLES):
    data = ""
    for j in range(NMR_OF_FEAT):
        data += str(dataset[0][i][j]) + ","
    data += str(dataset[1][i])
    f.write(data)
    f.write("\n")
f.close()
