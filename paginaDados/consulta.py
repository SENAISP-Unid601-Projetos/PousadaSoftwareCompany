from flask import Flask, jsonify
import pymysql
from flask_cors import CORS


app = Flask(__name__)

# Ativar o CORS em todas as rotas
CORS(app)

# Configurações de conexão com o banco de dados
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': 'Fiat##toro',
    'database': 'pousada_gerenciamento',
    'cursorclass': pymysql.cursors.DictCursor  # Isso retorna os dados como dicionário
}

# Função para conectar ao banco de dados
def get_db_connection():
    connection = pymysql.connect(**db_config)
    return connection

# Endpoint para obter todas as acomodações
@app.route('/acomodacoes', methods=['GET'])
def get_acomodacoes():
    connection = get_db_connection()
    with connection.cursor() as cursor:
        cursor.execute("SELECT * FROM acomodacoes")
        result = cursor.fetchall()
    connection.close()
    return jsonify(result)

# Endpoint para obter todos os usuários
@app.route('/usuarios', methods=['GET'])
def get_usuarios():
    connection = get_db_connection()
    with connection.cursor() as cursor:
        cursor.execute("SELECT * FROM usuarios")
        result = cursor.fetchall()
    connection.close()
    return jsonify(result)

# Endpoint para obter todas as reservas
@app.route('/reservas', methods=['GET'])
def get_reservas():
    connection = get_db_connection()
    with connection.cursor() as cursor:
        query = """
        SELECT id_usuario, id_acomodacao, 
               DATE_FORMAT(data_checkin, '%Y-%m-%d') as data_checkin, 
               DATE_FORMAT(data_checkout, '%Y-%m-%d') as data_checkout, 
               quantidade_pessoas, valor_total 
        FROM reservas
        """
        cursor.execute(query)
        result = cursor.fetchall()
    connection.close()
    return jsonify(result)

# Endpoint para obter uma reserva específica pelo ID
@app.route('/reserva/<int:reserva_id>', methods=['GET'])
def get_reserva(reserva_id):
    connection = get_db_connection()
    with connection.cursor() as cursor:
        query = """
        SELECT id_usuario, id_acomodacao, 
               DATE_FORMAT(data_checkin, '%Y-%m-%d') as data_checkin, 
               DATE_FORMAT(data_checkout, '%Y-%m-%d') as data_checkout, 
               quantidade_pessoas, valor_total 
        FROM reservas WHERE id = %s
        """
        cursor.execute(query, (reserva_id,))
        result = cursor.fetchone()
    connection.close()
    return jsonify(result)

# Endpoint para obter um usuário específico pelo ID
@app.route('/usuario/<int:usuario_id>', methods=['GET'])
def get_usuario(usuario_id):
    connection = get_db_connection()
    with connection.cursor() as cursor:
        cursor.execute("SELECT * FROM usuarios WHERE id = %s", (usuario_id,))
        result = cursor.fetchone()
    connection.close()
    return jsonify(result)

# Inicia o servidor Flask
if __name__ == '__main__':
    app.run(debug=True)
