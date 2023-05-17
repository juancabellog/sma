 
import pandas as pd
import numpy as np


def promedios_minuto(df):
    """
    Calcula el promedio de los valores por minuto de reloj para cada combinación 'UfId' & 'ProcesoId'. 
    Si hay minutos sin mediciones, no habra fila para ese minuto en el DataFrame de salida.
    
    Entrada: df(dataframe): Calibraciones(str), Crudo(str), UfId(int), ProcesoId(int), fecha(datetime), parametro(str), unidad(str), valor(float)
    Salida: result(dataframe): fecha(datetime), UfId(int), ProcesoId(int), parametro(str), valor(float)
    """
    # Truncar la columna 'fecha' al minuto
    df['fecha'] = df['fecha'].dt.floor('1T')

    # Agrupar por 'fecha_min', 'UfId', 'ProcesoId' y calcular la media de la columna 'valor'
    df = (df.groupby(['fecha', 'UfId', 'ProcesoId', 'parametro'], as_index=False)
                   ['valor']
                   .mean())

    return df
        
    
"""
    DECRETO 61  APRUEBA REGLAMENTO DE ESTACIONES DE MEDICIÓN DE CONTAMINANTES ATMOSFÉRICOS
    https://www.bcn.cl/leychile/navegar?i=281728&f=2009-09-21
    
    Artculo 2 , letra ñ):
    
    Promedio horario: Corresponde al que se calcula con los valores medidos entre
    el minuto uno y el minuto 60 de la hora. El promedio horario, para el caso de 
    monitores continuos de gases, debe calcularse sobre el promedio de las mediciones 
    realizadas durante cinco minutos consecutivos, es decir, el promedio horario 
    se obtendrá de promediar aritméticamente 12 valores promedio. En el caso de los 
    medidores continuos de material particulado, el valor promedio horario se calculará 
    sobre el promedio de las mediciones realizadas durante 15 minutos consecutivos, 
    es decir, el promedio horario se obtendrá de promediar aritméticamente 4 valores 
    promedio.
"""
    
#es gas o material particulado
def tipo_medicion(parametro):
    if parametro in ['PM25', 'PM10']:
        return 'PM'
    else:
        return 'gas'
        

def promedios_hora(df):
    """
    Calcula el promedio de los valores por hora de reloj para cada combinación 'UfId' & 'ProcesoId' 
    siguiendo el criterio del Decreto 61 para cada tipo de medición.
    
    Entrada: df(dataframe): Calibraciones(str), Crudo(str), UfId(int), ProcesoId(int), fecha(datetime), parametro(str), unidad(str), valor(float)
    Salida: result(dataframe): hora(datetime), UfId(int), ProcesoId(int), parametro(str), valor(float)
    """
    df = df.copy()
    
    # Primero calculamos los promedios por minuto usando la función promedios_minuto()
    df = promedios_minuto(df)

    # Distinguir entre mediciones de gases y material particulado
    df['tipo'] = df['parametro'].apply(tipo_medicion)

    # Separar el DataFrame en dos DataFrames diferentes para gases y material particulado
    df_gas = df[df['tipo'] == 'gas']
    df_particulado = df[df['tipo'] == 'PM']
    
    #promedio de las mediciones realizadas durante cinco minutos consecutivos
    df_gas['fecha'] = df_gas['fecha'].dt.floor('5T')
    df_gas = (df_gas.groupby(['fecha', 'UfId', 'ProcesoId', 'parametro'], as_index=False)\
                    ['valor']
                    .mean())
                        
    #promedio horario gas
    df_gas['fecha'] = df_gas['fecha'].dt.floor('1H')
    df_gas = (df_gas.groupby(['fecha', 'UfId', 'ProcesoId', 'parametro'], as_index=False)
                    ['valor']
                    .mean())
            
    #promedio de las mediciones realizadas durante 15 minutos consecutivos  
    df_particulado['fecha'] = df_gas['fecha'].dt.floor('15T')
    df_particulado = (df_gas.groupby(['fecha', 'UfId', 'ProcesoId', 'parametro'], as_index=False)
                    ['valor']
                    .mean())
                        
    #promedio horario material particulado
    df_particulado['fecha'] = df_gas['fecha'].dt.floor('1H')
    df_particulado = (df_gas.groupby(['fecha', 'UfId', 'ProcesoId', 'parametro'], as_index=False)
                    ['valor']
                    .mean())
    
    # Combinar los dos DataFrames de resultados en uno solo
    result = pd.concat([df_gas, df_particulado])

    return result



"""
DECRETO 61  APRUEBA REGLAMENTO DE ESTACIONES DE MEDICIÓN DE CONTAMINANTES ATMOSFÉRICOS
    https://www.bcn.cl/leychile/navegar?i=281728&f=2009-09-21
Artculo 2 , letra n):
Promedio diario: Aquel que se calcula con la información medida entre la hora 0 y la hora 23. 
El promedio diario deberá calcularse con al menos 18 valores de promedio. En la medición de 
material particulado con equipos basados en el método gravimétrico de alto y bajo volumen, el
promedio diario se calculará sobre la base de 18 horas continuas de medición. Ello sin 
perjuicio de lo dispuesto en las normas primarias de calidad del aire respectivas.
"""
#ESTA DEFINICION ME RESULTA ALTAMENTE AMBIGUA. POR AHORA IMPLEMENTO UN PROMEDIO DIARIO DE FORMA TRIVIAL
def promedios_dia(df):
    """
    Entrada: df(dataframe): hora(datetime), UfId(int), ProcesoId(int), parametro(str), valor(float)
    Salida: result(dataframe): dia(datetime), UfId(int), ProcesoId(int), parametro(str), valor(float)
    """
    
    # Truncar la columna 'fecha' al dia
    df['dia'] = df['hora'].dt.floor('1D')

    # Agrupar por 'dia', 'UfId', 'ProcesoId' y calcular la media de la columna 'valor'
    result = df\
        .groupby(['dia', 'UfId', 'ProcesoId', 'parametro'], as_index=False)\
        ['valor']\
        .mean()

    return result






    


def promedios(df):
    print('entra a promnedios:', len(data))
    for row in data:
        print('ufid:', row['UfId'], row['ProcesoId'], row['data'][0]['Parametros'][0]['nombre'])
        
        
        
