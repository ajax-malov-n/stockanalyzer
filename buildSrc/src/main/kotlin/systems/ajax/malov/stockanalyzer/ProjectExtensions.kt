package systems.ajax.malov.stockanalyzer

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project

internal val Project.libDeps: LibrariesForLibs
    get() = extensions.getByName("libs") as LibrariesForLibs
