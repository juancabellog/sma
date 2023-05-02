import json;
import geopandas;
import threading;
from connect_db import getConnect;
from validaciones_normtivas import validaciones_normativa;
from dateutil.parser import parse;
from shapely.geometry import Point;


AGNO_MINIMO_VALIDO = 1890;

chile = geopandas.read_file("chile/chl_admbnda_adm1_bcn_20211008.shp")

def getValidadores(conn, codigoTipoArchivo):
    cur = conn.cursor()
    cur.execute("SELECT hdd_nombre from hoja_de_datos where hdd_tar_codigo= %s", [codigoTipoArchivo]);
    rHojas = cur.fetchall()
    cur.close()
    hojas = []
    for h in rHojas:
        nombre = h[0]
        cur = conn.cursor()
        cur.execute("SELECT vta_tva_codigo, vta_datos_adicionales, ter_descripcion, vta_ter_codigo from validaciones_tipo_archivo, tipo_error where vta_ter_codigo = ter_codigo and vta_hdd_tar_codigo= %s and vta_hdd_nombre= %s", [codigoTipoArchivo, nombre]);
        records = cur.fetchall()
        cur.close()
        
        validaciones = []
        for r in records:
            validaciones.append({'tipoValidacion': r[0], 'codigoTipoError': r[3], 'tipoError': r[2], 'datosAdicionales':  json.loads(r[1])})
        
        hojas.append({'nombre': nombre, 'validaciones': validaciones})
    return hojas

def getParametro(conn, paramName):
    cur = conn.cursor()
    cur.execute("SELECT prm_unidad_medida, prm_valor_minimo, prm_valor_maximo from parametros where prm_codigo= %s or prm_descripcion= %s", [paramName, paramName]);
    record = cur.fetchone()
    cur.close();
    if record == None:
        return None;
    return {'UMedida': record[0], 'valorMinimo': record[1], 'valorMaximo': record[2]}
    
def validate(conn, nombreHoja, row, rowValues, validador, errors):
    tipoValidacion = validador['tipoValidacion'];
    tipoError = validador['tipoError']
    if tipoValidacion == 'TIPO_PARAMETRO':
        valueField = validador['datosAdicionales']['paramValue']
        paramName = rowValues[validador['datosAdicionales']['paramName']]
        valor = rowValues[valueField]
        if (valor == ''):
            errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'parametro': paramName, 'fila': row, 'campo': valueField, 'descripcion': 'Valor vacío'});
        else:
            try:
                val = float(valor)
                if (val < 0 ):
                    errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'parametro': paramName, 'fila': row, 'campo': valueField, 'descripcion': 'Valor debe ser mayor a cero'})
                else:
                    UMedida = rowValues[validador['datosAdicionales']['paramUMedida']].strip()
                    if (UMedida == '%'):
                        if (val > 100):
                           errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'parametro': paramName, 'fila': row, 'campo': valueField, 'descripcion': 'Valor debe ser un porcentaje (0-100)'})
                    else:
                        parametro = getParametro(conn, paramName)
                        if (parametro != None):
                            if (parametro['UMedida'] != None and UMedida != parametro['UMedida']):
                                errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'parametro': paramName, 'fila': row, 'campo': validador['datosAdicionales']['paramUMedida'], 'descripcion': 'Unidad de medidad no corresponde ' + UMedida + ' <> ' + parametro['UMedida'] });        
                            elif val < parametro['valorMinimo'] or val > parametro['valorMaximo']:
                                errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'parametro': paramName, 'fila': row, 'campo': valueField, 'descripcion': 'Valor ' + valor + ' debe entrar entre ' + str(parametro['valorMinimo']) + ' y ' + str(parametro['valorMaximo'])});        
            except  (Exception) as error:
                print('paramValor no numerico:' + valor, error)
        
    elif tipoValidacion == 'TIPO_COORDENADA':
        longitudField = validador['datosAdicionales']['longitud'];
        latitudField = validador['datosAdicionales']['latitud'];
        longitud = rowValues[longitudField];
        latitud = rowValues[latitudField];
        p = Point(longitud, latitud);
        if chile.geometry.contains(p).sum() < 1:
            errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'fila': row, 'campo': longitudField + ',' + latitudField, 'descripcion': 'Datos no están dentro chile : ' + str(p)});
    elif tipoValidacion == 'TIPO_FECHA':
        for field in validador['datosAdicionales']:
            val = rowValues[field];
            try:
                if (val != ''):
                    fecha = parse(val);
                    year = fecha.year;
                    if (year < AGNO_MINIMO_VALIDO):
                        errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'fila': row, 'campo': field, 'descripcion': 'fecha menor a ' + str(AGNO_MINIMO_VALIDO) + ': ' + val});        
            except (Exception) as error:
                errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'fila': row, 'campo': field, 'descripcion': 'Valor no es fecha: ' + val});
    elif tipoValidacion == 'TIPO_NUMBER':
        for field in validador['datosAdicionales']:
            val = rowValues[field];
            try:
                if (val != ''):
                    float(val)            
            except (Exception) as error:
                errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'fila': row, 'campo': field, 'descripcion': 'Valor no es numérico: ' + val});
    elif tipoValidacion == 'NOT_NULL':
        for field in validador['datosAdicionales']:
            val = rowValues[field];
            if val == '':
                errors.append({'tipo' : tipoError, 'hoja': nombreHoja, 'fila': row, 'campo': field, 'descripcion': 'Valor vacío'});
    else:
        raise Exception('Tipo validacion no Implementado:' + tipoValidacion);


def valida_archivo(params):
    conn = None
    codigoTipoArchivo = params['codigoTipoArchivo']
    try:
        conn = getConnect()            
        excelFile = params['excelFile'] 
        hojas_de_validaciones = getValidadores(conn, codigoTipoArchivo)
        errores = []
        rechazaArchivo = False
        for hoja in hojas_de_validaciones: 
            nombre = hoja['nombre']
            validaciones = hoja['validaciones']
            rows = excelFile[nombre]
            if (rows == None):
                raise Exception('Hoja no encontrada en archivo:' + nombre)
            r = 1
            for row in rows:    
                r = r + 1;
                for validador in validaciones:
                    erroresAnteriores = len(errores)
                    tipoError = validador['codigoTipoError']
                    validate(conn, nombre, r, row, validador, errores)
                    if erroresAnteriores < len(errores):
                        row['rowStatus'] = tipoError
                        if tipoError == 'ERRORA':
                            rechazaArchivo = True
                    else:
                        row['rowStatus'] = 'OK'
        if (not rechazaArchivo):
            thread = threading.Thread(target=validaciones_normativa, args=(excelFile, codigoTipoArchivo))
            thread.start()
            
        return {'resultado': 'OK', 'errores': errores, 'rechazaArchivo': rechazaArchivo};
    
    except (Exception) as error:
        print('error', error)
        return {'resultado': 'NOK', 'descripcion': str(error)};
    finally:
        if conn is not None:
            conn.close()    
