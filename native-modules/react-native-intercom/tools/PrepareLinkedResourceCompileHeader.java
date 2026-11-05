import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Compile declarations from the caller's real AGP resource link; never SDK implementation. */
public final class PrepareLinkedResourceCompileHeader {
    public static void main(String[] args) throws Exception {
        Set<String> namespaces = Set.of("io/intercom/android/sdk/R", "io/intercom/android/sdk/ui/R", "androidx/compose/ui/R");
        Set<String> found = new HashSet<>();
        int count = 0;
        try (ZipFile input = new ZipFile(args[0]); ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))) {
            for (ZipEntry entry : Collections.list(input.entries())) {
                if (!entry.getName().endsWith(".class")) continue;
                ClassNode node = new ClassNode();
                new ClassReader(input.getInputStream(entry)).accept(node, 0);
                String owner = node.name.split("\\$", 2)[0];
                if (!namespaces.contains(owner)) continue;
                found.add(owner);
                for (FieldNode field : node.fields) {
                    field.access &= ~Opcodes.ACC_FINAL;
                    field.value = null;
                }
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                ZipEntry copy = new ZipEntry(node.name + ".class");
                copy.setTime(0);
                output.putNextEntry(copy);
                output.write(writer.toByteArray());
                output.closeEntry();
                count++;
            }
        }
        if (!found.equals(namespaces)) throw new IllegalStateException("Incomplete linked resource namespaces: " + found);
        System.out.println("Own AGP linked R declarations=" + count + "; no final/ConstantValue; excluded from SDK output");
    }
}
