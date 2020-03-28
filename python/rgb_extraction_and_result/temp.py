from __future__ import division
import cv2
from matplotlib import pyplot as plt
#import numpy as numpy
#import was defined wrongly, as in whole program we are using np for numpy
import numpy as np
from math import cos,sin

#two imports was remaining which was causing error while parsing images from directory
import os

#As we are not using PIL library now, its redundant
#Note : whole program works on only OpenCV
#from PIL import Image

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
     image_with_ellipse = image.copy()
     ellipse = cv2.fitEllipse(contour)
     #addit
     cv2.ellipse(image_with_ellipse,ellipse,green,2,cv2.CV_AA)
     
     
     

def find_leaf(img):
    
     """

          Documentaion link : https://docs.opencv.org/2.4/modules/imgproc/doc/miscellaneous_transformations.html#cvtcolor
          Different types of images classifed in terms of bits : https://en.wikipedia.org/wiki/List_of_monochrome_and_RGB_palettes
          More datat to understand what is x-bit images : https://petapixel.com/2017/09/06/8-bit-vs-16-bit-photos-heres-difference/, https://blog.printaura.com/blog/art-resources/bit-vs-16-bit-images-whats-the-difference-which-to-use


          Syntax : cv2.cvtColor(input_image, flag)
          Where flag determines the type of conversion
          
          In OpenCV the default color format is BGR i.e Blue Green Red
          
          As we are implementing non-linear transforamtion the image should be
          normalized to its proper range that is RGB.

          So we change our color space to BGR to RGB

          Additional Notes : There are around 150 color space transformation in OpenCV

     """
     
     #Step 1
     img = cv2.cvtColor(img,cv2.COLOR_BGR2RGB)  #changing color space

     """
          Here what max function do is return largest number in given iterable
          Iterable is refered to array, tupple, list, etc.,
          e.g. if my array is array = [10, 5 , 18, 3], 
          print(max(array))
          will return 18 as output

          Shape of image is accessed by img.shape. It returns a tuple of number of rows, columns and channels (if image is color) i.e. RGB
          We can term number of rows and columns with pixels

          For more info on image operations i.e. img.shape visit : https://opencv-python-tutroals.readthedocs.io/en/latest/py_tutorials/py_core/py_basic_ops/py_basic_ops.html 

          What we are doing is getting width of the image in terms of pixels (In case of horizontal camera or you can say image)
          While if the camera angle is vertical or you can say image is vertical then the function will return height in terms of pixels

     """
     #Step 2
     max_dim = max(img.shape)
     #Here what we are doing is calculating scaling factor to resize our image to an standard dimension
     scale = 700/max_dim          #since max size 700x600

     """

          More info on cv2.resize() : https://www.tutorialkart.com/opencv/python/opencv-python-resize-image/
          Here we are using cv2.resize() function we are resizing image to standard dimension

          Syntax we are using is : cv2.resize(image_source,desired_size_of_output_image,scale_factor on x axis,scale_factor on y axis)

          Image resizing is necessary for increasing and decreasing pixel density in an image


     """
     img = cv2.resize(img,None,fx=scale,fy=scale)

     #Step 3

     """

          Cleaning image

          As we classifying other objects with our leaf we need to extract some features

          Here what we want to so is classifying our image on the basis of color 
          But to extract color first we have to clean the details of the image that is preprocessing before classifcation

          More info on Gaussian blur : https://en.wikipedia.org/wiki/Gaussian_blur

          Similar like kmeans clustering ? dont know yet.... 

     """
    
     image_blur = cv2.GaussianBlur(img,(7,7),0) 
     #it smoothes the image so that we get image even
     
     image_blur_hsv = cv2.cvtColor(image_blur,cv2.COLOR_RGB2HSV)
     print(image_blur_hsv)
     #hsv seperates luma from chroma(intensity and color sperated)
     #4defining filters
     #first filtering by color
     min_green=np.array()
     max_green=np.array([])

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

     #9 convert back to original color scheme
     bgr = cv2.cvtColor(circled,cv2.COLOR_RGB)
     return bgr

    
    #time to call
dir = "D:/workspace/python/github/iicdc_texas/python/rgb_extraction_and_result/test_dir"
  #change directory to your required location where images are stored
files = os.listdir(dir)
for x in files:

     #im = Image.open(dir + "/" + x) #passing wrong argument to function
     #im = cv2.imread(dir + '/' + x)
 
     #find_leaf(im)
     # instead we can just pass in one line

     #cv2 functions accepts argumetns from cv2 functions only
     find_leaf(cv2.imread(dir + '/' + x))
     
