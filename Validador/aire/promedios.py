from aire.promedios_por_hora import promedios
from datetime import datetime, timedelta
from connect_db import getMongoConnection, getConnect, getProperty
from aire.limpieza_y_validacion_pandas import validaLimpia
import pandas as pd

def getHour(hora:str):
    if (len(hora) == 1):
        return '0' + hora + ':00:00'
    return hora + ':00:00'

def getPropertyValue(object, property):
    try:
        return object[property].strip()
    except:
        return 'nan'

class Item():
    def __init__(self, ufId, procesoId, parametro, fecha):
        self.id= str(ufId) + '_' + str(procesoId) + '_' + parametro + '_' + fecha.strftime('%Y%m%d%H%M%S')
        self.ufId = ufId
        self.procesoId = procesoId
        self.parametro = parametro
        self.fecha = fecha

def guadarPromedios(fechaInicial, fechaFinal, data):
    with getConnect() as con:
        cur = con.cursor()
        cur.execute("delete from datos_promedios where dpr_fecha >= %s and dpr_fecha < %s", [fechaInicial.strftime('%Y-%m-%d %H:%M:%S'), fechaFinal.strftime('%Y-%m-%d %H:%M:%S')])
        cur.close()

        cur = con.cursor()
        print('nro de registros:', len(data))
        n = 0
        for index, row in data.iterrows():
            n += 1
            print(n, 'registro', row.UfId, row.ProcesoId, row.fecha.strftime('%Y-%m-%d %H:%M:%S'), row.parametro, row.valor)
        
        n = 0
        for index, row in data.iterrows():
            n += 1
            try:
                cur.execute("insert into datos_promedios (dpr_bdt_codigo, dpr_ufid, dpr_idproceso, dpr_fecha, dpr_prm_codigo, dpr_valor) values (%s, %s, %s, %s, %s, %s)", ['AIRE', row.UfId, row.ProcesoId, row.fecha.strftime('%Y-%m-%d %H:%M:%S'), row.parametro, row.valor])
            except (Exception) as error:
                print(n, 'ERROR en ', row.UfId, row.ProcesoId, row.fecha.strftime('%Y-%m-%d %H:%M:%S'), row.parametro, row.valor)
                raise error
        cur.close()

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
    tiposDatos = []
    objects = {}
    n = 0
    for doc in cursor:
        for data in doc['data']:
            for param in data['Parametros']:
                n = n + 1
                crudo = getPropertyValue(param, 'Crudo')
                validado = getPropertyValue(param, 'Validados')
                if (crudo == 'DC' or validado == 'DV'):

                    obj = Item(doc['UfId'], doc['ProcesoId'], param['nombre'], param['estampaTiempo'])
                    tipoDato = None
                    if (validado != 'nan'):
                        tipoDato = validado
                        try:
                            index = objects[obj.id]
                            tiposDatos[index] = tipoDato
                            valor[index] = param['valor']
                            continue
                        except:
                            objects[obj.id] = len(ufIds)

                    else: #datos crudo
                        tipoDato = crudo
                        try:
                            index = objects[obj.id]
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
                    tiposDatos.append(tipoDato)

    if len(ufIds) > 0:
        dataFrame = pd.DataFrame({'UfId': ufIds, 'ProcesoId': ProcesoId, 'dispositivoId': dispositivoId
                             , 'parametro' : parametro, 'valor': valor, 'unidad' : unidad
                             , 'fecha': fecha, 'tipoDato': tiposDatos})
        
        print('entrada', n, len(dataFrame))
        result = validaLimpia(dataFrame)
        print('limpios', len(result))
        result = promedios(result)
        print('salida',len(result))
        guadarPromedios(fechaInicial, fechaFinal, result)
        print('guarda ok')
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
                    