-- Deleta dados antigos para garantir uma importação limpa
DELETE FROM HistoricoInvestimento;
DELETE FROM ProdutoInvestimento;
DELETE FROM Usuario;

-- Insere usuários com senhas JÁ CRIPTOGRAFADAS (Bcrypt)
-- Senha para user123 é "user123"
INSERT INTO Usuario(id, username, password, role)
VALUES (1, 'user123', '$2a$10$FjsJO5FatNHvrwgA15b4I.vQXJ/oGHMMQDnzWoROAluiAaV8CmcBm', 'user');

-- Senha para admin123 é "admin123"
INSERT INTO Usuario(id, username, password, role)
VALUES (2, 'admin123', '$2a$10$pqwzbu5cQNafRHviOP0Gquf7xwPDZoohrgj7ffpZYZBq4lASW85cy', 'admin');

-- Insere o catálogo de produtos de investimento
INSERT INTO ProdutoInvestimento(id, nome, tipo, rentabilidadeAnual, risco)
VALUES (1, 'Tesouro Selic 2029', 'Renda Fixa', 10.50, 'BAIXO');
INSERT INTO ProdutoInvestimento(id, nome, tipo, rentabilidadeAnual, risco)
VALUES (2, 'CDB Banco X 110% CDI', 'Renda Fixa', 11.25, 'BAIXO');
INSERT INTO ProdutoInvestimento(id, nome, tipo, rentabilidadeAnual, risco)
VALUES (3, 'Fundo Imobiliário HGLG11', 'FII', 12.75, 'MEDIO');
INSERT INTO ProdutoInvestimento(id, nome, tipo, rentabilidadeAnual, risco)
VALUES (4, 'Ações Petrobras (PETR4)', 'Ações', 25.00, 'ALTO');
INSERT INTO ProdutoInvestimento(id, nome, tipo, rentabilidadeAnual, risco)
VALUES (5, 'Fundo Multimercado Y', 'Fundo', 14.50, 'MEDIO');

-- Insere o histórico de investimentos (carteira)
INSERT INTO HistoricoInvestimento(id, clienteId, tipo, valor, rentabilidade, data)
VALUES (1, 123, 'CDB', 5000.00, 1.2, '2024-10-01');
INSERT INTO HistoricoInvestimento(id, clienteId, tipo, valor, rentabilidade, data)
VALUES (2, 123, 'Fundo', 2000.00, 5.5, '2024-10-05');
INSERT INTO HistoricoInvestimento(id, clienteId, tipo, valor, rentabilidade, data)
VALUES (3, 456, 'Ação', 10000.00, -2.1, '2024-11-10');
INSERT INTO HistoricoInvestimento(id, clienteId, tipo, valor, rentabilidade, data)
VALUES (4, 123, 'CDB', 5100.00, 1.3, '2024-11-01');

