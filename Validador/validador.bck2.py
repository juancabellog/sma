import json;
import csv;
import geopandas;
from connect_db import getConnect;
from dateutil.parser import parse;
from shapely.geometry import Point;
from io import StringIO;
import pandas as pd


AGNO_MINIMO_VALIDO = 1890;

chile = geopandas.read_file("chile/chl_admbnda_adm1_bcn_20211008.shp")

def getValidadores(conn, codigoTipoArchivo):
    cur = conn.cursor()
    cur.execute("SELECT vta_tva_codigo, vta_datos_adicionales from validaciones_tipo_archivo where vta_tar_codigo= %s", [codigoTipoArchivo]);
    records = cur.fetchall()
    cur.close()
    results = []
    for r in records:
        results.append({'tipoValidacion': r[0], 'datosAdicionales':  json.loads(r[1])})
    return results


def get_csv_file(conn, codigoTipoArchivo, cvs):    
    cur = conn.cursor()
    cur.execute("SELECT tar_delimitador_archivo from tipoarchivo where tar_codigo= %s", [codigoTipoArchivo]);
    record = cur.fetchone()
    cur.close()
    delimitador = record[0]
    csvreader = csv.reader(StringIO(cvs), delimiter=delimitador)
    header = None
    rows = []
    i = 0
    for row in csvreader:
        #print(i, row)
        if i == 0:
            header = row
        else:
            hs = {}
            j = 0
            for c in header:
                hs[c] = row[j]
                j = j + 1
            rows.append(hs)
        i = i + 1
    return rows

def get_excel_file(conn, codigoTipoArchivo, bytesFile): 
    df = pd.read_excel(bytesFile)
    print(df)
    return '0'

def getParametro(conn, paramName):
    cur = conn.cursor()
    cur.execute("SELECT prm_unidad_medida, prm_valor_minimo, prm_valor_maximo from parametros where prm_codigo= %s or prm_descripcion= %s", [paramName, paramName]);
    record = cur.fetchone()
    cur.close();
    if record == None:
        return None;
    return {'UMedida': record[0], 'valorMinimo': record[1], 'valorMaximo': record[2]}
    
def validate(conn, row, rowValues, validador, errors):
    tipoValidacion = validador['tipoValidacion'];
    
    if tipoValidacion == 'TIPO_PARAMETRO':
        valueField = validador['datosAdicionales']['paramValue']
        valor = rowValues[valueField]
        if (valor == ''):
            errors.append({'tipo' : 'WARN', 'fila': row, 'campo': valueField, 'descripcion': 'Valor vacío'});
        else:
            try:
                val = float(valor)
                if (val < 0 ):
                    errors.append({'tipo' : 'WARN', 'fila': row, 'campo': valueField, 'descripcion': 'Valor debe ser mayor a cero'})
                else:
                    UMedida = rowValues[validador['datosAdicionales']['paramUMedida']].strip()
                    if (UMedida == '%'):
                        if (val > 100):
                           errors.append({'tipo' : 'WARN', 'fila': row, 'campo': valueField, 'descripcion': 'Valor debe ser un porcentaje (0-100)'})
                    else:
                        parametro = getParametro(conn, rowValues[validador['datosAdicionales']['paramName']])
                        if (parametro != None):
                            if (parametro['UMedida'] != None and UMedida != parametro['UMedida']):
                                errors.append({'tipo' : 'WARN', 'fila': row, 'campo': validador['datosAdicionales']['paramUMedida'], 'descripcion': 'Unidad de medidad no corresponde ' + UMedida + ' <> ' + parametro['UMedida'] });        
                            elif val < parametro['valorMinimo'] or val > parametro['valorMaximo']:
                                errors.append({'tipo' : 'WARN', 'fila': row, 'campo': validador['datosAdicionales']['paramValue'], 'descripcion': 'Valor ' + valor + ' debe entrar entre ' + str(parametro['valorMinimo']) + ' y ' + str(parametro['valorMaximo'])});        
            except  (Exception) as error:
                print('paramValor no numerico:' + valor, error)
        
    elif tipoValidacion == 'TIPO_COORDENADA':
        longitudField = validador['datosAdicionales']['longitud'];
        latitudField = validador['datosAdicionales']['latitud'];
        longitud = rowValues[longitudField];
        latitud = rowValues[latitudField];
        p = Point(longitud, latitud);
        if chile.geometry.contains(p).sum() < 1:
            errors.append({'tipo' : 'WARN', 'fila': row, 'campo': longitudField + ',' + latitudField, 'descripcion': 'Datos no están dentro chile : ' + str(p)});
    elif tipoValidacion == 'TIPO_FECHA':
        for field in validador['datosAdicionales']:
            val = rowValues[field];
            try:
                if (val != ''):
                    fecha = parse(val);
                    year = fecha.year;
                    if (year < AGNO_MINIMO_VALIDO):
                        errors.append({'tipo' : 'WARN', 'fila': row, 'campo': field, 'descripcion': 'fecha menor a ' + str(AGNO_MINIMO_VALIDO) + ': ' + val});        
            except (Exception) as error:
                errors.append({'tipo' : 'WARN', 'fila': row, 'campo': field, 'descripcion': 'Valor no es fecha: ' + val});
    elif tipoValidacion == 'TIPO_NUMBER':
        for field in validador['datosAdicionales']:
            val = rowValues[field];
            try:
                if (val != ''):
                    float(val)            
            except (Exception) as error:
                errors.append({'tipo' : 'WARN', 'fila': row, 'campo': field, 'descripcion': 'Valor no es numérico: ' + val});
    elif tipoValidacion == 'NOT_NULL':
        for field in validador['datosAdicionales']:
            val = rowValues[field];
            if val == '':
                errors.append({'tipo' : 'WARN', 'fila': row, 'campo': field, 'descripcion': 'Valor vacío'});
    else:
        raise Exception('Tipo validacion no Implementado:' + tipoValidacion);


def valida_archivo(params):
    conn = None
    try:
        conn = getConnect()   
        print('entra a validar')
         
        validadores = getValidadores(conn, params['codigoTipoArchivo'])
        #print(validadores, params['cvsFileName'])
        csv_rows = get_excel_file(conn, params['codigoTipoArchivo'], params['excelFile'])
        #print(validadores)
        #print(cvs_rows)
        r = 1;
        errores = [];
        for row in csv_rows:    
            r = r + 1;
            for validador in validadores:
                validate(conn, r, row, validador, errores)
        return {'resultado': 'OK', 'errores': errores};
    
    except (Exception) as error:
        print('eeror', error)
        return {'resultado': 'NOK', 'descripcion': str(error)};
    finally:
        if conn is not None:
            conn.close()    
