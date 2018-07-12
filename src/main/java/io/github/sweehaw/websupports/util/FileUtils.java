package io.github.sweehaw.websupports.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @author sweehaw
 */
public class FileUtils {

    public static Resource downloadFile(ServletContext context, HttpServletResponse response, String fileName) {

        File file = new File(fileName);

        response.setContentType(context.getMimeType(file.getAbsolutePath()));
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }
}
