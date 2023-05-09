#Data es un DataFrame con 
def promedios(data):
    print('entra a promnedios:', len(data))
    for row in data:
        print('ufid:', row['UfId'], row['ProcesoId'], row['data'][0]['Parametros'][0]['nombre'])
   