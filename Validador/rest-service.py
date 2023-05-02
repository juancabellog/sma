from flask import Flask, request, jsonify, session
from validador import valida_archivo;

app = Flask(__name__)

@app.post("/validaArchivo")
def validaArchivo():
    errores = valida_archivo(request.get_json(True))
    return jsonify(errores)
