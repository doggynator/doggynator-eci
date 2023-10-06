#importamos librerías
import pandas as pd
import random
import category_encoders as ce
from sklearn.ensemble import RandomForestClassifier
import psycopg2

class rfRecommender:
    def __init__(self):
        self.model = RandomForestClassifier(max_depth= 17, min_samples_leaf= 3, min_samples_split= 5, n_estimators= 50)
        self.encoder = ce.OneHotEncoder(cols=['rangoEdadPerro', 'rangoEdadH', 'viajes', 'tipofamilia', 'actividad_x', 'salir', 'estilo', 'vivienda', 'estrato', 'sueldo', 'trabajo', 'tamano', 'sexo', 'color', 'pelaje', 'agresividad', 'atencionesespeciales', 'ninos', 'entrenado', 'esterilizado', 'perros', 'actividad_y'])
        self.isFitted = False

    def loadDbtoFit(self):
        #leo las bases de datos 
        #usuario = pd.read_csv('./Datasets/usuario.csv', delimiter=",", encoding="utf-8")
        #megusta = pd.read_csv('./Datasets/megusta.csv', delimiter=",", encoding="utf-8")
        #ustage = pd.read_csv('./Datasets/user_stage.csv', delimiter=",", encoding="utf-8")
        perros = pd.read_csv('./Datasets/RDB.csv', delimiter=",", encoding="utf-8")
        # app_user = pd.read_csv('./Datasets/app_user.csv', delimiter=",", encoding="utf-8")
        #usuario = usuario.drop(['cluster', 'ciclovia', 'locha', 'disciplina', 'silencio', 'amigos', 'pyState'], axis=1)
        usuario, megusta, ustage, app_user = self.loadDataframeFromDB()
        result = pd.merge(usuario, ustage, on='userid')
        result = pd.merge(result, app_user, on='userid')
        mean_rate=[]
        for index, row in result.iterrows():
            m = megusta[megusta['userid']==row['userid']]['megusta'].mean()
            mean_rate.append(m)
        result['mean_rate']=mean_rate
        result2 = pd.merge(result, megusta, on='userid')
        indexUserid = result2[ (result2['userid'] <16)].index
        result2.drop(indexUserid , inplace=True)
        indexUserid = result2[ (result2['mean_rate'] == 5.000000) | (result2['mean_rate'] == 1.000000) ].index
        result2.drop(indexUserid , inplace=True)
        result3 = pd.merge(result2,  perros, on='perroid')
        result3 = result3.drop([ 'Nombre', 'Raza', 'Fundacion', 'Necesidades', 'cluster' ], axis=1)
        result3['viajes'] = result3['viajes'].apply(lambda x: "y" if x == 1 else "n")
        result3['megusta'] = result3['megusta'].apply(lambda x: "y" if x == 5 else "n")
        result3['atencionesespeciales'] = result3['atencionesespeciales'].apply(lambda x: "y" if x == 1 else "n")
        result3['ninos'] = result3['ninos'].apply(lambda x: "y" if x == 1 else "n")
        result3['entrenado'] = result3['entrenado'].apply(lambda x: "y" if x == 1 else "n")
        result3['esterilizado'] = result3['esterilizado'].apply(lambda x: "y" if x == 1 else "n")
        result3['perros'] = result3['perros'].apply(lambda x: "y" if x == 1 else "n")
        result3['actividad_y'] = result3['actividad_y'].apply(lambda x: "y" if x == 1 else "n")
        result3['rangoEdadPerro']=result3['Edad'].apply(lambda x: "a" if x<5 else ( "b" if (x>=5 and x<10) else "c"))
        result3['rangoEdadH']=result3['edad'].apply(lambda x: "a" if x<20 else ( "b" if (x>=20 and x<30) else ( "c" if (x>=30 and x<40) else "d")))
        result3 = result3.drop(['stage', 'mean_rate', 'provider', 'edad', 'Edad'], axis=1)
        X = self.encoder.fit_transform(result3.drop(['megusta'], axis=1))
        return X, result3

    # creamos funcion para separar la base de datos 70% entrenamiento 30% prueba en donde ambas bases de datos tengan los mismo usuario
    def split_train_test(self, x):
        test_size=0.3
        uids = x['userid'].unique()
        updict = dict()
        xtest = dict()
        xtrain = dict()
        for u in uids:
            updict[u]=[]
            xtest[u]=[]
            xtrain[u]=[]
        for idx, row in x.iterrows():
            updict[row['userid']].append(row['perroid'])
        for u in uids:
            plist = updict[u]
            count = round(len(plist)*test_size)
            testchoices = random.sample(plist, k=count)
            trainchoices = list(set(plist).difference(testchoices))
            xtest[u].append(testchoices)
            xtrain[u].append(trainchoices)
        xtestdf = pd.DataFrame(columns=x.columns)
        xtraindf = pd.DataFrame(columns=x.columns)
        for u in uids:
            for p in xtest[u]:
                xtestdf = pd.concat([xtestdf, x.loc[(x.userid == u) & (x.perroid.isin(p))]])
        for u in uids:
            for p in xtrain[u]:
                xtraindf = pd.concat([xtraindf, x.loc[(x.userid == u) & (x.perroid.isin(p))]])
        xtraindf[x.columns] = xtraindf[x.columns].astype(str).astype("uint8")
        xtestdf[x.columns] = xtestdf[x.columns].astype(str).astype("uint8")
        return xtest, xtrain, xtestdf, xtraindf

    def fit(self):
        X, result = self.loadDbtoFit()
        y_encoder = ce.OneHotEncoder(cols=['megusta'])
        resp = y_encoder.fit_transform(result['megusta'])
        X['megusta_1'] = resp['megusta_1']
        X['megusta_2'] = resp['megusta_2']
        #dictest, dictrain, dftest, dftrain = self.split_train_test(X)
        X_train=X.drop(['megusta_1', 'megusta_2', 'userid', 'perroid'], axis=1)
        #X_test=dftest.drop(['megusta_1', 'megusta_2', 'userid', 'perroid'], axis=1)
        y_train=X['megusta_1'] #megusta = 1, no me gusta = 0
        #y_test=dftest['megusta_1']
        #sort X_train columns 
        X_train.sort_index(axis=1, inplace=True)
        self.model.fit(X_train,y_train)
        self.isFitted = True
    
    def predict(self, userDogs):
        userDogsDf = pd.DataFrame(userDogs)
        dogsId = userDogsDf['perroid']
        dfEncoded = self.fitData(userDogsDf)
        dfToPredict = dfEncoded.drop(['userid', 'perroid'], axis=1)
        dfToPredict.sort_index(axis=1, inplace=True)
        y_predict_proba = self.model.predict_proba(dfToPredict)
        frame = {'perroid':dogsId, 'prediction_prob': y_predict_proba[:,1]}
        result = pd.DataFrame(frame)
        recommendation = result.sort_values(by=['prediction_prob'], ascending=False).iloc[:12]['perroid'].to_numpy().tolist()
        return {'perros': recommendation}
    
    def fitData(self, dataToPredict):
        dataToPredict['viajes'] = dataToPredict['viajes'].apply(lambda x: "y" if x == 1 else "n")
        dataToPredict['atencionesespeciales'] = dataToPredict['atencionesespeciales'].apply(lambda x: "y" if x == 1 else "n")
        dataToPredict['ninos'] = dataToPredict['ninos'].apply(lambda x: "y" if x == 1 else "n")
        dataToPredict['entrenado'] = dataToPredict['entrenado'].apply(lambda x: "y" if x == 1 else "n")
        dataToPredict['esterilizado'] = dataToPredict['esterilizado'].apply(lambda x: "y" if x == 1 else "n")
        dataToPredict['perros'] = dataToPredict['perros'].apply(lambda x: "y" if x == 1 else "n")
        dataToPredict['actividad_y'] = dataToPredict['actividad_y'].apply(lambda x: "y" if x == 1 else "n")
        dataToPredict['rangoEdadPerro']=dataToPredict['dogEdad'].apply(lambda x: "a" if x<5 else ( "b" if (x>=5 and x<10) else "c"))
        dataToPredict['rangoEdadH']=dataToPredict['userEdad'].apply(lambda x: "a" if x<20 else ( "b" if (x>=20 and x<30) else ( "c" if (x>=30 and x<40) else "d")))
        dataToPredict = dataToPredict.drop(['dogEdad', 'userEdad'], axis=1)
        X = self.encoder.transform(dataToPredict)
        return X
    #this function connects to postgres database and loads the data to fit the model
    def loadDataframeFromDB(self):
        conn = psycopg2.connect(
            host="localhost",
            database="doggynator",
            user="postgres",
            password="password")
        cur = conn.cursor()
        cur.execute("SELECT userid, viajes, tipofamilia, actividad, salir, estilo, vivienda, estrato, sueldo, trabajo, edad FROM usuario")
        rowsUsuario = cur.fetchall()
        columnsUsuario = ['userid', 'viajes', 'tipofamilia', 'actividad', 'salir', 'estilo', 'vivienda', 'estrato', 'sueldo', 'trabajo', 'edad']
        usuario = pd.DataFrame(rowsUsuario, columns=columnsUsuario)
        cur.execute("SELECT userid, perroid, megusta FROM megusta")
        rowsMegusta = cur.fetchall()
        columnsMegusta = ['userid', 'perroid', 'megusta']
        megusta = pd.DataFrame(rowsMegusta, columns=columnsMegusta)
        cur.execute("SELECT userid, stage FROM user_stage")
        rowsUserStage = cur.fetchall()
        columnsUserStage = ['userid', 'stage']
        userStage = pd.DataFrame(rowsUserStage, columns=columnsUserStage)
        cur.execute("SELECT userid, stage FROM user_stage")
        rowsUserStage = cur.fetchall()
        columnsUserStage = ['userid', 'stage']
        userStage = pd.DataFrame(rowsUserStage, columns=columnsUserStage)
        cur.execute("SELECT user_id, provider FROM app_user")
        rowsauser = cur.fetchall()
        columnsauser = ['userid', 'provider']
        auser = pd.DataFrame(rowsauser, columns=columnsauser)
        return usuario, megusta, userStage, auser