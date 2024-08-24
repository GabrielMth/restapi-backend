CREATE TABLE pessoa (
                           id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
                           nome VARCHAR(50) NOT NULL,
                           logradouro VARCHAR(50),
                           numero VARCHAR(50),
                           complemento VARCHAR(50),
                           bairro VARCHAR(50),
                           cep VARCHAR(50),
                           cidade VARCHAR(50),
                           estado VARCHAR(50),
                           ativo BOOLEAN NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO pessoa (nome,ativo) values ('Gabriel', TRUE);
INSERT INTO pessoa (nome,ativo) values ('Miguel', TRUE);
INSERT INTO pessoa (nome,ativo) values ('Rafael', TRUE);
INSERT INTO pessoa (nome,ativo) values ('Joel', TRUE);