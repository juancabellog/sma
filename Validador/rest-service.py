from flask import Flask, request, jsonify, session
from validador import valida_archivo
from promedios_por_hora import promedios

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
    print(request.args.get('fecha'), request.args.get('hora'))
    promedios([])
    return 'None'
