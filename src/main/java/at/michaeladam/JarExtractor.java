package at.michaeladam;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;

public class JarExtractor {
    private static Map<String, ClassNode> loadClasses(File jarFile) throws IOException {
        Map<String, ClassNode> classes = new HashMap<>();
        JarFile jar = new JarFile(jarFile);
        Stream<JarEntry> str = jar.stream();
        str.forEach(z -> readJar(jar, z, classes));
        jar.close();
        return classes;
    }

    private static Map<String, ClassNode> readJar(JarFile jar, JarEntry entry, Map<String, ClassNode> classes) {
        String name = entry.getName();
        try (InputStream inputStream = jar.getInputStream(entry)) {
            if (name.endsWith(".class")) {
                byte[] bytes = IOUtils.toByteArray(inputStream);
                String cafebabe = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
                if (!cafebabe.toLowerCase().equals("cafebabe")) {
                    // This class doesn't have a valid magic
                    return classes;
                }
                ClassNode cn = getNode(bytes);
                classes.put(cn.name, cn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static ClassNode getNode(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        try {
            cr.accept(cn, ClassReader.EXPAND_FRAMES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cr = null;
        return cn;
    }

    //Function to extract the jar file and get the classes
    public static void extractJar(String jarName, String destDir) {
        try {

            File jarFile = new File(jarName);
            Map<String, ClassNode> classes = loadClasses(jarFile);
            //build a json object of the loaded classes and group them by packages
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Object>> json = new HashMap<>();
            classes.forEach((k, v) -> {
                String packageName = v.name.substring(0, v.name.lastIndexOf("/")).replace("/", ".");
                if (!json.containsKey(packageName)) {
                    json.put(packageName, new HashMap<>());
                }
                Map<String, Object> map = json.get(packageName);

                //extract methods of class and add to json
                map.put(v.name, v.methods.stream().map(e -> {
                            Map<String, Object> method = new HashMap<>();
                            method.put("name", e.name);
                            method.put("access", getAccessType(e.access));
                            method.put("description", e.desc);
                            method.put("signature", e.signature);
                            method.put("params", Arrays.stream(e.desc.substring(1, e.desc.lastIndexOf(")")).split(";")).filter(e1 -> !e1.isEmpty()).collect(Collectors.toSet()));
                            method.put("returnType", e.desc.substring(e.desc.lastIndexOf(")") + 1));






                            method.put("exceptions", e.exceptions);
                            method.put("attributes", e.attrs);
                            Map<String, List> annotations = new HashMap<>();
                            if (e.visibleAnnotations != null) {
                                annotations.put("visible_annotations", e.visibleAnnotations.stream().map(annotationNode -> {
                                    if (annotationNode != null) {
                                        System.out.println(annotationNode.desc);
                                        System.out.println(annotationNode.values);
                                        return entry(annotationNode.desc,
                                                annotationNode.values == null ? Collections.EMPTY_LIST : annotationNode.values);
                                    }
                                    return null;
                                }).filter(Objects::nonNull).collect(Collectors.toList()));
                            }
                            if (e.invisibleAnnotations != null) {
                                annotations.put("hidden_annotation", e.invisibleAnnotations.stream().map(annotationNode -> {

                                    if (annotationNode != null) {
                                        return entry(annotationNode.desc,
                                                annotationNode.values == null ? Collections.EMPTY_LIST : annotationNode.values);
                                    }
                                    return null;
                                }).filter(Objects::nonNull).collect(Collectors.toList()));
                            }
                            method.put("annotations", annotations);

                            return method;
                        }
                ).collect(Collectors.toList()));


                //write it


            });
            mapper.writeValue(new File(destDir + "/" + "output" + ".json"), json);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String getAccessType(int access) {

        return Optcodes.OptCode.getAllMethodOptcodes().stream()
                .filter(e -> (access & e.getOptCode()) != 0)
                .map(Optcodes.OptCode::getName)
                .collect(Collectors.joining(" "));

    }
}
