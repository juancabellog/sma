import configparser
import psycopg2
from pymongo import MongoClient

config = configparser.RawConfigParser()
config.read('app.config')

def getConnect():
    return psycopg2.connect("host=" + config.get('DatabaseSection', 'database.host') 
                            + " dbname=" + config.get('DatabaseSection', 'database.dbname') 
                            + " user=" + config.get('DatabaseSection', 'database.user') 
                            + " password=" + config.get('DatabaseSection', 'database.password') )

def getMongoConnection():
    return MongoClient(config.get('MongoDatabaseSection', 'dlab.pid.mongodb.uri'))