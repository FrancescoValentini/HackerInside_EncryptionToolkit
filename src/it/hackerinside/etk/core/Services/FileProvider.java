package it.hackerinside.etk.core.Services;

import java.io.File;

@FunctionalInterface
public interface FileProvider {
    File getFile();
}