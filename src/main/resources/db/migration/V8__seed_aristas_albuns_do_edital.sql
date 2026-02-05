-- Seed V8: artistas e álbuns conforme exemplos do edital (ANEXO II)

-- ARTISTAS
INSERT INTO artista (id, nome, tipo, ativo) VALUES
    (12, 'Serj Tankian', 'SOLO', true),
    (13, 'Mike Shinoda', 'SOLO', true),
    (14, 'Michel Teló', 'SOLO', true),
    (15, 'Guns N'' Roses', 'BANDA', true);

-- ÁLBUNS
INSERT INTO album (id, titulo, ano_lancamento, ativo) VALUES
    -- Serj Tankian
    (12, 'Harakiri', 2012, true),
    (13, 'Black Blooms', 2011, true),
    (14, 'The Rough Dog', 2010, true),

    -- Mike Shinoda
    (15, 'The Rising Tied', 2005, true),
    (16, 'Post Traumatic', 2018, true),
    (17, 'Post Traumatic EP', 2018, true),
    (18, 'Where’d You Go', 2006, true),

    -- Michel Teló
    (19, 'Bem Sertanejo', 2014, true),
    (20, 'Bem Sertanejo - O Show (Ao Vivo)', 2016, true),
    (21, 'Bem Sertanejo - (1ª Temporada) - EP', 2015, true),

    -- Guns N' Roses
    (22, 'Use Your Illusion I', 1991, true),
    (23, 'Use Your Illusion II', 1991, true),
    (24, 'Greatest Hits', 2004, true);

-- RELACIONAMENTO N:N
INSERT INTO artista_album (artista_id, album_id) VALUES
    -- Serj Tankian
    (12, 12),
    (12, 13),
    (12, 14),

    -- Mike Shinoda
    (13, 15),
    (13, 16),
    (13, 17),
    (13, 18),

    -- Michel Teló
    (14, 19),
    (14, 20),
    (14, 21),

    -- Guns N' Roses
    (15, 22),
    (15, 23),
    (15, 24);

-- AJUSTE DE SEQUÊNCIAS (PostgreSQL)
SELECT setval(pg_get_serial_sequence('artista','id'), (SELECT MAX(id) FROM artista));
SELECT setval(pg_get_serial_sequence('album','id'), (SELECT MAX(id) FROM album));
