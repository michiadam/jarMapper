package at.michaeladam;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectMapper
{


    public static void main(String[] args) {

        JarExtractor.extractJar("C:\\Users\\michi\\IdeaProjects\\polarbackend\\target\\polar-0.0.1.jar", "C:\\ws\\projectmapper\\target");
    }

}
