from flask import Flask, request, jsonify, session
from validador import valida_archivo
from promedios_por_hora import promedios
from datetime import datetime, timedelta
from connect_db import getMongoConnection


app = Flask(__name__)

@app.post("/validaArchivo")
def validaArchivo():
    errores = valida_archivo(request.get_json(True))
    return jsonify(errores)

def getHour(hora:str):
    if (len(hora) == 1):
        return '0' + hora + ':00:00'
    return hora + ':00:00'
#los parámetros de entrada solo para aire: fecha, hora (0-23)
#dataframe, ufid, idproceso, fecha timestamp, parametro, valor
# los datos son: ufid, idProceso, fecha timestamp, parametro, valor
@app.route("/promediosPorHora")
def promediosPorHora():
    fechaInicial = datetime.strptime(request.args.get('fecha') + ' ' + getHour(request.args.get('hora')), '%Y-%m-%d %H:%M:%S')
    fechaFinal = fechaInicial + timedelta(hours=1)
    print(fechaInicial, fechaFinal)
    with getMongoConnection() as mongo:
        db = mongo.ExportData
        rows = []
        cursor = db.CA_ApiRest.find({'$and': [{ 'timestamp': { '$gte': fechaInicial }},{ 'timestamp': { '$lt': fechaFinal } }]} )
        for doc in cursor:
            doc['_id'] = 0
            rows.append(doc)
        promedios(rows)
        print(rows)
        return jsonify(rows)
