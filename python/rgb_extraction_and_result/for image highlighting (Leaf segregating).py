from __future__ import division
import cv2
from matplotlib import pyplot as plt
import numpy as numpy
from math import cos,sin



def show(img):
     plt.figure(figsize = (15,15))
     plt.imshow(img,interpolation='nearest')


def overlay_mask(mask, image):
    rgb_mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2RGB)
    img = cv2.addWeighted(rgb_mask, 0.5, image, 0.5, 0)
    show(img)
    
    
def find_big_leaf(image):
     image = image.copy()
     contour,heirarchy = cv2.findContours(image,cv2.RETR_LIST,cv2.CHAIN_APPROX_SIMPLE)
     
     #isolating largest contour 
     contour_sizes = [(cv2.contourArea(contour),contour) for contour in contours]
     biggest_contour = max(contour_sizes,key = lambda x: x[0])[1]
     
     #retruning the biggest contour
     mask = np.zeroes(image.shape, np.uint8)
     cv2.drawContours(mask, [biggest_contour],-1, 255, -1)
     return biggest_contour,mask
     
def Circle_contour(image,contour):
     #bounding ellipse
     image_with_ellipse = image_copy()
     ellipse = cv2.fitEllipse(contour)
     #addit
     cv2.ellipse(image_with_ellipse,ellipse,green,2,cv2.CV_AA)
     
     
     

def find_leaf(img):
    
    #1.convert to the correct color scheme
    
     img = cv2.cvtColor(img,cv2.COLOR_BGR2RGB)

     
     #2.to scale our image
     
     max_dim = max(img.shape)
     scale = 700/max_dim          #since max size 700x600
     img = cv2.resize(img,None,fx=scale,fy=scale)

     #3clean our image
    
     image_blur = cv2.GaussianBlur(img,(7,7),0) 
     #it smoothes the image so that we get image even
     
     image_blur_hsv = cv2.cvtColor(image_blur,cv2.COLOR_RGB2HSV)
     print(image_blur_hsv)
     #hsv seperates luma from chroma(intensity and color sperated)
  '''   

'''

     #4defining filters
     #first filtering by color
     min_green=np.array([#max amount of green intensity])
     max_green=np.array([#here as well])

     #fill it up

     mask1=cv2.inRange(image_blur_hsv,min_green,max_green)

     #to filter out the brightness
     ming2=np.array([])
     maxg2=np.array([])
     mask2=cv2.inRange(image_blur_hsv,ming2,maxg2)

     mask = mask1 + mask2

     #5-Segmentation

     kernel=cv2.getStructureingElement(cv2.MORPH_ELLIPSE,(15,15))     #THE VALUES TO BE CHANGED ...
     mask_closed = cv2.morphologyEx(mask,cv2.MORPH_CLOSE,kernel)
     mask_clean = cv2.morphologyEx(mask_closed,cv2.morph)
     
     #6 FIND BIGGEST LEAF
     big_leaf_contour , mask_strawberriesn=find_biggest_contour(mask_clean)


     #7  overlay

     overlay = overlay_mask(mask_clean,image)

     '''
     lets add this only if needed else lets delete it
     
    #8 circle the main leaf
     circled = circle_contour(overlay,big_leaf)

     show(circled)
     '''
    
     #9 convert back to original color scheme
     bgr = cv2.cvtColor(circled,cv2.COLOR_RGB)
     return bgr

    
    #time to call
dir = "D:\\workspace\\python\\vscode\\file_opener\\test_dir"  #change directory to your required location where images are stored
files = os.listdir(dir)
for x in files:
    im = Image.open(dir + "\\" + x)
    find_leaf(im)
     
