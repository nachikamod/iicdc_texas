from __future__ import division
import cv2
from matplotlib import pyplot as plt
import numpy as np
from math import cos,sin

from PIL import Image
import os

def find_leaf(image, image_name):

    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

    scale = 700/max(image.shape)
    image = cv2.resize(image,None,fx=scale,fy=scale)

    image_blur = cv2.GaussianBlur(image,(7,7),0)
    image_blur_hsv = cv2.cvtColor(image_blur,cv2.COLOR_RGB2HSV)
    
    print(image_blur_hsv)
    cv2.imshow(image_name,image_blur_hsv)

    min_green=np.array([42,79,65])
    max_green=np.array([100,255,255])

    #fill it up

    mask1=cv2.inRange(image_blur_hsv,min_green,max_green)

    cv2.imshow("1" + image_name, mask1)
     #to filter out the brightness
    ming2=np.array([20,100,100])     
    maxg2=np.array([40,255,255])

    mask2=cv2.inRange(image_blur_hsv,ming2,maxg2)

    cv2.imshow("2" + image_name, mask2)
    #cv2.imshow("test2.jpg", mask2)

    mask = mask1 + mask2

    cv2.imshow("test.jpg", mask)

    cv2.waitKey(0)
    cv2.destroyAllWindows()

def kmean_preprocessing(directory):
    img = cv2.imread(directory)
    img2 = img.reshape((-1,3)) 
    img2 = np.float32(img2)
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER , 10, 1.0)

    k = 5  
    attempts = 10    
    ret, label, center = cv2.kmeans(img2,k,None, criteria, attempts, cv2.KMEANS_PP_CENTERS)

    center = np.uint8(center)

    res  = center[label.flatten()]
    
    return res.reshape((img.shape))

dir = "D:/workspace/python/github/iicdc_texas/python/rgb_extraction_and_result/data_set"
  
files = os.listdir(dir)
for x in files:
    find_leaf(kmean_preprocessing(dir + '/' + x), x)
    

    
