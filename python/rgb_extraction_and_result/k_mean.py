import numpy as np
import cv2
import os

dir = "D:\\workspace\\python\\vscode\\file_opener\\test_dir"

count = 1

files = os.listdir(dir)

for x in files:

    img = cv2.imread('D:/workspace/python/vscode/file_opener/test_dir/' + x)
    img2 = img.reshape((-1,3))                                                  #to flatten the image,from pixel cordinate format to pixel area format 
    img2 = np.float32(img2)                                                     #for opencv opns we need float32 datatype(by def its int8)
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER , 10, 1.0)   #criteria for stopping the calcs 

    k = 5                                                                     #no. of clusters to be formed                                                               
    attempts = 10                                                              #no. of times the algo is executed
    ret, label, center = cv2.kmeans(img2,k,None, criteria, attempts, cv2.KMEANS_PP_CENTERS)

    center = np.uint8(center)

    res  = center[label.flatten()]
    res2 = res.reshape((img.shape))

    cv2.imwrite('D:/workspace/python/vscode/file_opener/segmented/segmented_' + str(count) + '.jpg',res2)

    count += 1  
