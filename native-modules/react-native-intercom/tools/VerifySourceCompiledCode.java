import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;

/** Compare compiler output to restored output. Inputs are owned source classes and text symbols only. */
public final class VerifySourceCompiledCode {
    static Map<String,ClassNode> read(Path folder)throws Exception {
        Map<String,ClassNode> result=new TreeMap<>();
        try(var walk=Files.walk(folder)){for(Path p:walk.filter(x->x.toString().endsWith(".class")).toList()){ClassNode n=new ClassNode();new ClassReader(Files.readAllBytes(p)).accept(n,0);result.put(n.name,n);}}
        return result;
    }
    static Object frame(Object value,Map<LabelNode,Integer> labels) {
        if(value instanceof LabelNode label)return "UNINITIALIZED_NEW_AT:"+labels.get(label);
        if(value instanceof List<?> list)return list.stream().map(x->frame(x,labels)).toList();
        return value;
    }
    static String code(MethodNode method) {
        Map<LabelNode,Integer> labels=new IdentityHashMap<>();int offset=0;
        for(AbstractInsnNode n:method.instructions){if(n instanceof LabelNode l)labels.put(l,offset);else if(n.getOpcode()>=0)offset++;}
        StringBuilder result=new StringBuilder(ArtifactFingerprint.of(method)).append("|max:").append(method.maxStack).append(',').append(method.maxLocals);
        for(AbstractInsnNode n:method.instructions)if(n instanceof FrameNode f)result.append("|frame:").append(f.type).append(':').append(frame(f.local,labels)).append(':').append(frame(f.stack,labels));
        for(TryCatchBlockNode h:method.tryCatchBlocks)result.append("|handler:").append(labels.get(h.start)).append(':').append(labels.get(h.end)).append(':').append(labels.get(h.handler)).append(':').append(h.type);
        return result.toString();
    }
    static boolean emptyConstructor(MethodNode method) {
        List<AbstractInsnNode> c=Arrays.stream(method.instructions.toArray()).filter(n->n.getOpcode()>=0).toList();
        return method.name.equals("<init>")&&method.desc.equals("()V")&&method.tryCatchBlocks.isEmpty()&&c.size()==3
            &&c.get(0) instanceof VarInsnNode load&&load.getOpcode()==Opcodes.ALOAD&&load.var==0
            &&c.get(1) instanceof MethodInsnNode call&&call.getOpcode()==Opcodes.INVOKESPECIAL&&call.owner.equals("java/lang/Object")&&call.name.equals("<init>")&&call.desc.equals("()V")
            &&c.get(2).getOpcode()==Opcodes.RETURN;
    }
    static boolean constructorReference(Object value,Set<String> owners) {
        if(value instanceof Handle h)return h.getName().equals("<init>")&&owners.contains(h.getOwner());
        if(value instanceof ConstantDynamic d){if(constructorReference(d.getBootstrapMethod(),owners))return true;for(int i=0;i<d.getBootstrapMethodArgumentCount();i++)if(constructorReference(d.getBootstrapMethodArgument(i),owners))return true;}
        return false;
    }
    public static void main(String[] args)throws Exception {
        Map<String,String> types=new HashMap<>(),owned=new HashMap<>(),global=new HashMap<>();
        for(String line:Files.readAllLines(Path.of(args[2]))) {
            String[] r=line.split("\t",-1);
            if(r[0].equals("C"))types.put(r[1],r[2]);
            else {owned.put(r[0]+"\t"+r[1]+"\t"+r[2]+"\t"+r[3],r[4]);if(r[4].startsWith("recovered$"))global.put(r[0]+"\t"+r[2]+"\t"+r[3],r[4]);}
        }
        Remapper forward=new Remapper() {
            @Override public String map(String name){return types.getOrDefault(name,name);}
            @Override public String mapMethodName(String owner,String name,String descriptor){return owned.getOrDefault("M\t"+owner+"\t"+name+"\t"+descriptor,global.getOrDefault("M\t"+name+"\t"+descriptor,name));}
            @Override public String mapFieldName(String owner,String name,String descriptor){return owned.getOrDefault("F\t"+owner+"\t"+name+"\t"+descriptor,global.getOrDefault("F\t"+name+"\t"+descriptor,name));}
        };
        Set<String> alreadyOriginal=new HashSet<>();
        for(String file:args[6].split(","))for(String line:Files.readAllLines(Path.of(file))){String[] r=line.split("\t",-1);if(r[0].equals("C"))alreadyOriginal.add(r[1]);}
        Map<String,ClassNode> before=read(Path.of(args[0])),after=read(Path.of(args[1]));
        List<String> failures=new ArrayList<>(),removed=new ArrayList<>();int methods=0,handlers=0;
        Set<String> declaredRemovals=new HashSet<>();
        for(String line:Files.readAllLines(Path.of(args[3]))){String[] r=line.split("\t",-1);if(r[0].equals("REMOVE_JAVA_IMPLICIT_CONSTRUCTOR"))declaredRemovals.add(types.getOrDefault(r[1],r[1])+"\t<init>"+r[2]);}
        for(String line:Files.readAllLines(Path.of(args[4]))){String[] r=line.split("\t",-1);if(r[0].equals("REMOVE_JAVAC_COVARIANT_INTERFACE_BRIDGE"))declaredRemovals.add(r[1]+"\t"+r[2]);}
        Set<String> actualRemovals=new HashSet<>(),removedConstructorOwners=new HashSet<>();
        for(ClassNode output:after.values()) {
            ClassNode canonical=output;
            if(!alreadyOriginal.contains(output.name)){canonical=new ClassNode();output.accept(new ClassRemapper(canonical,forward));}
            ClassNode original=before.remove(canonical.name);
            if(original==null){failures.add("ADDED_CLASS\t"+canonical.name);continue;}
            if(((original.access^canonical.access)&(Opcodes.ACC_INTERFACE|Opcodes.ACC_ANNOTATION|Opcodes.ACC_ENUM))!=0)failures.add("CLASS_KIND_CHANGED\t"+canonical.name);
            Map<String,FieldNode> rawFields=new HashMap<>();for(FieldNode field:original.fields)rawFields.put(field.name+field.desc,field);
            for(FieldNode field:canonical.fields){FieldNode raw=rawFields.get(field.name+field.desc);if(raw==null||((raw.access^field.access)&Opcodes.ACC_STATIC)!=0)failures.add("FIELD_STATIC_CHANGED\t"+canonical.name+"\t"+field.name+field.desc);}
            Map<String,MethodNode> old=new TreeMap<>();for(MethodNode m:original.methods)old.put(m.name+m.desc,m);
            for(MethodNode current:canonical.methods) {
                MethodNode previous=old.remove(current.name+current.desc);
                if(previous==null || !code(previous).equals(code(current)))failures.add("CODE_FRAME_MAX_HANDLER_CHANGED\t"+canonical.name+"\t"+current.name+current.desc);
                if(previous!=null && ((previous.access^current.access)&(Opcodes.ACC_STATIC|Opcodes.ACC_ABSTRACT|Opcodes.ACC_NATIVE))!=0)failures.add("METHOD_INVOCATION_KIND_CHANGED\t"+canonical.name+"\t"+current.name+current.desc);
                methods++;handlers+=current.tryCatchBlocks.size();
            }
            for(MethodNode missing:old.values()) {
                String key=canonical.name+"\t"+missing.name+missing.desc;
                if(!declaredRemovals.contains(key))failures.add("UNDECLARED_METHOD_REMOVAL\t"+key);
                else if(missing.name.equals("<init>")) {
                    if(!emptyConstructor(missing))failures.add("REMOVED_NONEMPTY_CONSTRUCTOR\t"+key);
                    removedConstructorOwners.add(canonical.name);
                } else if(!canonical.name.equals("io/intercom/android/sdk/m5/upload/data/MediaData$Media") || !missing.name.equals("getDataSource") || !missing.desc.equals("()Ljava/lang/Object;"))failures.add("UNEXPECTED_COMPILER_REMOVAL\t"+key);
                actualRemovals.add(key);removed.add(key);
            }
        }
        for(String missing:before.keySet())failures.add("REMOVED_CLASS\t"+missing);
        if(!declaredRemovals.equals(actualRemovals))failures.add("COMPILER_REMOVAL_LEDGER_MISMATCH");
        // Re-read all raw code to include constructor handles, not only explicit NEW/invokespecial.
        for(ClassNode node:read(Path.of(args[0])).values())for(MethodNode method:node.methods)for(AbstractInsnNode n:method.instructions) {
            boolean referenced=n instanceof MethodInsnNode call&&call.name.equals("<init>")&&removedConstructorOwners.contains(call.owner)
                ||n instanceof TypeInsnNode allocation&&allocation.getOpcode()==Opcodes.NEW&&removedConstructorOwners.contains(allocation.desc)
                ||n instanceof LdcInsnNode constant&&constructorReference(constant.cst,removedConstructorOwners);
            if(n instanceof InvokeDynamicInsnNode indy){referenced|=constructorReference(indy.bsm,removedConstructorOwners);for(Object argument:indy.bsmArgs)referenced|=constructorReference(argument,removedConstructorOwners);}
            if(referenced)failures.add("REFERENCE_TO_REMOVED_CONSTRUCTOR\t"+node.name+"\t"+method.name+method.desc);
        }
        Path report=Path.of(args[5]);Files.write(report,failures);Files.write(report.resolveSibling("verified-compiler-removals.tsv"),removed);
        System.out.println("Source classes="+after.size()+" retained methods="+methods+" handlers="+handlers+"; Code/frames/max/handler differences="+failures.size()+"; declared compiler removals="+removed.size()+"; no original SDK inputs");
        if(!failures.isEmpty())throw new IllegalStateException("Source Code preservation failed");
    }
}
