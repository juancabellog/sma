import psycopg2
import configparser
import sys
import csv
from Crypto.Cipher import PKCS1_OAEP
import base64
import geopandas
from shapely.geometry import Point, Polygon, LineString


config = configparser.RawConfigParser()
config.read('app.config')


def getConnect():
    return psycopg2.connect("host=" + config.get('DatabaseSection', 'database.host') 
                            + " dbname=" + config.get('DatabaseSection', 'database.dbname') 
                            + " user=" + config.get('DatabaseSection', 'database.user') 
                            + " password=" + config.get('DatabaseSection', 'database.password') )


def decrypt_public_key(encoded_encrypted_msg, public_key):
    f = open('/Users/manolocabello/servers/apache-tomcat-9.0.63/bin/pkey_rsa', 'r')
    data = f.read()
    print(data)
    encryptor = PKCS1_OAEP.new(data)
    print('aaa2', encoded_encrypted_msg)
    #decoded_encrypted_msg = base64.b64decode(encoded_encrypted_msg)
    print('aaa3', encoded_encrypted_msg)
    decoded_decrypted_msg = encryptor.decrypt(base64.b64decode(encoded_encrypted_msg))
    print('aaa4')
    return decoded_decrypted_msg

def valida_usuario(data):
    print('quii', data['id'])
    conn = None
    try:
        conn = getConnect()
        cur = conn.cursor()
        cur.execute("SELECT usr_id, usr_password, usr_nombre, usr_rol_id FROM usuario where usr_id = %s", [data['id']])
        record = cur.fetchone()
        print('aaa1')
        pwd = decrypt_public_key(record[1], '')
        print(record[0], pwd)
        return True
    except(Exception, psycopg2.DatabaseError) as error:
        print(error)
        return False
    finally:
        if conn is not None:
            conn.close()    
            
def get_parameters():
    conn = None
    try:
        conn = getConnect()

        # Open a cursor to perform database operations
        cur = conn.cursor()

        # Execute a query
        cur.execute("SELECT a FROM test")

        # Retrieve query results
        records = cur.fetchall()
        cur.close()
        results = []
        for r in records:
            results.append({'col': r[0], 'validadores':  ['COD1', 'COD2']})
        
        return results
    
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()    


def get_csv_file(file_type, cvs_file_name):    

    print(file_type, cvs_file_name)
    
    with open(cvs_file_name, 'r') as file:
        csvreader = csv.reader(file, delimiter='|')
        header = None
        rows = []
        i = 0
        for row in csvreader:
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

def validate(row, col, value, type):
    if type == 'COD1':
        return {'type' : 'ERROR', 'descripcion': 'Campo muy largo', 'Row' : row, 'Col': col, 'Value': value, 'Validador': type}
    elif type == 'COD2':
        return {'type' : 'WARN', 'descripcion': 'Campo muy largo', 'Row' : row, 'Col' : col, 'Value': value, 'Validador': type}
    else:
        return {'type' : 'OK', 'descripcion': '', 'Row' : row, 'Col' : col, 'Value': value, 'Validador': type}
    
    
def valida_csv(file_type, cvs_file_name):
    validadores = get_parameters()
    cvs_rows = get_csv_file(file_type, cvs_file_name)

    i = 0
    for r in cvs_rows:    
        i = i + 1
        for col in validadores:
            for val in col['validadores']:
                print(validate(i, col['col'], r[col['col']], val))




