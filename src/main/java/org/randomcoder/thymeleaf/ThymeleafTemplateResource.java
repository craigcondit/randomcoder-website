package org.randomcoder.thymeleaf;

import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ThymeleafTemplateResource implements ITemplateResource {

    private final String path;
    private final String characterEncoding;

    public ThymeleafTemplateResource(String path, String characterEncoding) {
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        this.path = path;
        this.characterEncoding = characterEncoding;
    }

    static String computeBaseName(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        String basePath = (path.charAt(path.length() - 1) == '/' ? path.substring(0, path.length() - 1) : path);
        int slashPos = basePath.lastIndexOf('/');
        if (slashPos != -1) {
            final int dotPos = basePath.lastIndexOf('.');
            if (dotPos != -1 && dotPos > slashPos + 1) {
                return basePath.substring(slashPos + 1, dotPos);
            }
            return basePath.substring(slashPos + 1);
        } else {
            final int dotPos = basePath.lastIndexOf('.');
            if (dotPos != -1) {
                return basePath.substring(0, dotPos);
            }
        }
        return (basePath.length() > 0 ? basePath : null);
    }

    static String computeRelativeLocation(String location, String relativeLocation) {
        int separatorPos = location.lastIndexOf('/');
        if (separatorPos != -1) {
            StringBuilder relativeBuilder = new StringBuilder(location.length() + relativeLocation.length());
            relativeBuilder.append(location, 0, separatorPos);
            if (relativeLocation.charAt(0) != '/') {
                relativeBuilder.append('/');
            }
            relativeBuilder.append(relativeLocation);
            return relativeBuilder.toString();
        }
        return relativeLocation;
    }

    @Override
    public String getDescription() {
        return this.path;
    }

    @Override
    public String getBaseName() {
        return computeBaseName(this.path);
    }

    @Override
    public boolean exists() {
        return ClassLoaderUtils.isResourcePresent(this.path);
    }

    @Override
    public Reader reader() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(this.path);
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("ClassLoader resource \"%s\" could not be resolved", this.path));
        }
        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
    }

    @Override
    public ITemplateResource relative(String relativeLocation) {
        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");
        final String fullRelativeLocation = computeRelativeLocation(this.path, relativeLocation);
        return new ThymeleafTemplateResource(fullRelativeLocation, this.characterEncoding);
    }

}
