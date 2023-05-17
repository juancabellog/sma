
import pandas as pd
import numpy as np


def readDataframe(df):
    """
    Lectura de archivos parquet. Retorna un dataframe pyspark
    """
    df = df.drop(["DeviceId", "Sistema", "Timestamp", "TimestampUTC", "IdVerificacion"], axis = 1)
    
    df = df.rename(columns={'DispositivoId': 'dispositivoId',\
                             'Nombre': 'parametro',\
                             'Valor': 'valor',\
                             'Unidad': 'unidad',\
                             'EstampaTiempo': 'fecha'})
    
    return df


def typeDF(pandasDF, typeData):
    """
    Separa dataframes por tipos de dato crudo, calibrado y validado. Retorna un dataframe
    """
    df = None

    if typeData == "Crudo":
        # IMPORTANTE CORREGIR VALORES EN DB
        df = pandasDF[pandasDF["Crudo"] == "DC "].drop(["Calibraciones", "Validados"], axis = 1)
    elif typeData == "Calibraciones":
        df = pandasDF[pandasDF["Calibraciones"] == "DP"].drop(["Crudo", "Validados"], axis = 1)
    elif typeData == "Validados":
        df = pandasDF[pandasDF["Validados"] == "DV"].drop(["Crudo", "Calibraciones"], axis = 1)
    else:
        print("typeData invalido")

    return df


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


def cleanDF(df):
    """
    Limpieza de NaN y duplicados. Retorna un dataframe
    """
    df = df.dropna()
    df = df.drop_duplicates()

    for cols in ['Crudo', 'Calibraciones', 'Validados']:
        if cols not in df.columns:
            df[cols] = np.nan

    return df


def returnDF(pandasDF, typeData):
    """
    Proceso completo. Devuelve el dataframe "crudo"
    """
    tmpDF      = readDataframe(pandasDF)
    typeFilter = typeDF(tmpDF, typeData)
    cleanedDF  = cleanDF(typeFilter)

    return cleanedDF


# ejemplo
# returnDF(dataframe, "Validados")