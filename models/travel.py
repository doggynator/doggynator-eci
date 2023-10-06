from pyspark.sql import SparkSession
from pyspark.sql.types import StructType,StructField, StringType, IntegerType,BooleanType,DoubleType
from pyspark.sql.functions import col, asc,desc
from pyspark.sql.functions import month
import pyspark.sql.functions as F
from pyspark import SparkContext
import json
from datetime import date
from datetime import datetime
import string
import nltk
import re
from nltk.tokenize import word_tokenize, sent_tokenize
from nltk.corpus import stopwords
import pandas as pd

fbSchema = StructType([\
        StructField("created_time", StringType(), True), \
        StructField("description", StringType(), True), \
        StructField("id", StringType(), True), \
        StructField("message", StringType(), True), \
        StructField("name", StringType(), True), \
        StructField("object_id", StringType(), True), \
        StructField("place", StructType([\
            StructField("id", StringType(), True), \
            StructField("location", StructType([\
                StructField("city", StringType(), True), \
                StructField("country", StringType(), True), \
                StructField("latitude", StringType(), True), \
                StructField("located_in", StringType(), True), \
                StructField("longitude", StringType(), True), \
                StructField("state", StringType(), True), \
                StructField("street", StringType(), True), \
                StructField("zip", StringType(), True), \
            ]), True), \
            StructField("name", StringType(), True)
            ]), True), \
            StructField("type", StringType(), True)
        ])
groupSchema = StructType([\
        StructField("description", StringType(), True), \
        StructField("id", StringType(), True), \
        StructField("name", StringType(), True)
        ])
EMOJI_PATTERN = re.compile(
    "(["
    "\U0001F1E0-\U0001F1FF"  # flags (iOS)
    "\U0001F300-\U0001F5FF"  # symbols & pictographs
    "\U0001F600-\U0001F64F"  # emoticons
    "\U0001F680-\U0001F6FF"  # transport & map symbols
    "\U0001F700-\U0001F77F"  # alchemical symbols
    "\U0001F780-\U0001F7FF"  # Geometric Shapes Extended
    "\U0001F800-\U0001F8FF"  # Supplemental Arrows-C
    "\U0001F900-\U0001F9FF"  # Supplemental Symbols and Pictographs
    "\U0001FA00-\U0001FA6F"  # Chess Symbols
    "\U0001FA70-\U0001FAFF"  # Symbols and Pictographs Extended-A
    "\U00002702-\U000027B0"  # Dingbats
    "])"
  )

familyDict = ['familia', 'hijo', 'hija', 'amor', 'bendicion', 'mama', 'papa', 'hijos', 'hijas', 'padres']
activityDict = ['ejercicio', 'fitness', 'correr', 'fit', 'salir', 'mm', 'reto', 'carrera', 'runner', 'crossfit', 'maraton', 'montana', 'media maraton', 'kilometro', 'entrenador', 
                'fisica', 'fisico', 'open water', 'buceo', 'fortalecimiento', 'tecnica', 'reto', 'rep', 'wallball', 'training']

spark = SparkSession.builder \
    .master("local[1]") \
    .appName("doggynator.com") \
    .getOrCreate()

def getProperties(fbData):

    type(fbData)
    #rdd = sc.parallelize(fbData)
    #df = spark.createDataFrame(fbData)
    birthday = fbData['birthday']
    age = fbData['age_range']['min']
    ageResp = 'no informa'
    if birthday is not None:
        ageResp = getAge(birthday)
    elif age is not None:
        ageResp = age
    feed = fbData['feed']['data']
    rdd = spark.sparkContext.parallelize(feed)
    df = spark.createDataFrame(rdd, schema=fbSchema)
    #df.show(truncate=False)
    cantidadSalidas =df.filter("place IS NOT NULL").where("place.location.city == 'Bogotá'").select('place.location.city', 'place.name', 'created_time', month(col('created_time'))).distinct().count()
    #print(f"cantidad de salidas en el ultimo año : {cantidadSalidas}")
    if cantidadSalidas == 0:
        viajes="no informa"
        plan="no informa"
    else:
        cantidadViajes = df.filter("place IS NOT NULL").where("place.location.city != 'Bogotá'").select('place.location.city', month(col('created_time'))).distinct().count()
        #print(f"cantidad de viajes en el ultimo año : {cantidadViajes}")
        if cantidadViajes/12 > 0.5:
            viajes="si"
        else:
            viajes="no"
        cantidadPlanes = df.filter("place IS NOT NULL and place.name NOT LIKE '%Home%'").where("place.location.city == 'Bogotá'").select('place.location.city', month(col('created_time'))).distinct().count()
        if cantidadPlanes/12 > 0.5:
            plan="si"
        else:
            plan="no"
    feeddf = df.filter("message IS NOT NULL").select('message').distinct().toPandas() 
    family = getFamily(feeddf)
    activity = getActivity(feeddf, fbData)
    response = {"Edad": ageResp,
                            "Viajes": viajes,
                            "Plan": plan, 
                            "familia": family,
                            "actividad": activity}
    return response

def getActivity(feed, fbData):
    if len(feed)>0:
        feed = feed.apply(lambda x: getCleanText(x['message']), axis=1)
        isPresent = []
        for post in feed:
            isPresent.append(getIsPresent(post, activityDict))
        msgs = pd.DataFrame(isPresent, columns=['isPresent'])
        activityFeedRate = len(msgs[msgs['isPresent']==True])/len(msgs)
        if activityFeedRate > 0.3:
            return "si"

    groups = fbData['groups']['data']
    rdd = spark.sparkContext.parallelize(groups)
    df = spark.createDataFrame(rdd, schema=groupSchema).select('name', 'description').toPandas()
    if len(df)>0:
        df['message'] = df['name'].fillna('') + ' ' + df['description'].fillna('')
        df['message'] = df.apply(lambda x: getCleanText(x['message']), axis=1)
        isPresent = []
        for group in df['message']:
            isPresent.append(getIsPresent(group, activityDict))
        gr = pd.DataFrame(isPresent, columns=['isPresent'])
        activityGroupRate = len(gr[gr['isPresent']==True])/len(gr)
        if activityGroupRate > 0.3:
            return "si"
    return "no"

def getAge(strn):
    birthdate = datetime.strptime(strn, '%m/%d/%Y').date()
    today = date.today()
    age = today.year - birthdate.year - ((today.month, today.day) < (birthdate.month, birthdate.day))
    return age

def getFamily(feed):
    if len(feed) == 0:
        return "no informa"
    else:
        feed = feed.apply(lambda x: getCleanText(x['message']), axis=1)
        isPresent = []
        for post in feed:
            isPresent.append(getIsPresent(post, familyDict))
        msgs = pd.DataFrame(isPresent, columns=['isPresent'])
        familyRate = len(msgs[msgs['isPresent']==True])/len(msgs)
        if familyRate > 0.3:
            return "si"
        else:
            return "no"

def getCleanText(text):
    palabras = text.split()
    palabras = [palabra.lower() for palabra in palabras]
    palabras = [re.sub("\\#[A-z]*", "", x) for x in palabras] #eliminamos hashtag
    palabras =[re.sub("[A-z]*\\:{1}\\/*[A-z]*\\.[A-z]*\\/*[A-z0-9]*\\s*", "", x) for x in palabras] #links
    palabras =[re.sub("\n*", "", x) for x in palabras] #saltos de linea
    palabras =[re.sub(EMOJI_PATTERN, "", x) for x in palabras] #emoji
    palabras =[re.sub("\\@[A-z0-9]*", "", x) for x in palabras] #usuarios
    palabras =[re.sub("\\<U*[[:punct:]][A-z0-9]*>", "", x) for x in palabras] #caracteres raros
    table = str.maketrans('', '', string.punctuation)
    stripped = [w.translate(table) for w in palabras]
    stop_words = set(stopwords.words('spanish')) 
    filtered_sentence = [w for w in stripped if not w in stop_words] 
    filtered_sentence = [] 
    for w in stripped: 
        if w not in stop_words: 
            filtered_sentence.append(w)
    words = ' '
    for word in filtered_sentence:
        words = words + ' ' + word
    return words

def getIsPresent(post, dict):
    for cue in dict:
        if(post.find(cue) > 0):
            return True
    return False

