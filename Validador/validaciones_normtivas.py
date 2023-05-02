import json;
from connect_db import getConnect;

def getValidadores(conn, codigoTipoArchivo):
    cur = conn.cursor()
    cur.execute("SELECT vnv_tva_codigo, vnv_datosadicionales from validaciones_normativas where vnv_tar_codigo= %s", [codigoTipoArchivo]);
    records = cur.fetchall()
    cur.close()
    
    validaciones = []
    for r in records:        
        datosAdicionales = r[1]
        if datosAdicionales != None:
            datosAdicionales = json.loads(r[1]);
        validaciones.append({'tipoValidacion': r[0], 'datosAdicionales': datosAdicionales})
        
    return validaciones


def guardarDatos(conn, excelFile):
    print('guarda datos en bd')
    
def getValue(val):
    if val == '':
        return None
    return val

def leeLimites(hojaLimites):
    limites = {}
    for row in hojaLimites:
        if (row['rowStatus'] == 'OK'):
            puntoMonitoreo = None
            try:
                puntMonitoreo = limites[row['PuntoMonitoreo']]
            except (Exception) as error:
                limites[row['PuntoMonitoreo']] = {}                
                puntMonitoreo = limites[row['PuntoMonitoreo']]

            puntMonitoreo[row['Parametro']] = {'uMedida': getValue(row['UnidadMedida']), 'inferior': getValue(row['LimiteInferior']), 'superior': getValue(row['LimiteSuperior'])}
            
    return limites
            
def validaArchivo(excelFile, validacion):
    tipoValidacion = validacion['tipoValidacion']
    
    if (tipoValidacion == 'LIMITES'):
        rowsDatosMonitoreo = excelFile['DatosMonitoreo']
        limites = leeLimites(excelFile['Limites'])
        
        r = 1
        for row in rowsDatosMonitoreo:
            r = r + 1
            puntoMonitoreo = row['PuntoMonitoreo']
            parametro = row['Parametro']
            valor = row['Valor']
            unidadMedida = row['UnidadMedida']
            limite = limites[puntoMonitoreo]
            try:
                parametroLimite = limite[parametro]
                inferior = parametroLimite['inferior']
                superior = parametroLimite['superior']
                uMedida = parametroLimite['uMedida']
                if (uMedida != unidadMedida):
                    print(puntoMonitoreo, parametro, 'Unidad de Medida informada', unidadMedida, 'difiere de la registrada en limite ', uMedida, 'en fila', r)
                if (inferior != None):
                    if (valor < inferior):
                        print(puntoMonitoreo, parametro, 'Alerta valor', valor, 'menor que limite inferior ', inferior, 'en fila', r)
                if (superior != None):
                    if (valor > superior):
                        print(puntoMonitoreo, parametro, 'Alerta valor', valor, 'mayor que limite superior', superior, 'en fila', r)
            except (Exception) as error:
                print('ERROR: Parametro no encontrado en limites:', parametro, 'para Punto monitoreo', puntoMonitoreo)
                
        
    else:
        print('validacion:', tipoValidacion, ' no implementada')
        
def validaciones_normativa(excelFile, codigoTipoArchivo):
    conn = None
    try:
        conn = getConnect() 
        guardarDatos(conn, excelFile)        
        validaciones = getValidadores(conn, codigoTipoArchivo)
        
        for validacion in validaciones:
            validaArchivo(excelFile, validacion)
        
    finally:
        if conn is not None:
            conn.close()    
