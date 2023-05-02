import configparser
import psycopg2

config = configparser.RawConfigParser()
config.read('app.config')

def getConnect():
    return psycopg2.connect("host=" + config.get('DatabaseSection', 'database.host') 
                            + " dbname=" + config.get('DatabaseSection', 'database.dbname') 
                            + " user=" + config.get('DatabaseSection', 'database.user') 
                            + " password=" + config.get('DatabaseSection', 'database.password') )
