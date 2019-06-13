#Image to NumpyArray Converter

import numpy
import PIL.Image
import requests
import sys

#image_path = str(sys.argv[1])


SERVER_URL = 'http://192.168.153.137:8501/v1/models/faster_rcnn:predict'
categories = ['person',
'bicycle',
'car',
'motorcycle',
'airplane',
'bus',
'train',
'truck',
'boat',
'traffic light',
'fire hydrant',
'street sign',
'stop sign',
'parking meter',
'bench',
'bird',
'cat',
'dog',
'horse',
'sheep',
'cow',
'elephant',
'bear',
'zebra',
'giraffe',
'hat',
'backpack',
'umbrella',
'shoe',
'eye glasses',
'handbag',
'tie',
'suitcase',
'frisbee',
'skis',
'snowboard',
'sports ball',
'kite',
'baseball bat',
'baseball glove',
'skateboard',
'surfboard',
'tennis racket',
'bottle',
'plate',
'wine glass',
'cup',
'fork',
'knife',
'spoon',
'bowl',
'banana',
'apple',
'sandwich',
'orange',
'broccoli',
'carrot',
'hot',
'pizza',
'donut',
'cake',
'chair',
'couch',
'potted plant',
'bed',
'mirror',
'dining table',
'window',
'desk',
'toilet',
'door',
'tv',
'laptop',
'mouse',
'remote',
'keyboard',
'cell phon',
'microwave',
'oven',
'toaster',
'sink',
'refrigerator',
'blender',
'book',
'clock',
'vase',
'scissors',
'teddy bear',
'hair drier',
'toothbrush',
'hair brush']

def predict(image_path):
	image = PIL.Image.open(image_path)
	image_np = numpy.array(image)
	payload = {"instances": [image_np.tolist()]}
	res = requests.post(SERVER_URL, json=payload)
	print(res)
	
	with open('MyResponse.txt','w') as outfile:
		outfile.write(str(res.json()))
		
	outfile.close()
	string = parse(res)
	return string

	
def unique(list1,scores): 
  
    # intilize a null list 
    unique_list = [] 
    unique_score = []  
    # traverse for all elements 
    for x in list1: 
        # check if exists in unique_list or not 
        if x not in unique_list: 
            unique_list.append(x) 
            unique_score.append(scores[unique_list.index(x)])
	
    return unique_list, unique_score
	
def parse(res):
	
	classes = (res.json()['predictions'][0]['detection_classes'])
	scores = (res.json()['predictions'][0]['detection_scores'])
	
	
	classes = classes[:15]
	scores = scores[:15]
	
	top_classes = []
	top_scores = []
	for i in range(0,15):
		if scores[i] >= 0.60:
			top_classes.append(classes[i])
			top_scores.append(scores[i])
			
	uniq_class, uniq_score = unique(top_classes,top_scores)
	

	print("Identifiesd: " + str(uniq_class))
	print("Predicted Score: " + str(uniq_score))
	string = map_classes(uniq_class)
	return string
	
def map_classes(classes):
	#print("List Of Objects: ")
	#for obj in classes:
	#	print(categories[int(obj)-1])
	string = ""
	for obj in classes:
		string = string + " " + categories[int(obj)-1]
	return string
