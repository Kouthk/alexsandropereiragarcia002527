package br.gov.mt.seplag.seletivo.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "album")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "ano_lancamento")
    private Integer anoLancamento;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "albuns")
    private Set<Artista> artistas;

    @OneToMany(
            mappedBy = "album",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<AlbumCapa> capas;


}
