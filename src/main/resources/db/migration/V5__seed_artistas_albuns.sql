-- Seed inicial: artistas, álbuns e relacionamento N:N (inclui 1 álbum com vários artistas)

INSERT INTO artista (id, nome, tipo, ativo) VALUES
    (1, 'Aurora Aksnes', 'SOLO', true),
    (2, 'Seu Pereira e Coletivo 401', 'BANDA', true),
    (3, 'CPM 22', 'BANDA', true),
    (4, 'Metallica', 'BANDA', true),
    (5, 'Don Broco', 'BANDA', true),
    (6, 'Of Monsters and Men', 'BANDA', true),
    (7, 'Two Door Cinema Club', 'BANDA', true);

INSERT INTO album (id, titulo, ano_lancamento, ativo) VALUES
    (1, 'Northern Lights', 2016, true),
    (2, 'Echoes of the Sky', 2018, true),
    (3, 'Eu Não Sou Boa Influência Pra Você', 2017, true),
    (4, 'Chegou a Hora de Recomeçar', 2002, true),
    (5, 'Cidade Cinza', 2007, true),
    (6, 'Master of Puppets', 1986, true),
    (7, 'Metallica (Black Album)', 1991, true),
    (8, 'Automatic', 2015, true),
    (9, 'My Head Is an Animal', 2011, true),
    (10, 'City Lights', 2010, true),

    -- Álbum com vários artistas (feat/compilado) para validar N:N e consultas parametrizadas
    (11, 'Conexões Sonoras', 2024, true);

-- Relacionamento artista <-> album
INSERT INTO artista_album (artista_id, album_id) VALUES
    -- Aurora (solo)
    (1, 1),
    (1, 2),

    -- Seu Pereira e Coletivo 401
    (2, 3),

    -- CPM 22
    (3, 4),
    (3, 5),

    -- Metallica
    (4, 6),
    (4, 7),

    -- Don Broco
    (5, 8),

    -- Of Monsters and Men
    (6, 9),

    -- Two Door Cinema Club
    (7, 10),

    -- Álbum compartilhado por vários artistas diferentes
    (1, 11),
    (2, 11),
    (3, 11),
    (6, 11),
    (7, 11);

-- Ajuste das sequências para evitar conflito quando inserir sem ID depois
SELECT setval('artista_id_seq', (SELECT MAX(id) FROM artista));
SELECT setval('album_id_seq', (SELECT MAX(id) FROM album));
