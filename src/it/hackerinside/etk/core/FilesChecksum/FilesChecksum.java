package it.hackerinside.etk.core.FilesChecksum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import it.hackerinside.etk.core.Models.FileChecksumDTO;
import it.hackerinside.etk.core.Models.HashAlgorithm;

/**
 * This class provides methods to compute checksums for files and verify their integrity
 * by comparing computed checksums with expected values.
 * 
 * @author Francesco Valentini
 */
public class FilesChecksum {

    private final HashAlgorithm hashAlgorithm;
    private final int bufferSize;

    /**
     * Constructs a FilesChecksum instance with the specified hash algorithm and buffer size.
     *
     * @param hashAlgorithm the hash algorithm to use for checksum computation
     * @param bufferSize the buffer size in bytes for reading files
     */
    public FilesChecksum(HashAlgorithm hashAlgorithm, int bufferSize) {
    	this.bufferSize = bufferSize;
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * Calculates the checksum for the specified file using the configured hash algorithm.
     * The file is read in chunks using the configured buffer size for efficient memory usage.
     *
     * @param file the file for which to calculate the checksum
     * @return a {@link FileChecksumDTO} containing the file path and computed checksum
     * @throws RuntimeException if an I/O error occurs or the hash algorithm is unavailable
     */
    public FileChecksumDTO calculate(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm.toString());

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[this.bufferSize];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            byte[] hash = digest.digest();
            return new FileChecksumDTO(file, hash);

        } catch (IOException | NoSuchAlgorithmException e) {
        	throw new RuntimeException("Error computing checksum", e);
        }
    }
    
    /**
     * Calculates checksums for the given input files/directories and writes them to a checksum file.
     * 
     * 
     * @param inputs List of File objects representing files or directories to process.
     *               Directories are expanded recursively to include all regular files.
     * @param checksumFile The output file where checksums will be written. The parent directory
     *                     of this file is used as the base for calculating relative paths.
     * @throws RuntimeException if an I/O error occurs while writing the checksum file.
     * @throws IllegalArgumentException if any input path does not exist or is an unsupported type.
     */
    public void calculate(List<File> inputs, File checksumFile) {

        List<File> files = inputs.stream()
                .flatMap(f -> expand(f).stream())
                .sorted(Comparator.comparing(File::getPath))
                .toList();

        Path baseDir = checksumFile.toPath()
                .toAbsolutePath()
                .getParent()
                .normalize();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(checksumFile), StandardCharsets.UTF_8))) {

            for (File file : files) {

                FileChecksumDTO dto = calculate(file);

                Path filePath = file.toPath()
                        .toAbsolutePath()
                        .normalize();

                String pathToWrite;

                if (filePath.getRoot().equals(baseDir.getRoot())) {
                    Path relativePath = baseDir.relativize(filePath);
                    pathToWrite = "." + File.separator + relativePath;
                } else {
                    pathToWrite = filePath.toString();
                }


                writer.write(dto.checksum());
                writer.write("  ");
                writer.write(pathToWrite);
                writer.write("\n");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing checksum file", e);
        }
    }

    /**
     * Verifies file checksums against a previously generated checksum file.
     * Supports standard checksum file formats (including binary indicator prefixes like "*").
     * 
     * @param checksumFile The checksum file to read and verify against. The parent directory
     *                     of this file is used as the base for resolving relative file paths.
     * @return A LinkedHashMap mapping each file's checksum data to its verification status (true if valid).
     *         The order of entries matches the order in the checksum file.
     * @throws RuntimeException if the checksum file has an invalid format or an I/O error occurs.
     */
    public HashMap<FileChecksumDTO, Boolean> verify(File checksumFile) {

    	HashMap<FileChecksumDTO, Boolean> result = new LinkedHashMap<>();

        Path checksumDir = checksumFile.toPath()
                .toAbsolutePath()
                .getParent()
                .normalize();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(checksumFile), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) continue;

                String[] parts = line.split("  ", 2);
                if (parts.length != 2) {
                    throw new RuntimeException("Invalid checksum file format: " + line);
                }

                String expectedHash = parts[0];
                String filename = parts[1];

                if (filename.startsWith("*") || filename.startsWith(" ")) {
                    filename = filename.substring(1);
                }

                Path filePath = checksumDir.resolve(filename).normalize();
                File file = filePath.toFile();

                FileChecksumDTO dto = new FileChecksumDTO(file, expectedHash);

                boolean valid = file.exists() && verify(file, expectedHash);

                result.put(dto, valid);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading checksum file", e);
        }

        return result;
    }

    /**
     * Expands a file system path into a list of regular files.
     * 
     * If the input is a regular file, returns a singleton list containing that file.
     * If the input is a directory, recursively walks the directory tree and returns
     * all regular files found. Symbolic links are followed during directory traversal.
     * 
     * @param input The file or directory to expand.
     * @return A list of regular File objects. For a file input: list containing that file.
     *         For a directory: all regular files within it (recursive).
     * @throws IllegalArgumentException if the path does not exist or is not a regular file/directory.
     * @throws RuntimeException if an I/O error occurs during directory traversal.
     */
    private List<File> expand(File input) {
        Path path = input.toPath();

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Path does not exist: " + input);
        }

        try {
            if (Files.isRegularFile(path)) {
                return List.of(input);
            }

            if (Files.isDirectory(path)) {
                try (Stream<Path> stream = Files.walk(path)) {
                    return stream
                            .filter(Files::isRegularFile)
                            .map(Path::toFile)
                            .toList();
                }
            }

            throw new IllegalArgumentException("Unsupported path type: " + input);

        } catch (IOException e) {
            throw new RuntimeException("Error expanding path: " + input, e);
        }
    }

    /**
     * Verifies the integrity of a file by comparing its computed checksum with the checksum
     * stored in a {@link FileChecksumDTO} object.
     *
     * @param file the {@link FileChecksumDTO} containing the expected checksum and file path
     * @return true if the computed checksum matches the expected checksum 
     *         false otherwise
     * @throws RuntimeException if the file cannot be read or checksum computation fails
     */
    public boolean verify(FileChecksumDTO file) {
        File realFile = file.filePath();
        FileChecksumDTO calculated = calculate(realFile);
        return calculated.checksum().equalsIgnoreCase(file.checksum());
    }

    /**
     * Verifies the integrity of a file by comparing its computed checksum with an expected
     * hexadecimal hash string.
     *
     * @param file the file to verify
     * @param hash the expected hexadecimal checksum string
     * @return true if the computed checksum matches the expected hash
     *         false otherwise
     * @throws RuntimeException if the file cannot be read or checksum computation fails
     */
    public boolean verify(File file, String hash) {
        FileChecksumDTO calculated = calculate(file);
        return calculated.checksum().equalsIgnoreCase(hash);
    }
}
