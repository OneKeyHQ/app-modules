import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Reference-only metadata adaptation for Kotlin's non-synthetic capturing static lambda bodies. */
public final class MarkKotlinLambdaBodies {
    public static void main(String[] args) throws Exception {
        Map<String,ClassNode> classes=new TreeMap<>();
        try (ZipFile input=new ZipFile(args[0])) {
            for (ZipEntry entry : Collections.list(input.entries())) if (entry.getName().endsWith(".class")) {
                ClassNode node=new ClassNode();new ClassReader(input.getInputStream(entry)).accept(node,0);classes.put(node.name,node);
            }
        }
        Set<String> marked=new TreeSet<>();
        for (ClassNode node : classes.values()) for (MethodNode method : node.methods) for (AbstractInsnNode instruction : method.instructions) {
            if (!(instruction instanceof InvokeDynamicInsnNode site) || !site.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory") || Type.getArgumentTypes(site.desc).length==0) continue;
            Handle target=(Handle)site.bsmArgs[1];ClassNode owner=classes.get(target.getOwner());
            if (target.getTag()!=Opcodes.H_INVOKESTATIC || owner==null) continue;
            for (MethodNode body : owner.methods) if (body.name.equals(target.getName()) && body.desc.equals(target.getDesc()) && (body.access&Opcodes.ACC_PRIVATE)!=0 && (body.access&Opcodes.ACC_STATIC)!=0) {
                body.access|=Opcodes.ACC_SYNTHETIC;marked.add(owner.name+"\t"+body.name+"\t"+body.desc);
            }
        }
        try (ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))) {
            for (ClassNode node : classes.values()) {
                ClassWriter writer=new ClassWriter(0);node.accept(writer);output.putNextEntry(new ZipEntry(node.name+".class"));output.write(writer.toByteArray());output.closeEntry();
            }
        }
        Files.write(Path.of(args[2]),marked);System.out.println("Marked reference-only lambda bodies: "+marked.size());
    }
}
