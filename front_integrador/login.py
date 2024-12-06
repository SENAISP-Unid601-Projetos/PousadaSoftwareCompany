from flask import Flask, render_template, request, redirect, url_for
from flask_cors import CORS
import pymysql  # Usando o PyMySQL em vez de mysql.connector

app = Flask(__name__)
CORS(app)  # Habilita CORS para todas as rotas

# Configuração do MySQL
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': 'aluno',
    'database': 'pousada_gerenciamento'
}

# Função para obter a conexão com o banco de dados
def get_db_connection():
    return pymysql.connect(**db_config)

# Página inicial (Menu)
@app.route('/')
def menu():
    return render_template('index.html')

# Página do formulário
@app.route('/formulario')
def formulario():
    return render_template('form.html')

# Rota para processar os dados do formulário
@app.route('/enviar', methods=['POST'])
def enviar():
    nome = request.form['nome']
    cpf = request.form['cpf']
    quarto = request.form['quarto']
    telefone = request.form['telefone']
    celular = request.form['celular']
    checkin = request.form['checkin']
    checkout = request.form['checkout']

    conn = None
    cursor = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        query = """
            INSERT INTO reservadapousada (nome, cpf, quarto, telefone, celular, checkin, checkout)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(query, (nome, cpf, quarto, telefone, celular, checkin, checkout))
        conn.commit()
    except pymysql.MySQLError as err:
        print(f"Erro: {err}")
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

    return redirect('/')

# Página para exibir os dados em tabela
@app.route('/dados')
def dados():
    conn = None
    cursor = None
    registros = []
    
    try:
        conn = get_db_connection()
        cursor = conn.cursor(pymysql.cursors.DictCursor)  # Usar DictCursor para pegar resultados como dicionário
        cursor.execute("SELECT * FROM reservadapousada")
        registros = cursor.fetchall()

    except pymysql.MySQLError as err:
        print(f"Erro ao obter dados: {err}")
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

    return render_template('tabela.html', registros=registros)  # Passa os registros diretamente para o template





# Página de login
# Página de login
@app.route('/login', methods=['GET'], endpoint='getLogin')
def getLogin():
    return render_template('login.html')

@app.route('/login', methods=['POST'], endpoint='postLogin')
def postLogin():
    login = request.form.get('login')
    senha = request.form.get('senha')

    if not login or not senha:
        return render_template('login.html', error="Login e senha são necessários!")

    conn = None
    cursor = None
    user = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor(pymysql.cursors.DictCursor)
        cursor.execute("SELECT * FROM user WHERE login = %s AND senha = %s", (login, senha))
        user = cursor.fetchone()
    except pymysql.MySQLError as err:
        print(f"Erro ao buscar usuário: {err}")
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

    if user:
        return redirect(url_for('dados'))  # Redireciona para a página de dados após login bem-sucedido
    else:
        return render_template('login.html', error="Login ou senha incorretos!")



# Página de sucesso (exemplo)
@app.route('/sucesso')
def pagina_sucesso():
    return render_template('login.html')

if __name__ == '__main__':
    app.run(debug=True)
