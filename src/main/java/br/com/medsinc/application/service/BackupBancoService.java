package br.com.medsinc.application.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BackupBancoService {
    private final JdbcTemplate jdbc;
    private final Path diretorio;

    public BackupBancoService(JdbcTemplate jdbc,
            @Value("${medsinc.backup.directory:./backups}") String diretorio) {
        this.jdbc = jdbc;
        this.diretorio = Path.of(diretorio).toAbsolutePath().normalize();
    }

    public Path gerarBackup() {
        try {
            Files.createDirectories(diretorio);
            String nome = "medsinc-backup-" + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".sql";
            Path destino = diretorio.resolve(nome).normalize();
            if (!destino.startsWith(diretorio)) throw new IllegalStateException("Diretório de backup inválido.");
            jdbc.execute((ConnectionCallback<Void>) connection -> {
                try (BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
                    writer.write("-- Backup de dados MEDSiNC. Requer o esquema do projeto; REPLACE atualiza registros com a mesma chave primária. Guarde em local protegido.\n");
                    writer.write("SET FOREIGN_KEY_CHECKS=0;\n");
                    for (String tabela : tabelas(connection)) {
                        writer.write("\n-- Tabela " + tabela + "\n");
                        try (Statement st = connection.createStatement();
                             ResultSet rs = st.executeQuery("SELECT * FROM `" + tabela + "`")) {
                            var meta = rs.getMetaData();
                            int colunas = meta.getColumnCount();
                            while (rs.next()) {
                                List<String> nomes = new ArrayList<>();
                                List<String> valores = new ArrayList<>();
                                for (int i = 1; i <= colunas; i++) {
                                    nomes.add("`" + meta.getColumnName(i).replace("`", "") + "`");
                                    Object valor = rs.getObject(i);
                                    valores.add(literal(valor));
                                }
                                writer.write("REPLACE INTO `" + tabela + "` (" + String.join(", ", nomes)
                                        + ") VALUES (" + String.join(", ", valores) + ");\n");
                            }
                        }
                    }
                    writer.write("\nSET FOREIGN_KEY_CHECKS=1;\n");
                } catch (IOException | SQLException ex) {
                    throw new IllegalStateException("Falha ao gerar arquivo de backup.", ex);
                }
                return null;
            });
            return destino;
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

    private List<String> tabelas(Connection connection) throws SQLException {
        List<String> nomes = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String nome = rs.getString("TABLE_NAME");
                if (!nome.startsWith("flyway_")) nomes.add(nome);
            }
        }
        return nomes;
    }

    private String literal(Object valor) {
        if (valor == null) return "NULL";
        if (valor instanceof Number) return valor.toString();
        if (valor instanceof Boolean b) return b ? "1" : "0";
        String texto = valor.toString().replace("\\", "\\\\")
                .replace("'", "''").replace("\r", "\\r").replace("\n", "\\n");
        return "'" + texto + "'";
    }
}
