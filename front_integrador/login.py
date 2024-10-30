from flask import Flask, request, redirect, url_for, render_template
from flask_cors import CORS
import mysql.connector

app = Flask(__name__)
CORS(app)  # Habilita CORS para todas as rotas

# Configuração do banco de dados
def get_db_connection():
    return mysql.connector.connect(
        host='localhost',
        user='root',
        password='aluno',
        database='pousada_gerenciamento',
        auth_plugin='mysql_native_password'  # Adicione essa linha
    )

@app.route('/')
def index():
    return render_template('index.html')


@app.route('/login', methods=['GET'])
def getLogin():
    return render_template('login.html')

@app.route('/login', methods=['POST'])
def postLogin():
    login = request.form.get('login')
    senha = request.form.get('senha')

    # Validar se os dados foram enviados
    if not login or not senha:
        return render_template('login.html', error="Login e senha são necessários!")

    # Conectar ao banco de dados
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)

    # Buscar o usuário pelo login e senha
    cursor.execute("SELECT * FROM user WHERE login = %s AND senha = %s", (login, senha))
    user = cursor.fetchone()

    # Fechar a conexão
    cursor.close()
    conn.close()

    # Verificar se o usuário foi encontrado
    if user:
        return render_template('teste.html')  # Redireciona para a página de sucesso
    else:
        return render_template('login.html', error="Login ou senha incorretos!")  # Retorna erro na mesma página
    
@app.route('/sucesso')
def pagina_sucesso():
        return render_template('login.html')

if __name__ == '__main__':
    app.run(debug=True)
