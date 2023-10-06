from flask import Flask, request
import bprDogs
from lightGBMRecommender import lightGBMRecommender
from rfRecommender import rfRecommender
import travel
import json
from flask import jsonify
app = Flask(__name__)
recommender = lightGBMRecommender()
rfRecommender = rfRecommender()

@app.get('/getBPRDogs')
def getBPRDogs():
   q = request.args.get('q')
   similar = bprDogs.getSimilar(int(q))
   numpyData = json.dumps( {"similar": similar.tolist()})
   
   return numpyData

@app.post('/getTravelProperty')
def getTravelProperty():
    if request.is_json:
        fbjson = request.get_json()
        trProp = travel.getTravelProperty(fbjson)
        response = json.dumps( {"viajes": trProp})
        return response, 201
    return {"error": "Request must be JSON"}, 415

@app.post('/getProperties')
def getPlanProperty():
    if request.is_json:
        fbjson = request.get_json()
        trProp = travel.getProperties(fbjson)
        return jsonify(trProp)
    return {"error": "Request must be JSON"}, 415

@app.get('/fitLightGBMDogs')
def fitLightGBMDogs():
    recommender.fit()
    return {"Fitted": True}

@app.post('/predictLightGBMDogs')
def predictLightGBMDogs():
    if request.is_json:
        recommender.fit()
        userDogs = request.get_json()
        print(userDogs)
        recommendation = recommender.predict(userDogs)
        return recommendation
    return {"error": "Request must be JSON"}, 415

@app.get('/fitRFDogs')
def fitRFDogs():
    rfRecommender.fit()
    return {"Fitted": True}

@app.post('/predictRFDogs')
def predictRFDogs():
    if request.is_json:
        rfRecommender.fit()
        userDogs = request.get_json()
        print(userDogs)
        recommendation = rfRecommender.predict(userDogs)
        return recommendation
    return {"error": "Request must be JSON"}, 415

   
    