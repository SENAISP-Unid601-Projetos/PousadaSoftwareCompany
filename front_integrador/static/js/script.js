// Abrir o popup
document.getElementById('openPopup').addEventListener('click', function() {
    document.getElementById('popupForm').classList.add('active');
    document.getElementById('overlay').classList.add('active');
});

// Fechar o popup ao clicar fora dele
document.getElementById('overlay').addEventListener('click', function() {
    document.getElementById('popupForm').classList.remove('active');
    document.getElementById('overlay').classList.remove('active');
});

// Função para enviar dados para o backend (simulação)
document.getElementById('enviarReserva').addEventListener('click', function() {
    const nome = document.getElementById('nome').value;
    const telefone = document.getElementById('telefone').value;
    const cpf = document.getElementById('cpf').value;
    const pagamento = document.getElementById('pagamento').value;
    const checkin = document.getElementById('checkin').value;
    const checkout = document.getElementById('checkout').value;
    const quarto = document.getElementById('quarto').value;

    // Criação de um objeto reserva
    const reserva = {
        nome,
        telefone,
        cpf,
        pagamento,
        checkin,
        checkout,
        quarto
    };

    // Enviar reserva para a página da tabela
    saveReservation(reserva);
});

// Salvar reserva em localStorage e redirecionar
function saveReservation(reserva) {
    let reservas = JSON.parse(localStorage.getItem('reservas')) || [];
    reservas.push(reserva);
    localStorage.setItem('reservas', JSON.stringify(reservas));
    alert('Reserva realizada com sucesso!');
    document.getElementById('popupForm').classList.remove('active');
    document.getElementById('overlay').classList.remove('active');
}
