#!/usr/bin/env python
# -*- coding: utf-8 -*-

import cPickle
from collections import defaultdict
import numpy as np

# gloveFile = "/home/woo88/cs774/glove/glove.6B.50d.txt"
gloveFile = "/home/woo88/cs774/vectors.txt"
bowFile = "/home/woo88/cs774/dataset/bow.pkl"
outputFile = "/home/woo88/cs774/glove.2M.pkl"


def main():
    bow = readBowData()
    wordTovectors = setWordToVectors()
    i = 1
    wordidVectorsArr = []
    for each in bow:
        wordidVectorsArr.append([i] + wordTovectors[each])
        i += 1

    data = np.array(wordidVectorsArr)
    data.dump(outputFile)
    # writeFile(wordTovectors)


def main2():
    bow = cPickle.load(open('data/bow.pkl'))
    M = 5
    m = Word2Vec.load('data/word2vecmodels/model%d.mm' % (M))
    word_to_vec = np.array([m[bow[i]] for i in xrange(len(bow))])

    # word_to_vec = cPickle.load(open('data/word_to_vec_pkl'))
    print word_to_vec[0][0]


def readBowData():
    bow = cPickle.load(open(bowFile, 'rb'))
    return bow


def setWordToVectors():
    wordTovectors = defaultdict(list)

    f = open(gloveFile, 'r')
    lines = f.readlines()

    for line in lines:
        strArr = line.split()

        for i in range(1, len(strArr)-1):
            wordTovectors[strArr[0]].append(strArr[i])

    return wordTovectors


def writeFile(wordTovectors):
    output = open(outputFile, 'wb')
    pickle.dump(wordTovectors, output)
    output.close()

if __name__ == '__main__':
    main2()
