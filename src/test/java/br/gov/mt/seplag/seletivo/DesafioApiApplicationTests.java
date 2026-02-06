package br.gov.mt.seplag.seletivo;


import br.gov.mt.seplag.seletivo.domain.repository.AlbumCapaRepository;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumRepository;
import br.gov.mt.seplag.seletivo.domain.repository.ArtistaRepository;
import br.gov.mt.seplag.seletivo.domain.repository.RegionalRepository;
import br.gov.mt.seplag.seletivo.security.repository.RoleRepository;
import br.gov.mt.seplag.seletivo.security.repository.TokenRepository;
import br.gov.mt.seplag.seletivo.security.repository.UserRepository;
import io.minio.MinioClient;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,"
                + "org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration,"
                + "org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration,"
                + "org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration,"
                + "org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration"
})
class DesafioApiApplicationTests {

    @MockBean
    private AlbumRepository albumRepository;

    @MockBean
    private AlbumCapaRepository albumCapaRepository;

    @MockBean
    private ArtistaRepository artistaRepository;

    @MockBean
    private RegionalRepository regionalRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private MinioClient minioClient;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @Test
    void contextLoads() {
    }
}