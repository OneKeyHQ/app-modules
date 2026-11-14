import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Reject unsupported class features/private access before removing inert source compiler nest metadata. */
public final class FinishAndroidClassMetadata {
    record Member(String owner,int access) {}
    static final Map<String,ClassNode> classes=new TreeMap<>();
    static final List<String> failures=new ArrayList<>(), records=new ArrayList<>();
    static Member resolve(String owner,String name,String desc,boolean field,Set<String> visited) {
        if(!visited.add(owner))return null;
        ClassNode node=classes.get(owner);if(node==null)return null;
        if(field)for(FieldNode member:node.fields)if(member.name.equals(name)&&member.desc.equals(desc))return new Member(owner,member.access);
        if(!field)for(MethodNode member:node.methods)if(member.name.equals(name)&&member.desc.equals(desc))return new Member(owner,member.access);
        if(name.equals("<init>"))return null;
        if(field)for(String itf:node.interfaces){Member found=resolve(itf,name,desc,true,visited);if(found!=null)return found;}
        if(node.superName!=null){Member found=resolve(node.superName,name,desc,field,visited);if(found!=null)return found;}
        if(!field)for(String itf:node.interfaces){Member found=resolve(itf,name,desc,false,visited);if(found!=null)return found;}
        return null;
    }
    static void access(String caller,String method,String owner,String name,String desc,boolean field) {
        Member target=resolve(owner,name,desc,field,new HashSet<>());
        if(target!=null && !target.owner.equals(caller) && (target.access&Opcodes.ACC_PRIVATE)!=0)
            failures.add("PRIVATE_CROSS\t"+caller+"\t"+method+"\t"+owner+"\t"+name+desc+"\tDECLARED:"+target.owner);
    }
    static void constant(String caller,String method,Object value) {
        if(value instanceof ConstantDynamic dynamic){failures.add("CONSTANT_DYNAMIC\t"+caller+"\t"+method);constant(caller,method,dynamic.getBootstrapMethod());for(int i=0;i<dynamic.getBootstrapMethodArgumentCount();i++)constant(caller,method,dynamic.getBootstrapMethodArgument(i));}
        if(value instanceof Handle handle)access(caller,method,handle.getOwner(),handle.getName(),handle.getDesc(),handle.getTag()<=4);
    }
    public static void main(String[] args)throws Exception {
        Path input=Path.of(args[0]),output=Path.of(args[1]);
        try(var walk=Files.walk(input)){for(Path path:walk.filter(p->p.toString().endsWith(".class")).sorted().toList()){ClassNode n=new ClassNode();new ClassReader(Files.readAllBytes(path)).accept(n,0);if(classes.put(n.name,n)!=null)throw new IllegalStateException("Duplicate class owner");}}
        int indyCount=0,nestHosts=0,nestMembers=0;
        for(ClassNode node:classes.values()) {
            if(node.version!=Opcodes.V1_8 && node.version!=Opcodes.V1_7)failures.add("VERSION\t"+node.name+"\t"+node.version);
            if(node.recordComponents!=null || (node.access&Opcodes.ACC_RECORD)!=0)failures.add("RECORD\t"+node.name);
            if(node.permittedSubclasses!=null)failures.add("PERMITTED_SUBCLASSES\t"+node.name);
            if((node.access&Opcodes.ACC_MODULE)!=0 || node.module!=null)failures.add("MODULE\t"+node.name);
            for(MethodNode method:node.methods) {
                String caller=method.name+method.desc;
                if((node.access&Opcodes.ACC_INTERFACE)!=0 && (method.access&Opcodes.ACC_PRIVATE)!=0)failures.add("PRIVATE_INTERFACE_METHOD\t"+node.name+"\t"+caller);
                for(AbstractInsnNode instruction:method.instructions) {
                    if(instruction instanceof MethodInsnNode call)access(node.name,caller,call.owner,call.name,call.desc,false);
                    if(instruction instanceof FieldInsnNode field)access(node.name,caller,field.owner,field.name,field.desc,true);
                    if(instruction instanceof LdcInsnNode ldc)constant(node.name,caller,ldc.cst);
                    if(instruction instanceof InvokeDynamicInsnNode indy) {
                        indyCount++;
                        if(!indy.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")||!indy.bsm.getName().equals("metafactory")||indy.bsmArgs.length!=3
                           || !(indy.bsmArgs[0] instanceof Type a)||a.getSort()!=Type.METHOD||!(indy.bsmArgs[1] instanceof Handle)
                           || !(indy.bsmArgs[2] instanceof Type b)||b.getSort()!=Type.METHOD)failures.add("BOOTSTRAP\t"+node.name+"\t"+caller+"\t"+indy.bsm);
                        constant(node.name,caller,indy.bsm);for(Object argument:indy.bsmArgs)constant(node.name,caller,argument);
                    }
                }
            }
        }
        Files.write(Path.of(args[2]),failures);
        if(!failures.isEmpty())throw new IllegalStateException("Android class feature/private access gate failed: "+failures.size());
        Files.createDirectories(output);
        for(ClassNode node:classes.values()) {
            if(node.nestHostClass!=null){records.add("REMOVE_INERT_NEST_HOST\t"+node.name+"\t"+node.nestHostClass);nestHosts++;}
            if(node.nestMembers!=null)for(String member:node.nestMembers){records.add("REMOVE_INERT_NEST_MEMBER\t"+node.name+"\t"+member);nestMembers++;}
            node.nestHostClass=null;node.nestMembers=null;
            ClassWriter writer=new ClassWriter(0);node.accept(writer);Path target=output.resolve(node.name+".class");Files.createDirectories(target.getParent());Files.write(target,writer.toByteArray());
        }
        Files.write(Path.of(args[3]),records);
        System.out.println("Classes="+classes.size()+"; original Java7/8 class versions/no preview; standard Java8 lambda sites="+indyCount+"; private cross=0; private interface=0; Condy/record/sealed/module/unknown bootstrap=0; inert nest hosts removed="+nestHosts+" members="+nestMembers);
    }
}
