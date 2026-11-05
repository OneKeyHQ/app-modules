import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Prove reference-return bridges are pure same-argument calls before requesting javac regeneration. */
public final class ExportCovariantBridges {
    static String arguments(String descriptor){return descriptor.substring(0,descriptor.indexOf(')')+1);}
    static boolean reference(Type type){return type.getSort()==Type.OBJECT || type.getSort()==Type.ARRAY;}
    public static void main(String[] args)throws Exception {
        List<String> rows=new ArrayList<>(), rejectedRows=new ArrayList<>();int rejected=0;
        for(int input=1;input<args.length;input++)try(ZipFile archive=new ZipFile(args[input])) {
            for(ZipEntry entry:Collections.list(archive.entries()))if(entry.getName().endsWith(".class")) {
                ClassNode node=new ClassNode();new ClassReader(archive.getInputStream(entry)).accept(node,0);
                for(MethodNode bridge:node.methods) {
                    if((bridge.access&Opcodes.ACC_BRIDGE)==0 || !reference(Type.getReturnType(bridge.desc)))continue;
                    List<MethodNode> targets=node.methods.stream().filter(method->(method.access&Opcodes.ACC_BRIDGE)==0 && method.name.equals(bridge.name) && arguments(method.desc).equals(arguments(bridge.desc)) && reference(Type.getReturnType(method.desc)) && !method.desc.equals(bridge.desc)).toList();
                    if(targets.size()!=1)continue;MethodNode target=targets.get(0);
                    List<AbstractInsnNode> code=new ArrayList<>();for(AbstractInsnNode instruction:bridge.instructions)if(instruction.getOpcode()>=0)code.add(instruction);
                    Type[] parameters=Type.getArgumentTypes(bridge.desc);boolean pure=(bridge.access&Opcodes.ACC_STATIC)==0 && bridge.tryCatchBlocks.isEmpty() && code.size()==parameters.length+3;
                    int cursor=0,slot=1;
                    if(pure)pure=code.get(cursor++) instanceof VarInsnNode load && load.getOpcode()==Opcodes.ALOAD && load.var==0;
                    for(Type parameter:parameters) {
                        if(pure)pure=code.get(cursor++) instanceof VarInsnNode load && load.getOpcode()==parameter.getOpcode(Opcodes.ILOAD) && load.var==slot;
                        slot+=parameter.getSize();
                    }
                    if(pure)pure=code.get(cursor++) instanceof MethodInsnNode call && call.getOpcode()==Opcodes.INVOKEVIRTUAL && call.owner.equals(node.name) && call.name.equals(target.name) && call.desc.equals(target.desc) && !call.itf;
                    if(pure)pure=code.get(cursor).getOpcode()==Opcodes.ARETURN;
                    if(!pure){rejected++;rejectedRows.add(String.join("\t",node.name,bridge.name,bridge.desc,target.desc,"UNRESTORED: does not match strict same-argument pure forwarder or contains handlers"));continue;}
                    rows.add(String.join("\t",node.name,bridge.name,bridge.desc,target.desc,"ALOAD0; ordered exact argument loads; INVOKEVIRTUAL same-owner typed target; ARETURN; no handlers"));
                }
            }
        }
        Collections.sort(rows);Collections.sort(rejectedRows);Files.write(Path.of(args[0]),rows);Files.write(Path.of(args[0]+".unproven.tsv"),rejectedRows);System.out.println("Proven covariant bridges "+rows.size()+", rejected non-pure "+rejected);
    }
}
