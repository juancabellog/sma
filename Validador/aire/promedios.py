from aire.promedios_por_hora import promedios
from datetime import datetime, timedelta
from connect_db import getMongoConnection, getConnect, getProperty
from aire.limpieza_y_validacion_pandas import returnDF
import pandas as pd

def getHour(hora:str):
    if (len(hora) == 1):
        return '0' + hora + ':00:00'
    return hora + ':00:00'

def getPropertyValue(object, property):
    try:
        return object[property]
    except:
        return 'nan'
    
class Item():
    def __init__(self, ufId, procesoId, parametro, fecha):
        self.id= str(ufId) + '_' + str(procesoId) + '_' + parametro + '_' + fecha.strftime('%Y%m%d%H%M%S')
        self.ufId = ufId
        self.procesoId = procesoId
        self.parametro = parametro
        self.fecha = fecha
        
def calculaPromedios(db, fechaInicial, fechaFinal):
    print(datetime.now(), fechaInicial, fechaFinal)
    #rows = []
    cursor = db.CA_ApiRest.find({'$and': [{ 'timestamp': { '$gte': fechaInicial }},{ 'timestamp': { '$lt': fechaFinal } }]} )
    ufIds = []
    ProcesoId = []
    dispositivoId = []
    parametro = []
    valor = []
    unidad = []
    fecha = []
    Crudo = []
    Calibraciones = []
    Validados = []
    objects = {}
    n = 0
    for doc in cursor:
        for data in doc['data']:
            for param in data['Parametros']:
                n = n + 1
                crudo = getPropertyValue(param, 'Crudo')
                validado = getPropertyValue(param, 'Validados')
                if (crudo != 'nan' or validado != 'nan'):
                    
                    obj = Item(doc['UfId'], doc['ProcesoId'], param['nombre'], param['estampaTiempo'])
                    
                    if (validado != 'nan'):
                        try:
                            index = objects[obj.id]
                            Crudo[index] = 'nan'
                            Validados[index] = validado
                            valor[index] = param['valor']
                            continue
                        except:
                            objects[obj.id] = len(ufIds)                    
                    
                    ufIds.append(obj.ufId)
                    ProcesoId.append(obj.procesoId)
                    dispositivoId.append(data['dispositivoId'])
                    parametro.append(param['nombre'])
                    valor.append(param['valor'])
                    unidad.append(param['unidad'])
                    fecha.append(obj.fecha)
                    Crudo.append(crudo)
                    Calibraciones.append(getPropertyValue(param, 'Calibraciones'))
                    Validados.append(validado)
                
    if len(ufIds) > 0:
        dataFrame = pd.DataFrame({'UfId': ufIds, 'ProcesoId': ProcesoId, 'dispositivoId': dispositivoId
                             , 'parametro' : parametro, 'valor': valor, 'unidad' : unidad
                             , 'fecha': fecha, 'Crudo': Crudo
                             , 'Calibraciones': Calibraciones
                             , 'Validados': Validados})
        print('entrada', n, len(dataFrame))
        """ dataFrame = returnDF(dataFrame)"""
        print('limpios', len(dataFrame))
        result = promedios(dataFrame)        
        print(result)
        print('salida',len(result))
        return 'OK'
    return 'NO DATA'

def calculaPromediosPorHora(fecha:str, hora:str):
    print(hora)
    fechaInicial = None
    fechaFinal = None
    if (hora == ''):
        fechaInicial = datetime.strptime(fecha + ' 00:00:00', '%Y-%m-%d %H:%M:%S')    
        fechaFinal = fechaInicial + timedelta(days=1)
    else:
        fechaInicial = datetime.strptime(fecha + ' ' + getHour(hora), '%Y-%m-%d %H:%M:%S')
        fechaFinal = fechaInicial + timedelta(hours=1)
    with getMongoConnection() as mongo:
        db = mongo.ExportData
        return calculaPromedios(db, fechaInicial, fechaFinal)

def calculaUltimosPromedios():
    with getConnect() as conn:
        cur = conn.cursor()
        cur.execute("SELECT max(dpr_fecha) as fecha from datos_promedios")
        fechas = cur.fetchone()
        cur.close()        
        fecha = fechas[0]
        if fecha == None:
            fecha = datetime.strptime(getProperty('MongoDatabaseSection', 'dlab.pid.mongodb.fechaminima') + ' 00:00:00', '%Y-%m-%d %H:%M:%S')
        now = datetime.now()
        print(fecha, now)
        with getMongoConnection() as mongo:
            db = mongo.ExportData
            while fecha < now: 
                fechaInicial = fecha
                fecha = fecha + timedelta(hours=1)
                calculaPromedios(db, fechaInicial, fecha)
    return 'OK'

