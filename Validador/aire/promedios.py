from aire.promedios_por_hora import promedios
from datetime import datetime, timedelta
from connect_db import getMongoConnection, getConnect, getProperty
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
    
def calculaPromedios(db, fechaInicial, fechaFinal):
    print(fechaInicial, fechaFinal)
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
    for doc in cursor:
        for data in doc['data']:
            for param in data['Parametros']:
                ufIds.append(doc['UfId'])
                ProcesoId.append(doc['ProcesoId'])
                dispositivoId.append(data['dispositivoId'])
                parametro.append(param['nombre'])
                valor.append(param['valor'])
                unidad.append(param['unidad'])
                fecha.append(param['estampaTiempo'])
                Crudo.append(getPropertyValue(param, 'Crudo'))
                Calibraciones.append(getPropertyValue(param, 'Calibraciones'))
                Validados.append(getPropertyValue(param, 'Validados'))
                
    if len(ufIds) > 0:
        dataFrame = pd.DataFrame({'UfId': ufIds, 'ProcesoId': ProcesoId, 'dispositivoId': dispositivoId
                             , 'parametro' : parametro, 'valor': valor, 'unidad' : unidad
                             , 'fecha': fecha, 'Crudo': Crudo
                             , 'Calibraciones': Calibraciones
                             , 'Validados': Validados})
        promedios(dataFrame)        
        return 'OK'
    return 'NO DATA'

def calculaPromediosPorHora(fecha:str, hora:str):
    fechaInicial = datetime.strptime(fecha + ' ' + getHour(hora), '%Y-%m-%d %H:%M:%S')
    fechaFinal = fechaInicial + timedelta(hours=1)
    with getMongoConnection() as mongo:
        db = mongo.ExportData
        return calculaPromedios(db, fechaInicial, fechaFinal)

def calculaPromediosActuales():
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

