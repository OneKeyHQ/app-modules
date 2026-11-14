import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

/** Normalize JVM identifiers for Java source recovery; never a runtime dependency. */
public final class NormalizeJvmNames {
    private static final Set<String> KEYWORDS = Set.of(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
        "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
        "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false", "null", "_"
    );
    private static final Set<String> ALIASES = new TreeSet<>();
    private static final Map<String, String> REVERSE = new HashMap<>();

    private static boolean legal(String name) {
        if (name.isEmpty() || KEYWORDS.contains(name) || !Character.isJavaIdentifierStart(name.charAt(0))) return false;
        for (int i = 1; i < name.length(); i++) if (!Character.isJavaIdentifierPart(name.charAt(i))) return false;
        return true;
    }

    private static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 8; i++) result.append(String.format("%02x", digest[i]));
            return result.toString();
        } catch (Exception error) { throw new IllegalStateException(error); }
    }

    private static String type(String name) {
        int slash = name.lastIndexOf('/');
        int dollar = name.lastIndexOf('$');
        String mapped;
        if (dollar > slash) {
            String inner = name.substring(dollar + 1);
            boolean anonymousIndex = inner.matches("[0-9]+");
            mapped = type(name.substring(0, dollar)) + "$" +
                (legal(inner) || anonymousIndex ? inner : "RecoveredInner" + hash(inner));
        } else {
            String simple = name.substring(slash + 1);
            mapped = name.substring(0, slash + 1) +
                (legal(simple) ? simple : "RecoveredType" + hash(simple));
        }
        if (!name.equals(mapped)) ALIASES.add("C\t" + name + "\t" + mapped);
        return mapped;
    }

    private static String member(String kind, String owner, String name, String descriptor) {
        if (name.equals("<init>") || name.equals("<clinit>") || legal(name)) return name;
        // A global name+descriptor alias also covers inherited external Kotlin value-class methods.
        String alias = "recovered$" + hash(kind + "\n" + name + "\n" + descriptor);
        String key = kind + "\n" + alias + "\n" + descriptor;
        String previous = REVERSE.putIfAbsent(key, name);
        if (previous != null && !previous.equals(name)) throw new IllegalStateException("Alias collision");
        ALIASES.add(String.join("\t", kind, owner, name, descriptor, alias));
        return alias;
    }

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args[0]);
        Files.createDirectories(output);
        Remapper remapper = new Remapper() {
            @Override public String map(String name) { return type(name); }
            @Override public String mapMethodName(String owner, String name, String descriptor) {
                return member("M", owner, name, descriptor);
            }
            @Override public String mapFieldName(String owner, String name, String descriptor) {
                return member("F", owner, name, descriptor);
            }
        };
        for (int i = 1; i < args.length; i++) {
            Path input = Path.of(args[i]);
            try (ZipFile zip = new ZipFile(input.toFile());
                 ZipOutputStream target = new ZipOutputStream(Files.newOutputStream(output.resolve(input.getFileName())))) {
                List<? extends ZipEntry> entries = Collections.list(zip.entries());
                entries.sort(java.util.Comparator.comparing(ZipEntry::getName));
                for (ZipEntry entry : entries) {
                    if (entry.isDirectory()) continue;
                    byte[] data = zip.getInputStream(entry).readAllBytes();
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        ClassWriter writer = new ClassWriter(0);
                        new ClassReader(data).accept(new ClassRemapper(writer, remapper), 0);
                        data = writer.toByteArray();
                        if (!input.getFileName().toString().contains("-reference")) {
                            // javac hides Kotlin synthetic default constructors/methods. This is a
                            // compile-only view of external dependencies, never packaged output.
                            ClassNode external = new ClassNode();
                            new ClassReader(data).accept(external, 0);
                            external.access &= ~Opcodes.ACC_SYNTHETIC;
                            external.signature = null;
                            // Recovered Nexus sources intentionally use flat binary class names.
                            // Their runtime JAR retains the original nested declaration metadata.
                            if (input.getFileName().toString().equals("nexus-twig-source.jar")) {
                                external.innerClasses.clear();
                                external.outerClass = null;
                                external.outerMethod = null;
                                external.outerMethodDesc = null;
                            }
                            // Kotlin property annotations are emitted on synthetic JVM annotation
                            // methods although Java's annotation target cannot express PROPERTY.
                            // Remove only the compile-view restriction, retaining SDK annotations.
                            if (external.name.equals("kotlinx/serialization/SerialName")) {
                                if (external.visibleAnnotations != null) external.visibleAnnotations.removeIf(annotation -> annotation.desc.equals("Ljava/lang/annotation/Target;"));
                                if (external.invisibleAnnotations != null) external.invisibleAnnotations.removeIf(annotation -> annotation.desc.equals("Ljava/lang/annotation/Target;"));
                            }
                            for (FieldNode field : external.fields) { field.access &= ~Opcodes.ACC_SYNTHETIC; field.signature = null; }
                            for (MethodNode method : external.methods) {
                                // Android's ordinary covariant Java bridges must remain hidden;
                                // exposing all return-only overloads makes setDuration ambiguous.
                                boolean platformBridge = input.getFileName().toString().equals("android.jar") && (method.access & Opcodes.ACC_BRIDGE) != 0;
                                if (!platformBridge) method.access &= ~(Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE);
                                method.signature = null;
                                method.exceptions.clear();
                            }
                            ClassWriter visible = new ClassWriter(0);
                            external.accept(visible);
                            data = visible.toByteArray();
                        }
                        name = type(name.substring(0, name.length() - 6)) + ".class";
                    }
                    ZipEntry replacement = new ZipEntry(name);
                    replacement.setTime(0);
                    target.putNextEntry(replacement);
                    target.write(data);
                    target.closeEntry();
                }
            }
        }
        Files.write(output.resolve("identifier-aliases.tsv"), ALIASES, StandardCharsets.UTF_8);
        System.out.println("Recorded " + ALIASES.size() + " reversible identifier aliases");
    }
}
