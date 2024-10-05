// Carregar as reservas do banco de dados
async function fetchReservas() {
    try {
        const response = await fetch('/api/reservas'); // Chama a API Node-RED
        const reservas = await response.json();
        
        const reservasList = document.getElementById('reservas-list');
        reservasList.innerHTML = '';  // Limpa a tabela antes de popular novamente

        reservas.forEach(reserva => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${reserva.nome}</td>
                <td>${reserva.nome_quarto}</td>
                <td>${reserva.data_checkin}</td>
                <td>${reserva.data_checkout}</td>
                <td>${reserva.quantidade_pessoas}</td>
                <td>R$ ${reserva.valor_total}</td>
            `;
            reservasList.appendChild(row);
        });
    } catch (error) {
        console.error('Erro ao carregar reservas:', error);
    }
}

// Salvar uma nova reserva
async function salvarReserva(event) {
    event.preventDefault();

    const id_usuario = document.getElementById('id_usuario').value;
    const id_acomodacao = document.getElementById('id_acomodacao').value;
    const data_checkin = document.getElementById('data_checkin').value;
    const data_checkout = document.getElementById('data_checkout').value;
    const quantidade_pessoas = document.getElementById('quantidade_pessoas').value;
    const valor_total = document.getElementById('valor_total').value;

    const reserva = {
        id_usuario: id_usuario,
        id_acomodacao: id_acomodacao,
        data_checkin: data_checkin,
        data_checkout: data_checkout,
        quantidade_pessoas: quantidade_pessoas,
        valor_total: valor_total
    };

    try {
        const response = await fetch('/api/reservas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reserva)
        });

        if (response.ok) {
            alert('Reserva salva com sucesso!');
            fetchReservas();  // Atualiza a lista de reservas
        } else {
            alert('Erro ao salvar a reserva.');
        }
    } catch (error) {
        console.error('Erro ao salvar reserva:', error);
    }
}

// Adiciona o event listener ao formulário
document.getElementById('reserva-form').addEventListener('submit', salvarReserva);

// Carregar as reservas quando a página for carregada
window.onload = fetchReservas;
