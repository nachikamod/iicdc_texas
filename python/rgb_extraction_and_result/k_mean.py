import numpy as np
import cv2
import os

dir = "D:\\workspace\\python\\vscode\\file_opener\\test_dir"

count = 1

files = os.listdir(dir)

for x in files:

    img = cv2.imread('D:/workspace/python/vscode/file_opener/test_dir/' + x)
    img2 = img.reshape((-1,3))
    img2 = np.float32(img2)
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER , 10, 1.0)

    k = 5
    attempts = 10
    ret, label, center = cv2.kmeans(img2,k,None, criteria, attempts, cv2.KMEANS_PP_CENTERS)

    center = np.uint8(center)

    res  = center[label.flatten()]
    res2 = res.reshape((img.shape))

    cv2.imwrite('D:/workspace/python/vscode/file_opener/segmented/segmented_' + str(count) + '.jpg',res2)

    count += 1  