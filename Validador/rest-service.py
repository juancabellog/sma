from flask import Flask, request, jsonify
from validador import valida_archivo
from promedios import calculaPromediosPorHora, calculaPromediosActuales


app = Flask(__name__)

@app.post("/validaArchivo")
def validaArchivo():
    errores = valida_archivo(request.get_json(True))
    return jsonify(errores)

#los parámetros de entrada solo para aire: fecha, hora (0-23)
#dataframe, ufid, idproceso, fecha timestamp, parametro, valor
# los datos son: ufid, idProceso, fecha timestamp, parametro, valor
@app.route("/promediosPorHora")
def promediosPorHora():
    return jsonify(calculaPromediosPorHora(request.args.get('fecha'), request.args.get('hora')))

@app.get("/promediosActuales")
def promediosActuales():
    return calculaPromediosActuales()
        
