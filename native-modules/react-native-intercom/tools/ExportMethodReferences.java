import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;

/** Export complete descriptor-specific call/handle references for source symbol restoration. */
public final class ExportMethodReferences {
    public static void main(String[] args)throws Exception {
        Set<String> targets=new HashSet<>(Files.readAllLines(Path.of(args[0])));List<String> output=new ArrayList<>();
        for(int input=2;input<args.length;input++)try(ZipFile archive=new ZipFile(args[input])) {
            for(ZipEntry entry:Collections.list(archive.entries()))if(entry.getName().endsWith(".class")) {
                ClassReader reader=new ClassReader(archive.getInputStream(entry));String caller=reader.getClassName();
                reader.accept(new ClassVisitor(Opcodes.ASM9) {
                    @Override public MethodVisitor visitMethod(int access,String name,String descriptor,String signature,String[] exceptions) {
                        String enclosing=caller+"\t"+name+"\t"+descriptor;
                        return new MethodVisitor(Opcodes.ASM9) {
                            void handle(String kind,Handle reference) {
                                String target=reference.getOwner()+"\t"+reference.getName()+"\t"+reference.getDesc();
                                if(targets.contains(target))output.add(kind+"\t"+enclosing+"\t"+target+"\t"+reference.getTag());
                            }
                            void constant(Object value) {
                                if(value instanceof Handle reference)handle("HANDLE",reference);
                                else if(value instanceof ConstantDynamic dynamic){handle("CONDY_BOOTSTRAP",dynamic.getBootstrapMethod());for(int i=0;i<dynamic.getBootstrapMethodArgumentCount();i++)constant(dynamic.getBootstrapMethodArgument(i));}
                            }
                            @Override public void visitMethodInsn(int opcode,String owner,String method,String desc,boolean isInterface) {
                                String target=owner+"\t"+method+"\t"+desc;if(targets.contains(target))output.add("CALL\t"+enclosing+"\t"+target+"\t"+opcode);
                            }
                            @Override public void visitInvokeDynamicInsn(String name,String descriptor,Handle bootstrap,Object... arguments){handle("INDY_BOOTSTRAP",bootstrap);for(Object argument:arguments)constant(argument);}
                            @Override public void visitLdcInsn(Object value){constant(value);}
                        };
                    }
                },ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
            }
        }
        Collections.sort(output);Files.write(Path.of(args[1]),output);System.out.println("References "+output.size());
    }
}
