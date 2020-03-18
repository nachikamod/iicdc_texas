import os 
import time
from PIL import Image
import cv2 

dir = "D:\\workspace\\python\\vscode\\file_opener\\test_dir"  #change directory to your required location where images are stored

files = os.listdir(dir)

for x in files:
    rot = [0,0,0]
    im = Image.open(dir + "\\" + x)
    pix = im.load()
    im_size = im.size
    
    im_height = im_size[0]
    im_width = im_size[1]

    print(x)
    print( )

    for i in range(-1,2):
        for j in range(-1,2):
            
            tup = pix[(im_height/2)+i,(im_width/2)+j]

            for k in range(3):
                rot[k] += tup[k]
                
                if i == 1 and j == 1 and k == 2:
                    rgb_dump = [number / 9 for number in rot]
                    print(rgb_dump)

                    if int(rgb_dump[0]) in range(150,256) and int(rgb_dump[1]) in range(150,256) and int(rgb_dump[2]) in range(-1,56):
                        print("Yellow found")

