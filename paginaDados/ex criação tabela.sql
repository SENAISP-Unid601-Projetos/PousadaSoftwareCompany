create table acomodacoes(
id int primary key auto_increment,
 numero_quarto varchar(10),
 nome_quarto varchar(100),
 descricao text,
 valor_diaria decimal(10,2),
 status_disponibilidade enum("disponivel","indisponivel")
);