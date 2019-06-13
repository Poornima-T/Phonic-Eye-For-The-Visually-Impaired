from flask import Flask, request, Response

import numpy as np
import cv2
import base64
import predict
import time

 
app = Flask(__name__)

@app.route('/',methods=['GET','POST'])
def upload():
	image = request.values.get("image")
	#with open("out.txt","w") as fp:
	#    fp.write(str(image))
	#fp.close()
	with open("imageToSave.png", "wb") as fh:
		fh.write(base64.b64decode(image))
	fh.close()	

	str = predict.predict('imageToSave.png')
	#str = "true"
	with open('results.txt','w') as fp:
		fp.write(str)
	fp.close
	#time.sleep(4000)
	return str
	
if __name__ == "__main__":
	app.run(host='0.0.0.0', threaded = True)