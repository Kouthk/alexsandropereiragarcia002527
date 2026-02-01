package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.config.Minio.MinioProperties;
import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import br.gov.mt.seplag.seletivo.exception.StorageException;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
public class MinioStorageService implements LayerDefinition {

    private static final Duration PRESIGNED_TTL = Duration.ofMinutes(30);

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioStorageService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public String uploadAlbumCapa(MultipartFile file) {
        validarArquivoImagem(file);
        String objectKey = gerarObjectKey(file.getOriginalFilename());

        try {
            garantirBucket();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return objectKey;
        } catch (Exception ex) {
            throw new StorageException("Erro ao enviar capa para o MinIO", this);
        }
    }

    public String gerarUrlPresignada(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .method(Method.GET)
                            .expiry((int) PRESIGNED_TTL.toSeconds())
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageException("Erro ao gerar URL de acesso da capa", this);
        }
    }

    private void garantirBucket() {
        try {
            boolean existe = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build()
            );
            if (!existe) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucket())
                                .build()
                );
            }
        } catch (Exception ex) {
            throw new StorageException("Erro ao validar bucket do MinIO", this);
        }
    }

    private void validarArquivoImagem(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Arquivo de capa é obrigatório", this);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new StorageException("Arquivo de capa deve ser uma imagem", this);
        }
    }

    private String gerarObjectKey(String originalFilename) {
        String safeName = Objects.requireNonNullElse(originalFilename, "capa")
                .replaceAll("\\s+", "_");
        return "albuns/" + UUID.randomUUID() + "-" + safeName;
    }

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public LayerEnum getLayer() {
        return LayerEnum.SERVICE;
    }
}