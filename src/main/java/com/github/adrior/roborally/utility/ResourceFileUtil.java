package com.github.adrior.roborally.utility;

import com.github.adrior.roborally.exceptions.ResourceFileOperationException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for handling InputStream operations.
 */
@UtilityClass
public class ResourceFileUtil {

    /**
     * Gets the absolute path of a file from the given resource path.
     *
     * @param filePath the path to the resource file
     * @return the absolute path of the created file
     * @throws IOException if an I/O error occurs during the operation
     */
    @NonNull
    public static String getAbsolutePathFromResourcePath(@NonNull String filePath) throws IOException {
        InputStream resourceStream = ResourceFileUtil.class.getResourceAsStream(filePath);

        // Ensure the resource path is not null
        if (null == resourceStream) throw new IOException("Error: resource " + filePath + " not found");

        // Create a file object for the resource and return its absolute path
        File file = createTemporaryFileFromStream(resourceStream);
        return file.getAbsolutePath();
    }

    /**
     * Creates a temporary file from the given InputStream.
     * The temporary file will be automatically deleted when the program exits.
     *
     * @param inputStream the input stream to be read from
     * @return the created temporary file
     * @throws IOException if an I/O error occurs during the operation
     */
    @NonNull
    private static File createTemporaryFileFromStream(@NonNull InputStream inputStream) throws IOException {
        File temporaryFile = File.createTempFile("temporary_", ".json");

        temporaryFile.deleteOnExit();   // Delete the temporary file when the program exits.

        try (FileOutputStream fileOutputStream = new FileOutputStream(temporaryFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while (-1 != (bytesRead = inputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException ioException) {
            throw new ResourceFileOperationException("Failed to create temporary file from stream", ioException);
        }

        return temporaryFile;
    }
}