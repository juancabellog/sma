
import pandas as pd
import numpy as np
from connect_db import getConnect



def exploreValues(df, colList, attr=None):
    """
    Exploración de categorías de las columnas a excepción de valor y fecha. Retorna un diccionario.
    """
    res, result = {}, None
    
    if attr is None:
        for column in colList:
            if column in ["valor", "fecha"]: pass
            else:
                res[column] = df[column].unique()
                result   = res
    else:
        try:
            result = df[column].unique()
        except:
            res[attr] = str(attr) + " doesn't exists"
            result    = res

    return result

def getLimites():
    with getConnect() as con:
        cur = con.cursor()
        cur.execute("select lmt_rgd_id, lmt_est_id, lmt_dispositivo, lmt_parametro, lmt_unidad_medida, lmt_minimo, lmt_maximo from limites_aire")
        df = pd.DataFrame(cur.fetchall())
        cur.close()
        df.columns = ['ufId', 'idProceso', 'idDispositivo', 'parametro', 'unidadMedida', 'valorMinimo', 'valorMaximo']
        return df
    
def cleanDF(df):
    """
    Limpieza de NaN y duplicados. Retorna un dataframe
    """

    if df.isna().sum().sum() > 0:   # cantidad de NaN
        df = df.dropna()

    if df.duplicated().sum() > 0:   # cantidad de duplicados
        df = df.drop_duplicates()

    # promedio de valor para distintos dispositivos en la misma fecha
    finalDF    = pd.DataFrame(columns = df.columns)
    lstDFs     = []
    listProcId = df['ProcesoId'].unique()

    for procId in listProcId:
        dfProcId   = df[df['ProcesoId'] == procId]
        listParams = dfProcId['parametro'].unique()

        for param in listParams:
            dfParam = dfProcId[dfProcId['parametro'] == param]
            dateList = list(set(dfParam['fecha'].tolist()))

            # print(dfParam)

            for date in dateList:
                dfDate = dfParam[dfParam['fecha'] == date]
                if dfDate['fecha'].shape[0] > 1:
                    # print(dfDate)
                    dfValid    = dfDate[dfDate['tipoDato'] == 'DV']
                    listDispId = dfValid['dispositivoId'].unique()

                    if dfValid.shape[0] > 1 and len(listDispId) > 1:
                        # print(dfValid)
                        prom    = dfValid['valor'].mean()
                        dfValid = dfValid.iloc[[0]]
                        dfValid['valor'] = prom
                        # print(dfValid)
                        lstDFs.append(dfValid)

                else:
                    lstDFs.append(dfDate)


def validaLimpia(pandasDF):
    """
    Proceso completo. Devuelve el dataframe "crudo"
    """
    cleanedDF  = cleanDF(pandasDF)

    return cleanedDF


# ejemplo
# returnDF(dataframe, "Validados")