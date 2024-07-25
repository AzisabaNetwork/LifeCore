package com.github.mori01231.lifecore.util

import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path

object FileSystemUtil {
    @JvmStatic
    fun openFileAsFileSystem(file: File): FileSystem =
        FileSystems.newFileSystem(file.toPath(), null as ClassLoader?)

    @JvmStatic
    fun getPathInFile(file: File, path: String): Path =
        openFileAsFileSystem(file).getPath(path)
}