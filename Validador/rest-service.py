from flask import Flask, request, jsonify
from validador import valida_archivo
from aire.promedios import calculaPromediosPorHora, calculaUltimosPromedios
from aire.analitica import generaAnalitica
from aire.analiticaIA import generaAnaliticaIA
from aire.validaciones_normativas import valida_normativas_aire
from connect_db import getConnect
from pandas import DataFrame


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

@app.get("/ultimosPromedios")
def promediosActuales():
    return calculaUltimosPromedios()
        
@app.get("/analitica")
def analitica():
    with getConnect() as conn:
        cur = conn.cursor()
        cur.execute("SELECT dpr_ufid, dpr_idproceso, dpr_fecha, dpr_prm_codigo, dpr_valor from datos_promedios")
        df = DataFrame(cur.fetchall())
        df.columns = ['ufId', 'idProceso', 'fecha', 'parametro', 'valor']
        cur.close()        
        return generaAnalitica(df)

@app.get("/analiticaIA")
def analiticaIA():
    with getConnect() as conn:
        cur = conn.cursor()
        cur.execute("SELECT dpr_ufid, dpr_idproceso, dpr_fecha, dpr_prm_codigo, dpr_valor from datos_promedios")
        df = DataFrame(cur.fetchall())
        cur.close()        
        if (len(df) > 0):
            df.columns = ['ufId', 'idProceso', 'fecha', 'parametro', 'valor']
        return generaAnaliticaIA(df)

@app.get("/validacionesNormativasAire")
def validacionesNormativasAire():
    with getConnect() as conn:
        cur = conn.cursor()
        cur.execute("SELECT dpr_ufid, dpr_idproceso, dpr_fecha, dpr_prm_codigo, dpr_valor from datos_promedios")
        df = DataFrame(cur.fetchall())
        cur.close()        
        if (len(df) > 0):
            df.columns = ['ufId', 'idProceso', 'fecha', 'parametro', 'valor']
        return valida_normativas_aire(df)
