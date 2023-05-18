
import pandas as pd
import numpy as np


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

    return df


def validaLimpia(pandasDF):
    """
    Proceso completo. Devuelve el dataframe "crudo"
    """
    cleanedDF  = cleanDF(pandasDF)

    return cleanedDF


# ejemplo
# returnDF(dataframe, "Validados")