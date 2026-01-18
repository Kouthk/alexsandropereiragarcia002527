CREATE TABLE artista (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ano_lancamento INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE artista_album (
    artista_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artista_id, album_id),
    CONSTRAINT fk_artista_album_artista
        FOREIGN KEY (artista_id)
        REFERENCES artista (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_artista_album_album
        FOREIGN KEY (album_id)
        REFERENCES album (id)
        ON DELETE CASCADE
);
